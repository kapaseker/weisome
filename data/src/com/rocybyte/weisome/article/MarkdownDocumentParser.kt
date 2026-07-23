package com.rocybyte.weisome.article

object MarkdownDocumentParser {
    private val heading = Regex("^(#{1,3})\\s+(.+)$")
    private val unordered = Regex("^[-*]\\s+(.+)$")
    private val ordered = Regex("^\\d+\\.\\s+(.+)$")
    private val emphasis = Regex("\\*\\*([^*\\n]+)\\*\\*|(?<!\\*)\\*([^*\\n]+)\\*(?!\\*)")
    private val codeFence = Regex("^```\\s*([^\\s`]*)\\s*$")
    private const val CodePlaceholder = '\uFFFC'

    /** Parses supported Markdown blocks and inline markup into a structured document. */
    fun parse(markdown: String): MarkdownDocument {
        if (markdown.isBlank()) return MarkdownDocument(emptyList())
        val blocks = mutableListOf<MarkdownBlock>()
        val paragraph = mutableListOf<String>()
        val items = mutableListOf<List<MarkdownInline>>()
        var orderedList: Boolean? = null
        var codeLanguage: CodeLanguage? = null
        var codeLines: MutableList<String>? = null

        /** Emits the accumulated paragraph lines and clears their buffer. */
        fun flushParagraph() {
            if (paragraph.isNotEmpty()) {
                blocks += MarkdownBlock.Paragraph(inlineLines(paragraph))
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
                    paragraph += line
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

    /** Parses supported inline markup from one source line. */
    private fun inline(text: String): List<MarkdownInline> = inlineLines(listOf(text)).single()

    /** Parses inline markup while allowing a code span to consume paragraph line endings. */
    private fun inlineLines(lines: List<String>): List<List<MarkdownInline>> {
        val encoded = encodeCodeSpans(lines.joinToString("\n"))
        val result = mutableListOf(mutableListOf<MarkdownInline>())
        var cursor = 0

        /** Appends one encoded range, splitting source line endings but keeping code placeholders atomic. */
        fun appendRange(start: Int, endExclusive: Int, style: InlineStyle) {
            val text = StringBuilder()

            /** Emits accumulated literal text with the requested emphasis. */
            fun flushText() {
                if (text.isEmpty()) return
                val value = text.toString()
                result.last() += when (style) {
                    InlineStyle.Plain -> MarkdownInline.Text(value)
                    InlineStyle.Bold -> MarkdownInline.Bold(value)
                    InlineStyle.Italic -> MarkdownInline.Italic(value)
                }
                text.clear()
            }

            var index = start
            while (index < endExclusive) {
                val code = encoded.codeByOffset[index]
                when {
                    code != null -> {
                        flushText()
                        result.last() += MarkdownInline.Code(code)
                    }
                    encoded.text[index] == '\n' -> {
                        flushText()
                        result.add(mutableListOf())
                    }
                    else -> text.append(encoded.text[index])
                }
                index++
            }
            flushText()
        }

        emphasis.findAll(encoded.text).forEach { match ->
            appendRange(cursor, match.range.first, InlineStyle.Plain)
            val group = if (match.groupValues[1].isNotEmpty()) match.groups[1]!! else match.groups[2]!!
            val style = if (match.groupValues[1].isNotEmpty()) InlineStyle.Bold else InlineStyle.Italic
            appendRange(group.range.first, group.range.last + 1, style)
            cursor = match.range.last + 1
        }
        appendRange(cursor, encoded.text.length, InlineStyle.Plain)
        return result
    }

    /** Replaces valid CommonMark code spans with position-tracked placeholders. */
    private fun encodeCodeSpans(text: String): EncodedInline {
        val encoded = StringBuilder()
        val codeByOffset = mutableMapOf<Int, String>()
        var index = 0
        while (index < text.length) {
            when {
                text[index] == '\\' && text.getOrNull(index + 1) == '`' -> {
                    encoded.append('`')
                    index += 2
                }
                text[index] == '`' -> {
                    val delimiterLength = backtickRunLength(text, index)
                    val contentStart = index + delimiterLength
                    val closingStart = findClosingBackticks(text, contentStart, delimiterLength)
                    if (closingStart == null) {
                        encoded.append(text, index, contentStart)
                        index = contentStart
                    } else {
                        codeByOffset[encoded.length] = normalizeCodeSpan(text.substring(contentStart, closingStart))
                        encoded.append(CodePlaceholder)
                        index = closingStart + delimiterLength
                    }
                }
                else -> {
                    encoded.append(text[index])
                    index++
                }
            }
        }
        return EncodedInline(encoded.toString(), codeByOffset)
    }

    /** Counts the maximal backtick run beginning at the requested offset. */
    private fun backtickRunLength(text: String, start: Int): Int {
        var end = start
        while (end < text.length && text[end] == '`') end++
        return end - start
    }

    /** Finds the next maximal backtick run whose length matches the opening delimiter. */
    private fun findClosingBackticks(text: String, start: Int, delimiterLength: Int): Int? {
        var index = start
        while (index < text.length) {
            if (text[index] != '`') {
                index++
                continue
            }
            val runLength = backtickRunLength(text, index)
            if (runLength == delimiterLength) return index
            index += runLength
        }
        return null
    }

    /** Applies CommonMark line-ending and edge-space normalization to code span content. */
    private fun normalizeCodeSpan(content: String): String {
        val normalized = content.replace('\n', ' ').replace('\r', ' ')
        return if (
            normalized.length >= 2 &&
            normalized.first() == ' ' &&
            normalized.last() == ' ' &&
            normalized.any { it != ' ' }
        ) {
            normalized.substring(1, normalized.lastIndex)
        } else {
            normalized
        }
    }

    private enum class InlineStyle { Plain, Bold, Italic }

    private data class EncodedInline(
        val text: String,
        val codeByOffset: Map<Int, String>,
    )
}
