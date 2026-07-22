package com.rocybyte.weisome.article

object MarkdownDocumentParser {
    private val heading = Regex("^(#{1,3})\\s+(.+)$")
    private val unordered = Regex("^[-*]\\s+(.+)$")
    private val ordered = Regex("^\\d+\\.\\s+(.+)$")
    private val emphasis = Regex("\\*\\*([^*\\n]+)\\*\\*|(?<!\\*)\\*([^*\\n]+)\\*(?!\\*)")
    private val codeFence = Regex("^```\\s*([^\\s`]*)\\s*$")

    /** Parses supported Markdown blocks and inline markup into a structured document. */
    fun parse(markdown: String): MarkdownDocument {
        if (markdown.isBlank()) return MarkdownDocument(emptyList())
        val blocks = mutableListOf<MarkdownBlock>()
        val paragraph = mutableListOf<List<MarkdownInline>>()
        val items = mutableListOf<List<MarkdownInline>>()
        var orderedList: Boolean? = null
        var codeLanguage: CodeLanguage? = null
        var codeLines: MutableList<String>? = null

        /** Emits the accumulated paragraph lines and clears their buffer. */
        fun flushParagraph() {
            if (paragraph.isNotEmpty()) {
                blocks += MarkdownBlock.Paragraph(paragraph.toList())
                paragraph.clear()
            }
        }
        /** Emits the accumulated list items and resets the active list. */
        fun flushList() {
            orderedList?.let {
                blocks += MarkdownBlock.ListBlock(it, items.toList())
                items.clear()
                orderedList = null
            }
        }
        /** Adds an item while splitting the active list when its ordering mode changes. */
        fun addList(isOrdered: Boolean, text: String) {
            flushParagraph()
            if (orderedList != null && orderedList != isOrdered) flushList()
            orderedList = isOrdered
            items += inline(text)
        }
        /** Emits the active fenced code block and clears its parsing state. */
        fun flushCode() {
            val lines = codeLines ?: return
            blocks += MarkdownBlock.CodeBlock(codeLanguage, lines.joinToString("\n"))
            codeLanguage = null
            codeLines = null
        }

        markdown.replace("\r\n", "\n").lines().forEach { line ->
            if (codeLines != null) {
                if (line.trim() == "```") {
                    flushCode()
                } else {
                    codeLines?.add(line)
                }
                return@forEach
            }

            val codeFenceMatch = codeFence.matchEntire(line)
            if (codeFenceMatch != null) {
                flushParagraph()
                flushList()
                codeLanguage = codeLanguage(codeFenceMatch.groupValues[1])
                codeLines = mutableListOf()
                return@forEach
            }

            val headingMatch = heading.matchEntire(line)
            when {
                line.isBlank() -> {
                    flushParagraph()
                    flushList()
                }
                headingMatch != null -> {
                    flushParagraph()
                    flushList()
                    blocks += MarkdownBlock.Heading(headingMatch.groupValues[1].length, inline(headingMatch.groupValues[2]))
                }
                unordered.matches(line) -> addList(false, unordered.matchEntire(line)!!.groupValues[1])
                ordered.matches(line) -> addList(true, ordered.matchEntire(line)!!.groupValues[1])
                else -> {
                    flushList()
                    paragraph += inline(line)
                }
            }
        }
        flushParagraph()
        flushList()
        flushCode()
        return MarkdownDocument(blocks)
    }

    /** Maps supported fenced-code labels and aliases to their language model. */
    private fun codeLanguage(label: String): CodeLanguage? = when (label.lowercase()) {
        "java" -> CodeLanguage.Java
        "kotlin", "kt" -> CodeLanguage.Kotlin
        "rust", "rs" -> CodeLanguage.Rust
        else -> null
    }

    /** Parses bold, italic, and plain inline spans from a text fragment. */
    private fun inline(text: String): List<MarkdownInline> {
        val result = mutableListOf<MarkdownInline>()
        var cursor = 0
        emphasis.findAll(text).forEach { match ->
            if (match.range.first > cursor) result += MarkdownInline.Text(text.substring(cursor, match.range.first))
            result += if (match.groupValues[1].isNotEmpty()) {
                MarkdownInline.Bold(match.groupValues[1])
            } else {
                MarkdownInline.Italic(match.groupValues[2])
            }
            cursor = match.range.last + 1
        }
        if (cursor < text.length) result += MarkdownInline.Text(text.substring(cursor))
        return result
    }
}
