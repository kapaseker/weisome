package com.rocybyte.weisome.article

import kotlin.test.Test
import kotlin.test.assertEquals

class MarkdownDocumentParserTest {
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
}
