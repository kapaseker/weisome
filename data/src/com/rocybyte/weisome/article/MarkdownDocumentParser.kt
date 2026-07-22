package com.rocybyte.weisome.article

object MarkdownDocumentParser {
    private val heading = Regex("^(#{1,3})\\s+(.+)$")
    private val unordered = Regex("^[-*]\\s+(.+)$")
    private val ordered = Regex("^\\d+\\.\\s+(.+)$")
    private val emphasis = Regex("\\*\\*([^*\\n]+)\\*\\*|(?<!\\*)\\*([^*\\n]+)\\*(?!\\*)")

    fun parse(markdown: String): MarkdownDocument {
        if (markdown.isBlank()) return MarkdownDocument(emptyList())
        val blocks = mutableListOf<MarkdownBlock>()
        val paragraph = mutableListOf<List<MarkdownInline>>()
        val items = mutableListOf<List<MarkdownInline>>()
        var orderedList: Boolean? = null

        fun flushParagraph() {
            if (paragraph.isNotEmpty()) {
                blocks += MarkdownBlock.Paragraph(paragraph.toList())
                paragraph.clear()
            }
        }
        fun flushList() {
            orderedList?.let {
                blocks += MarkdownBlock.ListBlock(it, items.toList())
                items.clear()
                orderedList = null
            }
        }
        fun addList(isOrdered: Boolean, text: String) {
            flushParagraph()
            if (orderedList != null && orderedList != isOrdered) flushList()
            orderedList = isOrdered
            items += inline(text)
        }

        markdown.replace("\r\n", "\n").lines().forEach { line ->
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
        return MarkdownDocument(blocks)
    }

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
