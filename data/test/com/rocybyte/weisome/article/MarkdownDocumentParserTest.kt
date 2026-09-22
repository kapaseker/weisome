package com.rocybyte.weisome.article

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MarkdownDocumentParserTest {
    @Test
    /** Verifies block ranges are ordered, non-overlapping, and span the full source text. */
    fun `records source offset ranges for mixed top-level blocks`() {
        val markdown = "# Title\n\nIntro paragraph\n\n- item\n\n> quote\n\n---\n\n```kotlin\nval a = 1\n```"
        val document = MarkdownDocumentParser.parse(markdown)

        assertEquals(document.blocks.size, document.blockRanges.size)
        // Ranges are ordered, non-overlapping, and jointly span the whole document
        // (blank separator lines belong to no block).
        var previousEnd = -1
        document.blockRanges.forEach { range ->
            assertTrue(range.last >= range.first, "range $range is empty")
            assertTrue(range.first > previousEnd, "range $range overlaps the previous one")
            previousEnd = range.last
        }
        assertEquals(0, document.blockRanges.first().first)
        assertEquals(markdown.length - 1, document.blockRanges.last().last)
    }

    @Test
    /** Verifies a block range's text matches the block's exact source lines. */
    fun `block range text matches block source`() {
        val markdown = "Para one\nstill para.\n\n```kotlin\nval a = 1\n```"
        val document = MarkdownDocumentParser.parse(markdown)

        val paragraphRange = document.blockRanges[0]
        assertEquals("Para one\nstill para.", markdown.substring(paragraphRange.first, paragraphRange.last + 1))
        val codeRange = document.blockRanges[1]
        assertEquals("```kotlin\nval a = 1\n```", markdown.substring(codeRange.first, codeRange.last + 1))
    }

    @Test
    /** Verifies CommonMark code spans remain literal and support surrounding emphasis. */
    fun `parses literal inline code inside emphasized text`() {
        val document = MarkdownDocumentParser.parse("**Call `launch(*args*)` now** and \\`later\\`")

        assertEquals(
            MarkdownBlock.Paragraph(
                listOf(
                    listOf(
                        MarkdownInline.Bold("Call "),
                        MarkdownInline.Code("launch(*args*)"),
                        MarkdownInline.Bold(" now"),
                        MarkdownInline.Text(" and `later`"),
                    ),
                ),
            ),
            document.blocks.single(),
        )
    }

    @Test
    /** Verifies equal backtick runs and CommonMark whitespace normalization across lines. */
    fun `parses multiline code spans with matching backtick runs`() {
        val document = MarkdownDocumentParser.parse("Before `` value is `x`  \nnext `` after")

        assertEquals(
            MarkdownBlock.Paragraph(
                listOf(
                    listOf(
                        MarkdownInline.Text("Before "),
                        MarkdownInline.Code("value is `x`   next"),
                        MarkdownInline.Text(" after"),
                    ),
                ),
            ),
            document.blocks.single(),
        )
    }

    @Test
    /** Verifies an unmatched backtick run remains readable source text. */
    fun `preserves unmatched backtick runs`() {
        assertEquals(
            MarkdownBlock.Paragraph(listOf(listOf(MarkdownInline.Text("Use ``unfinished` here")))),
            MarkdownDocumentParser.parse("Use ``unfinished` here").blocks.single(),
        )
    }

    @Test
    /** Verifies Java, Kotlin, and Rust fence labels and aliases map to supported languages. */
    fun `parses supported fenced code languages`() {
        val markdown = """
            ```java
            class Example {}
            ```
            ```kt
            fun main() = Unit
            ```
            ```rs
            fn main() {}
            ```
        """.trimIndent()

        assertEquals(
            listOf(
                MarkdownBlock.CodeBlock(CodeLanguage.Java, "class Example {}"),
                MarkdownBlock.CodeBlock(CodeLanguage.Kotlin, "fun main() = Unit"),
                MarkdownBlock.CodeBlock(CodeLanguage.Rust, "fn main() {}"),
            ),
            MarkdownDocumentParser.parse(markdown).blocks,
        )
    }

    @Test
    /** Verifies unsupported and unclosed fences remain readable plain code blocks. */
    fun `falls back to plain code for unknown and unclosed fences`() {
        val markdown = """
            ```python
            print("hello")
        """.trimIndent()

        assertEquals(
            listOf(MarkdownBlock.CodeBlock(language = null, code = "print(\"hello\")")),
            MarkdownDocumentParser.parse(markdown).blocks,
        )
    }

    @Test
    /** Verifies fenced code preserves blank lines and suppresses Markdown parsing inside the fence. */
    fun `preserves fenced code contents verbatim`() {
        val markdown = """
            ```kotlin
            # not a heading

            * not a list
            ```
        """.trimIndent()

        assertEquals(
            MarkdownBlock.CodeBlock(
                language = CodeLanguage.Kotlin,
                code = "# not a heading\n\n* not a list",
            ),
            MarkdownDocumentParser.parse(markdown).blocks.single(),
        )
    }

    @Test
    /** Verifies thematic breaks in all marker styles parse as horizontal rules. */
    fun `parses thematic breaks as horizontal rules`() {
        assertEquals(
            listOf(
                MarkdownBlock.Paragraph(listOf(listOf(MarkdownInline.Text("Above")))),
                MarkdownBlock.HorizontalRule,
                MarkdownBlock.HorizontalRule,
                MarkdownBlock.HorizontalRule,
            ),
            MarkdownDocumentParser.parse("Above\n\n---\n\n***\n\n___").blocks,
        )
    }

    @Test
    /** Verifies headings up to level six are recognized. */
    fun `parses level six headings`() {
        assertEquals(
            MarkdownBlock.Heading(6, listOf(MarkdownInline.Text("Deep"))),
            MarkdownDocumentParser.parse("###### Deep").blocks.single(),
        )
    }

    @Test
    /** Verifies blockquote content parses recursively and nesting is preserved. */
    fun `parses nested blockquotes recursively`() {
        assertEquals(
            MarkdownBlock.BlockQuote(
                listOf(
                    MarkdownBlock.Paragraph(listOf(listOf(MarkdownInline.Text("Outer")))),
                    MarkdownBlock.BlockQuote(
                        listOf(MarkdownBlock.Paragraph(listOf(listOf(MarkdownInline.Text("Inner"))))),
                    ),
                ),
            ),
            MarkdownDocumentParser.parse("> Outer\n> > Inner").blocks.single(),
        )
    }

    @Test
    /** Verifies GFM tables parse into header and body rows of inline content. */
    fun `parses gfm tables with header and rows`() {
        assertEquals(
            MarkdownBlock.Table(
                header = listOf(
                    listOf(MarkdownInline.Text("Name")),
                    listOf(MarkdownInline.Bold("Value")),
                ),
                rows = listOf(
                    listOf(listOf(MarkdownInline.Text("a")), listOf(MarkdownInline.Text("1"))),
                    listOf(listOf(MarkdownInline.Text("b")), listOf(MarkdownInline.Text("2"))),
                ),
            ),
            MarkdownDocumentParser.parse("| Name | **Value** |\n| --- | --- |\n| a | 1 |\n| b | 2 |").blocks.single(),
        )
    }

    @Test
    /** Verifies indented list items become nested children of the previous item. */
    fun `parses nested list items as children`() {
        assertEquals(
            MarkdownBlock.ListBlock(
                ordered = false,
                items = listOf(
                    ListItem(
                        content = listOf(MarkdownInline.Text("Parent")),
                        child = MarkdownBlock.ListBlock(
                            ordered = true,
                            items = listOf(ListItem(listOf(MarkdownInline.Text("Child")))),
                        ),
                    ),
                ),
            ),
            MarkdownDocumentParser.parse("- Parent\n  1. Child").blocks.single(),
        )
    }

    @Test
    /** Verifies task list markers capture their checked state. */
    fun `parses task list markers`() {
        assertEquals(
            MarkdownBlock.ListBlock(
                ordered = false,
                items = listOf(
                    ListItem(listOf(MarkdownInline.Text("todo")), task = false),
                    ListItem(listOf(MarkdownInline.Text("done")), task = true),
                ),
            ),
            MarkdownDocumentParser.parse("- [ ] todo\n- [x] done").blocks.single(),
        )
    }

    @Test
    /** Verifies links, strikethrough, and images parse as inline constructs. */
    fun `parses links strikethrough and images`() {
        assertEquals(
            listOf(
                MarkdownInline.Link("site", "https://example.com"),
                MarkdownInline.Text(" "),
                MarkdownInline.Strikethrough("gone"),
                MarkdownInline.Text(" "),
                MarkdownInline.Image("pic", "https://example.com/i.png"),
            ),
            MarkdownDocumentParser.parse("[site](https://example.com) ~~gone~~ ![pic](https://example.com/i.png)")
                .blocks.single().let { it as MarkdownBlock.Paragraph }.lines.single(),
        )
    }
}
