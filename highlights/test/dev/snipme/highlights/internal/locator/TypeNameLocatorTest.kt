package dev.snipme.highlights.internal.locator

import dev.snipme.highlights.model.PhraseLocation
import kotlin.test.Test
import kotlin.test.assertEquals

internal class TypeNameLocatorTest {

    @Test
    fun `Returns empty set for code without uppercase tokens`() {
        val testCode = "val sum = 1 + 2"

        val result = TypeNameLocator.locate(testCode, setOf())

        assertEquals(emptySet(), result)
    }

    @Test
    fun `Returns location of type name in declaration`() {
        val testCode = "class HomeSourcesController"
        val expectedResult = setOf(PhraseLocation(6, 27))

        val result = TypeNameLocator.locate(testCode, setOf())

        assertEquals(expectedResult, result)
    }

    @Test
    fun `Returns location of type reference after colon`() {
        val testCode = "private val scope: CoroutineScope"
        val expectedResult = setOf(PhraseLocation(19, 33))

        val result = TypeNameLocator.locate(testCode, setOf())

        assertEquals(expectedResult, result)
    }

    @Test
    fun `Returns location of constructor call`() {
        val testCode = "val controller = HomeSourcesController("
        val expectedResult = setOf(PhraseLocation(17, 38))

        val result = TypeNameLocator.locate(testCode, setOf())

        assertEquals(expectedResult, result)
    }

    @Test
    fun `Returns location of nullable type reference`() {
        val testCode = "source: SourceItem?"
        val expectedResult = setOf(PhraseLocation(8, 19))

        val result = TypeNameLocator.locate(testCode, setOf())

        assertEquals(expectedResult, result)
    }

    @Test
    fun `Not returns location of capitalized word inside ignored range`() {
        val testCode = "val text = \"Coroutine inside\""

        val result = TypeNameLocator.locate(
            testCode,
            setOf(),
            ignoreRanges = setOf(IntRange(11, 28)),
        )

        assertEquals(emptySet(), result)
    }

    @Test
    fun `Not returns location of uppercase keyword`() {
        val testCode = "Self value"
        val keywords = setOf("Self")

        val result = TypeNameLocator.locate(testCode, keywords)

        assertEquals(emptySet(), result)
    }

    @Test
    fun `Not returns location of capitalized part of identifier`() {
        val testCode = "val scopeName = readLine()"

        val result = TypeNameLocator.locate(testCode, setOf())

        assertEquals(emptySet(), result)
    }
}
