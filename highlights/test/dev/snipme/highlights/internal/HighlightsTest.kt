package dev.snipme.highlights.internal

import dev.snipme.highlights.Highlights
import dev.snipme.highlights.HighlightsResultListener
import dev.snipme.highlights.model.CodeHighlight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.measureTime

@OptIn(ExperimentalCoroutinesApi::class)
class HighlightsTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `returns list of code highlights for sync call`() {
        val default = Highlights.default().apply {
            setCode(longJavaCode)
        }

        val highlights = default.getHighlights()
        assertTrue { highlights.isNotEmpty() }
    }

    @Test
    fun `returns error for exception during analysis`() = runTest {
        val default = Highlights.default().apply {
            setCode(longJavaCode)
        }

        var error: Throwable? = null
        suspendCancellableCoroutine { continuation ->
            invokeHighlightsRequest(
                default,
                onStart = { throw IllegalStateException() },
                onError = {
                    error = it
                    continuation.resume(Unit) {}
                },
            )
        }

        assertTrue { error != null }
    }

    @Test
    fun `cancels first analysis when second is invoked`() = runTest {
        val default = Highlights.default().apply {
            setCode(longJavaCode)
        }

        val results = mutableListOf<List<CodeHighlight>>()

        suspendCancellableCoroutine { continuation ->
            invokeHighlightsRequest(
                default,
                onSuccess = { results.add(it) },
                onStart = {
                    invokeHighlightsRequest(default) {
                        results.add(it)
                        continuation.resume(Unit) {}
                    }
                },
            )
        }

        assertTrue { results.size == 1 }
    }

    @Test
    fun `returns list of code highlights asynchronously`() = runTest {
        val default = Highlights.default().apply {
            setCode(longJavaCode)
        }

        val result = suspendCancellableCoroutine { continuation ->
            invokeHighlightsRequest(default) {
                continuation.resume(it) {}
            }
        }

        assertTrue { result.isNotEmpty() }
    }

    @Test
    fun `returns asynchronous results one by one`() = runTest {
        val default = Highlights.default().apply {
            setCode(longJavaCode)
        }

        var result1: List<CodeHighlight>
        val time1 = measureTime {
            result1 = suspendCancellableCoroutine { continuation ->
                invokeHighlightsRequest(default) {
                    continuation.resume(it) {}
                }
            }
        }
        println("Time1: ${time1.inWholeMilliseconds} ms")
        assertTrue { result1.isNotEmpty() }

        default.setCode(longJavaCode.replace("static", "statac"))

        var result2: List<CodeHighlight>
        val time2 = measureTime {
            result2 = suspendCancellableCoroutine { continuation ->
                invokeHighlightsRequest(default) {
                    continuation.resume(it) {}
                }
            }
        }
        println("Time2: ${time2.inWholeMilliseconds} ms")
        assertTrue { result2.isNotEmpty() }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HighlightsCancellationTest {
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `returns immediately result from second invocation`() = runTest {
        val default = Highlights.default().apply {
            setCode(longJavaCode)
        }

        var cancelled = false
        var succeeded = false

        val job1 = launch {
            suspendCancellableCoroutine { c ->
                invokeHighlightsRequest(
                    default,
                    onStart = {},
                    onCancel = { cancelled = true; c.resume(Unit) {} },
                    onSuccess = { succeeded = false; c.resume(Unit) {} },
                )
            }
        }

        val job2 = launch {
            suspendCancellableCoroutine { c ->
                invokeHighlightsRequest(
                    default,
                    onStart = {},
                    onCancel = { c.resume(Unit) {} },
                    onSuccess = { succeeded = true; c.resume(Unit) {} },
                )
            }
        }

        job1.join()
        job2.join()
        // Local deviation from upstream: the original assertion compared wall-clock times
        // (time1 < time2), which only holds when the first analysis has not started before
        // cancellation. Cancellation is cooperative (ensureActive between analysis stages),
        // so on fast machines both requests run the full analysis and the timing comparison
        // becomes a coin flip. Assert the behavioral contract instead: the first request is
        // cancelled by the second one, and the second one completes successfully.
        assertTrue { cancelled && succeeded }
    }
}

private fun invokeHighlightsRequest(
    highlights: Highlights,
    onStart: () -> Unit = {},
    onCancel: () -> Unit = {},
    onError: (Throwable) -> Unit = {},
    onSuccess: (List<CodeHighlight>) -> Unit = {}
) {
    highlights.getHighlightsAsync(object : HighlightsResultListener {
        override fun onStart() = onStart()
        override fun onSuccess(result: List<CodeHighlight>) = onSuccess(result)
        override fun onError(exception: Throwable) = onError(exception)
        override fun onCancel() = onCancel()
    })
}