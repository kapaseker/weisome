package com.rocybyte.weisome.article

object MarkdownDocumentParser {
    private val heading = Regex("^(#{1,6})\\s+(.+)$")
    private val unordered = Regex("^(\\s*)[-*+]\\s+(.+)$")
    private val ordered = Regex("^(\\s*)\\d+\\.\\s+(.+)$")
    private val thematicBreak = Regex("^ {0,3}([-*_])(?:\\s*\\1){2,}\\s*$")
    private val quoteLine = Regex("^ {0,3}>")
    private val quotePrefix = Regex("^ {0,3}> ?")
    private val tableSeparator = Regex("^\\s*\\|?\\s*:?-+:?\\s*(\\|\\s*:?-+:?\\s*)*\\|?\\s*$")
    private val taskMarker = Regex("^\\[([ xX])]\\s+(.+)$")
    private val inlineToken = Regex(
        "!\\[([^\\]\\n]*)]\\(([^)\\s]+)\\)" + // 1, 2: image alt and url
            "|\\[([^\\]\\n]*)]\\(([^)\\s]+)\\)" + // 3, 4: link text and url
            "|~~([^~\\n]+)~~" + // 5: strikethrough
            "|\\*\\*([^*\\n]+)\\*\\*" + // 6: bold
            "|(?<!\\*)\\*([^*\\n]+)\\*(?!\\*)", // 7: italic
    )
    private val codeFence = Regex("^```\\s*([^\\s`]*)\\s*$")
    private const val CodePlaceholder = '\uFFFC'

    /** Parses supported Markdown blocks and inline markup into a structured document. */
    fun parse(markdown: String): MarkdownDocument {
        if (markdown.isBlank()) return MarkdownDocument(emptyList())
        val normalized = markdown.replace("\r\n", "\n")
        val lines = normalized.lines()
        val blocks = mutableListOf<MarkdownBlock>()
        val ranges = mutableListOf<IntRange>()
        val paragraph = mutableListOf<String>()
        var paragraphStartLine = 0
        var codeLanguage: CodeLanguage? = null
        var codeLines: MutableList<String>? = null
        var codeStartLine = 0
        // Inclusive character offset of each line start in the normalized source text.
        val lineOffsets = IntArray(lines.size + 1)
        for (lineIndex in lines.indices) {
            lineOffsets[lineIndex + 1] = lineOffsets[lineIndex] + lines[lineIndex].length + 1
        }

        /** Converts an inclusive line span into an inclusive character-offset range. */
        fun offsetRange(startLine: Int, endLineInclusive: Int): IntRange {
            val lastLine = lines.lastIndex
            val end = if (endLineInclusive >= lastLine) {
                normalized.length - 1
            } else {
                lineOffsets[endLineInclusive + 1] - 2
            }
            return lineOffsets[startLine]..end
        }

        /** Emits the accumulated paragraph lines and clears their buffer. */
        fun flushParagraph() {
            if (paragraph.isNotEmpty()) {
                blocks += MarkdownBlock.Paragraph(inlineLines(paragraph))
                ranges += offsetRange(paragraphStartLine, paragraphStartLine + paragraph.size - 1)
                paragraph.clear()
            }
        }

        /** Emits the active fenced code block and clears its parsing state. */
        fun flushCode() {
            val linesInCode = codeLines ?: return
            blocks += MarkdownBlock.CodeBlock(codeLanguage, linesInCode.joinToString("\n"))
            ranges += offsetRange(codeStartLine, codeStartLine + linesInCode.size + 1)
            codeLanguage = null
            codeLines = null
        }

        var index = 0
        while (index < lines.size) {
            val line = lines[index]

            if (codeLines != null) {
                if (line.trim() == "```") {
                    flushCode()
                } else {
                    codeLines?.add(line)
                }
                index++
                continue
            }

            val codeFenceMatch = codeFence.matchEntire(line)
            when {
                codeFenceMatch != null -> {
                    flushParagraph()
                    codeLanguage = codeLanguage(codeFenceMatch.groupValues[1])
                    codeLines = mutableListOf()
                    codeStartLine = index
                }

                line.isBlank() -> flushParagraph()

                thematicBreak.matches(line) -> {
                    flushParagraph()
                    blocks += MarkdownBlock.HorizontalRule
                    ranges += offsetRange(index, index)
                }

                else -> {
                    val headingMatch = heading.matchEntire(line)
                    val startsQuote = quoteLine.containsMatchIn(line)
                    val isTable = line.contains('|') && index + 1 < lines.size &&
                        lines[index + 1].contains('|') && tableSeparator.matches(lines[index + 1])
                    val isListItem = unordered.matches(line) || ordered.matches(line)
                    when {
                        headingMatch != null -> {
                            flushParagraph()
                            blocks += MarkdownBlock.Heading(
                                headingMatch.groupValues[1].length,
                                inline(headingMatch.groupValues[2]),
                            )
                            ranges += offsetRange(index, index)
                        }

                        startsQuote -> {
                            flushParagraph()
                            val quoteStart = index
                            val quoted = mutableListOf<String>()
                            while (index < lines.size && quoteLine.containsMatchIn(lines[index])) {
                                quoted += quotePrefix.replaceFirst(lines[index], "")
                                index++
                            }
                            blocks += MarkdownBlock.BlockQuote(parse(quoted.joinToString("\n")).blocks)
                            ranges += offsetRange(quoteStart, index - 1)
                            continue
                        }

                        isTable -> {
                            flushParagraph()
                            val tableStart = index
                            val header = splitTableRow(line).map(::inline)
                            index += 2
                            val rows = mutableListOf<List<List<MarkdownInline>>>()
                            while (index < lines.size && lines[index].contains('|') && lines[index].isNotBlank()) {
                                rows += splitTableRow(lines[index]).map(::inline)
                                index++
                            }
                            blocks += MarkdownBlock.Table(header, rows)
                            ranges += offsetRange(tableStart, index - 1)
                            continue
                        }

                        isListItem -> {
                            flushParagraph()
                            val (list, next) = parseList(lines, index)
                            blocks += list
                            ranges += offsetRange(index, next - 1)
                            index = next
                            continue
                        }

                        else -> {
                            if (paragraph.isEmpty()) paragraphStartLine = index
                            paragraph += line
                        }
                    }
                }
            }
            index++
        }
        flushParagraph()
        flushCode()
        return MarkdownDocument(blocks, ranges)
    }

    /** Accumulates raw item text while a list is being parsed, before inline parsing. */
    private class RawListItem(val task: Boolean?, val text: StringBuilder) {
        var child: MarkdownBlock.ListBlock? = null
    }

    /**
     * Parses a consecutive run of list lines starting at [start] and returns the block together
     * with the index of the first unconsumed line. Items indented two or more spaces beyond the
     * list base become the nested child of the previous item.
     */
    private fun parseList(lines: List<String>, start: Int): Pair<MarkdownBlock.ListBlock, Int> {
        val baseIndent = leadingSpaces(lines[start])
        val isOrdered = ordered.matches(lines[start])
        val items = mutableListOf<RawListItem>()
        var index = start
        while (index < lines.size) {
            val line = lines[index]
            if (line.isBlank()) {
                val nextNonBlank = lines.withIndex().drop(index + 1).firstOrNull { it.value.isNotBlank() }
                val staysOpen = nextNonBlank != null &&
                    isListItemLine(nextNonBlank.value) &&
                    leadingSpaces(nextNonBlank.value) >= baseIndent
                if (!staysOpen) break
                index = nextNonBlank.index
                continue
            }
            val marker = unordered.find(line) ?: ordered.find(line)
            val indent = leadingSpaces(line)
            when {
                marker != null && indent >= baseIndent + 2 && items.isNotEmpty() -> {
                    val (child, next) = parseList(lines, index)
                    items.last().child = child
                    index = next
                }

                marker != null && indent < baseIndent -> break

                marker != null && ordered.matches(line) == isOrdered -> {
                    val content = marker.groupValues[2]
                    val taskMatch = taskMarker.matchEntire(content.trim())
                    val task = taskMatch?.let { it.groupValues[1].lowercase() == "x" }
                    items += RawListItem(task, StringBuilder(taskMatch?.groupValues[2] ?: content))
                    index++
                }

                marker != null -> break // ordering mode changed; the caller starts a new list

                indent >= baseIndent + 2 && items.isNotEmpty() -> {
                    items.last().text.append(' ').append(line.trim())
                    index++
                }

                else -> break
            }
        }
        val parsed = items.map { ListItem(inline(it.text.toString()), it.task, it.child) }
        return MarkdownBlock.ListBlock(isOrdered, parsed) to index
    }

    /** Reports whether the line begins with an unordered or ordered list marker. */
    private fun isListItemLine(line: String): Boolean = unordered.matches(line) || ordered.matches(line)

    /** Counts the leading spaces of a line. */
    private fun leadingSpaces(line: String): Int = line.indexOfFirst { it != ' ' }.let { if (it < 0) line.length else it }

    /** Splits one GFM table row into trimmed cell texts, dropping optional edge pipes. */
    private fun splitTableRow(line: String): List<String> {
        var trimmed = line.trim()
        if (trimmed.startsWith("|")) trimmed = trimmed.substring(1)
        if (trimmed.endsWith("|")) trimmed = trimmed.dropLast(1)
        return trimmed.split("|").map { it.trim() }
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
                    InlineStyle.Del -> MarkdownInline.Strikethrough(value)
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

        inlineToken.findAll(encoded.text).forEach { match ->
            appendRange(cursor, match.range.first, InlineStyle.Plain)
            when {
                match.groups[2] != null -> result.last() += MarkdownInline.Image(
                    decodeEncoded(encoded, match.groups[1]!!.range.first, match.groups[1]!!.range.last + 1),
                    decodeEncoded(encoded, match.groups[2]!!.range.first, match.groups[2]!!.range.last + 1),
                )

                match.groups[4] != null -> result.last() += MarkdownInline.Link(
                    decodeEncoded(encoded, match.groups[3]!!.range.first, match.groups[3]!!.range.last + 1),
                    decodeEncoded(encoded, match.groups[4]!!.range.first, match.groups[4]!!.range.last + 1),
                )

                match.groups[5] != null -> appendRange(
                    match.groups[5]!!.range.first,
                    match.groups[5]!!.range.last + 1,
                    InlineStyle.Del,
                )

                match.groups[6] != null -> appendRange(
                    match.groups[6]!!.range.first,
                    match.groups[6]!!.range.last + 1,
                    InlineStyle.Bold,
                )

                match.groups[7] != null -> appendRange(
                    match.groups[7]!!.range.first,
                    match.groups[7]!!.range.last + 1,
                    InlineStyle.Italic,
                )
            }
            cursor = match.range.last + 1
        }
        appendRange(cursor, encoded.text.length, InlineStyle.Plain)
        return result
    }

    /** Restores code-span placeholders inside one encoded range to their raw content. */
    private fun decodeEncoded(encoded: EncodedInline, start: Int, endExclusive: Int): String {
        val decoded = StringBuilder()
        for (index in start until endExclusive) {
            val code = encoded.codeByOffset[index]
            if (code != null) decoded.append(code) else decoded.append(encoded.text[index])
        }
        return decoded.toString()
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

    private enum class InlineStyle { Plain, Bold, Italic, Del }

    private data class EncodedInline(
        val text: String,
        val codeByOffset: Map<Int, String>,
    )
}
