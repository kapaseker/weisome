package com.rocybyte.weisome.widget

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

class WeiSomeTextFieldTest {
    @Test
    /** Verifies scrolling up when the span starts above the visible window. */
    fun `scrolls up to reveal span above viewport`() {
        assertEquals(120, bringIntoViewScroll(top = 120f, bottom = 150f, scrollValue = 300, viewport = 500))
    }

    @Test
    /** Verifies scrolling down when the span ends below the visible window. */
    fun `scrolls down to reveal span below viewport`() {
        assertEquals(440, bringIntoViewScroll(top = 900f, bottom = 940f, scrollValue = 300, viewport = 500))
    }

    @Test
    /** Verifies no scroll when the span is already fully visible. */
    fun `keeps scroll when span is visible`() {
        assertNull(bringIntoViewScroll(top = 350f, bottom = 370f, scrollValue = 300, viewport = 500))
        assertNull(bringIntoViewScroll(top = 300f, bottom = 800f, scrollValue = 300, viewport = 500))
    }

    @Test
    /** Verifies layout is skipped before the viewport has been measured. */
    fun `ignores unmeasured viewport`() {
        assertNull(bringIntoViewScroll(top = 700f, bottom = 740f, scrollValue = 0, viewport = 0))
    }

    @Test
    /** Verifies a collapsed caret indents its whole line and rides along with the insertion. */
    fun `indents the caret line and shifts the caret`() {
        val result = applyLineIndent(TextFieldValue("hello\nworld", TextRange(3, 3)), indent = true)
        assertEquals("    hello\nworld", result.text)
        assertEquals(TextRange(7, 7), result.selection)
    }

    @Test
    /** Verifies a selection ending at column zero still indents that final line. */
    fun `indents the final line when the selection ends at column zero`() {
        val result = applyLineIndent(TextFieldValue("alpha\nbeta\ngamma", TextRange(0, 6)), indent = true)
        assertEquals("    alpha\n    beta\ngamma", result.text)
        assertEquals(TextRange(4, 14), result.selection)
    }

    @Test
    /** Verifies every line a multi-line selection covers is indented. */
    fun `indents every selected line`() {
        val result = applyLineIndent(TextFieldValue("a\nb\nc", TextRange(0, 5)), indent = true)
        assertEquals("    a\n    b\n    c", result.text)
        assertEquals(TextRange(4, 17), result.selection)
    }

    @Test
    /** Verifies unindent removes up to four leading spaces, dropping fewer when that is all there is. */
    fun `unindents up to four leading spaces`() {
        assertEquals("a", applyLineIndent(TextFieldValue("    a", TextRange(0, 0)), indent = false).text)
        assertEquals("a", applyLineIndent(TextFieldValue("  a", TextRange(0, 0)), indent = false).text)
    }

    @Test
    /** Verifies unindent never touches a line that has no leading spaces. */
    fun `leaves lines without leading spaces untouched`() {
        val result = applyLineIndent(TextFieldValue("a    b", TextRange(0, 0)), indent = false)
        assertEquals("a    b", result.text)
    }

    @Test
    /** Verifies a multi-line unindent remaps the selection onto the same content. */
    fun `unindents every selected line and remaps the selection`() {
        val result = applyLineIndent(TextFieldValue("    alpha\n    beta", TextRange(0, 10)), indent = false)
        assertEquals("alpha\nbeta", result.text)
        assertEquals(TextRange(0, 6), result.selection)
    }

    @Test
    /** Verifies every tab becomes four spaces and the selection is shifted by the same amounts. */
    fun `expands tabs to four spaces`() {
        val result = expandTabs(TextFieldValue("a\tb", TextRange(2, 3)))
        assertEquals("a    b", result.text)
        assertEquals(TextRange(5, 6), result.selection)
    }

    @Test
    /** Verifies a value without tabs is returned untouched instead of rebuilt. */
    fun `keeps values without tabs unchanged`() {
        val value = TextFieldValue("plain text", TextRange(4, 4))
        assertSame(value, expandTabs(value))
    }

    @Test
    /** Verifies a bare caret gets the marker pair with the caret left between them. */
    fun `inserts an inline marker pair at a bare caret`() {
        val result = toggleInlineMarker(TextFieldValue("ab", TextRange(1, 1)), "**")
        assertEquals("a****b", result.text)
        assertEquals(TextRange(3, 3), result.selection)
    }

    @Test
    /** Verifies a selection without markers is wrapped and stays covered. */
    fun `wraps an unwrapped selection`() {
        val result = toggleInlineMarker(TextFieldValue("bold", TextRange(0, 4)), "**")
        assertEquals("**bold**", result.text)
        assertEquals(TextRange(2, 6), result.selection)
    }

    @Test
    /** Verifies markers sitting directly around the selection are dropped instead of doubled. */
    fun `unwraps markers surrounding the selection`() {
        val result = toggleInlineMarker(TextFieldValue("**bold**", TextRange(2, 6)), "**")
        assertEquals("bold", result.text)
        assertEquals(TextRange(0, 4), result.selection)
    }

    @Test
    /** Verifies a selection carrying the markers itself is unwrapped. */
    fun `unwraps markers inside the selection`() {
        val result = toggleInlineMarker(TextFieldValue("**bold**", TextRange(0, 8)), "**")
        assertEquals("bold", result.text)
        assertEquals(TextRange(0, 4), result.selection)
    }

    @Test
    /** Verifies the italic marker wraps with a single character on each side. */
    fun `wraps with the italic marker`() {
        val result = toggleInlineMarker(TextFieldValue("hi", TextRange(0, 2)), "*")
        assertEquals("*hi*", result.text)
        assertEquals(TextRange(1, 3), result.selection)
    }

    @Test
    /** Verifies every line a multi-line selection covers gets the unordered prefix. */
    fun `prefixes every selected line as unordered`() {
        val result = toggleList(TextFieldValue("a\nb", TextRange(0, 3)), ordered = false)
        assertEquals("- a\n- b", result.text)
        assertEquals(TextRange(2, 7), result.selection)
    }

    @Test
    /** Verifies lines that all carry the unordered prefix lose it together. */
    fun `removes the unordered prefix from prefixed lines`() {
        val result = toggleList(TextFieldValue("- a\n- b", TextRange(0, 7)), ordered = false)
        assertEquals("a\nb", result.text)
        assertEquals(TextRange(0, 3), result.selection)
    }

    @Test
    /** Verifies the prefix lands after existing indentation so nested items stay intact. */
    fun `keeps indentation before the prefix`() {
        val result = toggleList(TextFieldValue("    a", TextRange(5, 5)), ordered = false)
        assertEquals("    - a", result.text)
        assertEquals(TextRange(7, 7), result.selection)
    }

    @Test
    /** Verifies ordered lines are numbered from one in selection order. */
    fun `numbers every selected line as ordered`() {
        val result = toggleList(TextFieldValue("a\nb\nc", TextRange(0, 5)), ordered = true)
        assertEquals("1. a\n2. b\n3. c", result.text)
        assertEquals(TextRange(3, 14), result.selection)
    }

    @Test
    /** Verifies switching to ordered replaces an unordered marker instead of nesting it. */
    fun `replaces an unordered marker with ordered numbering`() {
        val result = toggleList(TextFieldValue("- a", TextRange(0, 3)), ordered = true)
        assertEquals("1. a", result.text)
        assertEquals(TextRange(1, 4), result.selection)
    }

    @Test
    /** Verifies ordered lines that all carry numbering lose it together. */
    fun `removes ordered numbering from prefixed lines`() {
        val result = toggleList(TextFieldValue("1. a\n2. b", TextRange(0, 9)), ordered = true)
        assertEquals("a\nb", result.text)
        assertEquals(TextRange(0, 3), result.selection)
    }

    @Test
    /** Verifies a multi-line selection is fenced with the content left selected. */
    fun `wraps the selected lines in a fence`() {
        val result = toggleCodeFence(TextFieldValue("a\nb", TextRange(0, 3)))
        assertEquals("```\na\nb\n```", result.text)
        assertEquals(TextRange(4, 7), result.selection)
    }

    @Test
    /** Verifies a bare caret on an empty line yields an empty fenced block around it. */
    fun `fences an empty line at a bare caret`() {
        val result = toggleCodeFence(TextFieldValue("", TextRange(0, 0)))
        assertEquals("```\n\n```", result.text)
        assertEquals(TextRange(4, 4), result.selection)
    }

    @Test
    /** Verifies a selection inside a fenced block drops the fences around it. */
    fun `removes the fence around the selection`() {
        val result = toggleCodeFence(TextFieldValue("```\na\nb\n```", TextRange(4, 7)))
        assertEquals("a\nb", result.text)
        assertEquals(TextRange(0, 3), result.selection)
    }

    @Test
    /** Verifies fencing and unfencing the same block round-trips back to the original text. */
    fun `round-trips fencing the selection`() {
        val fenced = toggleCodeFence(TextFieldValue("a\nb", TextRange(0, 3)))
        val result = toggleCodeFence(fenced)
        assertEquals("a\nb", result.text)
        assertEquals(TextRange(0, 3), result.selection)
    }
}
