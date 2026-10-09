package dev.snipme.highlights.internal.locator

import dev.snipme.highlights.model.PhraseLocation
import kotlin.test.Test
import kotlin.test.assertEquals

internal class FunctionCallLocatorTest {

    @Test
    fun `Returns empty set for code without function calls`() {
        val testCode = "val scope: CoroutineScope"

        val result = FunctionCallLocator.locate(testCode, setOf())

        assertEquals(emptySet(), result)
    }

    @Test
    fun `Returns location of invoked function`() {
        val testCode = "scope.launch { }"
        val expectedResult = setOf(PhraseLocation(6, 12))

        val result = FunctionCallLocator.locate(testCode, setOf())

        assertEquals(expectedResult, result)
    }

    @Test
    fun `Returns location of callable reference`() {
        val testCode = "stateStore::updateArticles"
        val expectedResult = setOf(PhraseLocation(12, 26))

        val result = FunctionCallLocator.locate(testCode, setOf())

        assertEquals(expectedResult, result)
    }

    @Test
    fun `Returns location of all invocations`() {
        val testCode = "repo.getArticles(source).onSuccess(store::save)"
        val expectedResult = setOf(
            PhraseLocation(5, 16),
            PhraseLocation(25, 34),
            PhraseLocation(42, 46),
        )

        val result = FunctionCallLocator.locate(testCode, setOf())

        assertEquals(expectedResult, result)
    }

    @Test
    fun `Skips keyword and returns following invocation`() {
        val testCode = "if (check())"
        val keywords = setOf("if")
        val expectedResult = setOf(PhraseLocation(4, 9))

        val result = FunctionCallLocator.locate(testCode, keywords)

        assertEquals(expectedResult, result)
    }

    @Test
    fun `Not returns location of name inside ignored range`() {
        val testCode = "// launch(shown as text)"

        val result = FunctionCallLocator.locate(
            testCode,
            setOf(),
            ignoreRanges = setOf(IntRange(0, 23)),
        )

        assertEquals(emptySet(), result)
    }

    @Test
    fun `Not returns location of lowercase part of identifier`() {
        val testCode = "val stateStore = createStore()"

        val result = FunctionCallLocator.locate(testCode, setOf("createStore"))

        assertEquals(emptySet(), result)
    }
}
