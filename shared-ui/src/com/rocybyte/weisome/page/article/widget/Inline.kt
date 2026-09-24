package com.rocybyte.weisome.page.article.widget

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.PathParser
import com.rocybyte.weisome.article.MarkdownInline
import com.rocybyte.weisome.ui.LocalWeiSomeTextStyle
import com.rocybyte.weisome.widget.WeiSomeText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.skia.Image
import java.net.URI

/** Renders inline Markdown with the active theme's code labels, links, deletions, and images. */
@Composable
internal fun InlineMarkdownText(
    lines: List<List<MarkdownInline>>,
    fontSize: TextUnit,
    lineHeight: TextUnit,
    modifier: Modifier = Modifier,
    fontWeight: FontWeight? = null,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
) {
    val alignmentModifier = if (textAlign != null) modifier.fillMaxWidth() else modifier
    BoxWithConstraints(alignmentModifier) {
        val textMeasurer = rememberTextMeasurer()
        val density = androidx.compose.ui.platform.LocalDensity.current
        val horizontalPadding = 6.dp
        val verticalPadding = 1.dp
        val styles = LocalMarkdownPreviewStyles.current
        val codeStyle = LocalWeiSomeTextStyle.current.merge(
            TextStyle(
                color = styles.inlineCodeColor,
                fontSize = fontSize * styles.inlineCodeFontScale,
                fontStyle = FontStyle.Normal,
            ),
        )
        val maxContentWidth = with(density) {
            (maxWidth.toPx() - horizontalPadding.toPx() * 2f).coerceAtLeast(1f)
        }

        // ponytail: per-URL state with no cache or cancellation; revisit with coil if previews get image-heavy.
        val imageUrls = remember(lines) {
            lines.flatten().filterIsInstance<MarkdownInline.Image>().map { it.url }.distinct()
        }
        val imageBitmaps = remember(lines) {
            imageUrls.associateWith { mutableStateOf<ImageBitmap?>(null) }
        }
        LaunchedEffect(imageUrls) {
            imageUrls.forEach { url ->
                imageBitmaps.getValue(url).value = loadNetworkImage(url)
            }
        }

        val inlineContent = mutableMapOf<String, InlineTextContent>()
        var codeIndex = 0

        /** Measures the inline placeholder for an image scaled down to the available width. */
        fun imagePlaceholderSize(bitmap: ImageBitmap?): Pair<TextUnit, TextUnit> {
            if (bitmap == null) return 96.sp to 64.sp
            val scale = minOf(1f, maxContentWidth / bitmap.width)
            return with(density) {
                (bitmap.width * scale).toSp() to (bitmap.height * scale).toSp()
            }
        }

        val text = buildAnnotatedString {
            lines.forEachIndexed { lineIndex, inlines ->
                if (lineIndex > 0) append('\n')
                inlines.forEach { inline ->
                    when (inline) {
                        is MarkdownInline.Text -> append(inline.value)
                        is MarkdownInline.Bold -> withStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = styles.boldColor ?: Color.Unspecified,
                            ),
                        ) {
                            append(inline.value)
                        }

                        // Preview approximation: hydrogen styles em with dot text-emphasis,
                        // which Compose does not support; italic is the closest fallback.
                        is MarkdownInline.Italic -> withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(inline.value)
                        }

                        is MarkdownInline.Code -> {
                            val chunks = inlineCodeChunks(inline.value, maxContentWidth) { value ->
                                textMeasurer.measure(value, codeStyle, maxLines = 1).size.width.toFloat()
                            }
                            chunks.forEach { chunk ->
                                val id = "inline-code-${codeIndex++}"
                                val measuredSize = textMeasurer.measure(chunk, codeStyle, maxLines = 1).size
                                // The placeholder is sized to the measured text width, leaving the label
                                // no slack: any sub-pixel difference between measuring and rendering pushes
                                // the final glyph onto a second line, which maxLines = 1 then drops whole.
                                // One dp of slack keeps the label on its single line.
                                val placeholderWidth = with(density) {
                                    (measuredSize.width + horizontalPadding.roundToPx() * 2 + 1.dp.roundToPx()).toSp()
                                }
                                val placeholderHeight = with(density) {
                                    (measuredSize.height + verticalPadding.roundToPx() * 2).toSp()
                                }
                                inlineContent[id] = InlineTextContent(
                                    placeholder = Placeholder(
                                        width = placeholderWidth,
                                        height = placeholderHeight,
                                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                                    ),
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize()
                                            .background(
                                                styles.inlineCodeBackground,
                                                RoundedCornerShape(styles.inlineCodeCornerRadius),
                                            )
                                            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                                        contentAlignment = Alignment.CenterStart,
                                    ) {
                                        WeiSomeText(
                                            text = chunk,
                                            style = codeStyle,
                                            // Labels are pre-split to fit the line, so a wrap here can only
                                            // mean overflow; clipping it beats dropping a whole glyph.
                                            softWrap = false,
                                            maxLines = 1,
                                        )
                                    }
                                }
                                appendInlineContent(id, chunk)
                            }
                        }

                        is MarkdownInline.Link -> {
                            withStyle(
                                SpanStyle(
                                    color = styles.linkColor,
                                    textDecoration = if (styles.linkUnderlined) TextDecoration.Underline else null,
                                ),
                            ) {
                                append(inline.text)
                            }
                            if (styles.linkHasIcon) {
                                val id = "link-icon-${codeIndex++}"
                                inlineContent[id] = InlineTextContent(
                                    placeholder = Placeholder(
                                        width = 18.sp,
                                        height = 18.sp,
                                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                                    ),
                                ) {
                                    LinkIcon(Modifier.fillMaxSize(), styles.linkColor)
                                }
                                append(' ')
                                appendInlineContent(id, " ")
                            }
                        }

                        is MarkdownInline.Strikethrough -> withStyle(
                            SpanStyle(
                                textDecoration = TextDecoration.LineThrough,
                                color = styles.strikethroughColor,
                            ),
                        ) {
                            append(inline.value)
                        }

                        is MarkdownInline.Image -> {
                            val id = "inline-image-${codeIndex++}"
                            val bitmap = imageBitmaps[inline.url]?.value
                            val (placeholderWidth, placeholderHeight) = imagePlaceholderSize(bitmap)
                            inlineContent[id] = InlineTextContent(
                                placeholder = Placeholder(
                                    width = placeholderWidth,
                                    height = placeholderHeight,
                                    placeholderVerticalAlign = PlaceholderVerticalAlign.AboveBaseline,
                                ),
                            ) {
                                val current = imageBitmaps[inline.url]?.value
                                if (current != null) {
                                    androidx.compose.foundation.Image(
                                        bitmap = current,
                                        contentDescription = inline.alt,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .shadow(
                                                styles.imageShadowElevation,
                                                RoundedCornerShape(styles.imageCornerRadius),
                                            )
                                            .clip(RoundedCornerShape(styles.imageCornerRadius)),
                                    )
                                } else {
                                    Box(
                                        Modifier
                                            .fillMaxSize()
                                            .background(styles.quoteBackground, RoundedCornerShape(2.dp)),
                                    )
                                }
                            }
                            appendInlineContent(id, "[${inline.alt}]")
                        }
                    }
                }
            }
        }
        WeiSomeText(
            text = text,
            inlineContent = inlineContent,
            fontSize = fontSize,
            lineHeight = lineHeight,
            fontWeight = fontWeight,
            color = color,
            // The text box must span the container before textAlign can move the glyphs off the
            // start edge; BoxWithConstraints places a narrow child at TopStart regardless.
            modifier = if (textAlign != null) Modifier.fillMaxWidth() else Modifier,
            style = LocalWeiSomeTextStyle.current.merge(TextStyle(textAlign = textAlign ?: TextAlign.Unspecified)),
        )
    }
}

/** Renders the hydrogen link icon by stroking the stylesheet's SVG paths at the given size. */
@Composable
private fun LinkIcon(modifier: Modifier = Modifier, color: Color) {
    val paths = remember {
        HydrogenAssets.linkIconPathData.map { PathParser().parsePathString(it).toPath() }
    }
    Canvas(modifier.size(18.dp)) {
        val scale = size.width / 22f
        withTransform({ scale(scale, scale, pivot = androidx.compose.ui.geometry.Offset.Zero) }) {
            paths.forEach { path: Path ->
                drawPath(
                    path = path,
                    color = color,
                    style = Stroke(width = 1f, cap = StrokeCap.Round),
                )
            }
        }
    }
}

/** Downloads and decodes an image URL into a Compose bitmap on the IO dispatcher. */
internal suspend fun loadNetworkImage(url: String): ImageBitmap? = withContext(Dispatchers.IO) {
    runCatching {
        val bytes = URI(url).toURL().openStream().use { it.readBytes() }
        Image.makeFromEncoded(bytes).toComposeImageBitmap()
    }.getOrNull()
}

/**
 * Uppercases the first letter of the leading text-bearing inline, reproducing the
 * hydrogen `h2, h3, p::first-letter { text-transform: capitalize }` rule. Code and image
 * inlines are skipped because their content is not plain flowing text.
 */
internal fun capitalizeFirstLetter(inlines: List<MarkdownInline>): List<MarkdownInline> {
    if (inlines.isEmpty()) return inlines
    val head = inlines.first()
    val uppercased = when (head) {
        is MarkdownInline.Text -> head.copy(value = head.value.replaceFirstChar { it.uppercaseChar() })
        is MarkdownInline.Bold -> head.copy(value = head.value.replaceFirstChar { it.uppercaseChar() })
        is MarkdownInline.Italic -> head.copy(value = head.value.replaceFirstChar { it.uppercaseChar() })
        is MarkdownInline.Strikethrough -> head.copy(value = head.value.replaceFirstChar { it.uppercaseChar() })
        is MarkdownInline.Link -> head.copy(text = head.text.replaceFirstChar { it.uppercaseChar() })
        is MarkdownInline.Code, is MarkdownInline.Image -> return inlines
    }
    return listOf(uppercased) + inlines.drop(1)
}

/** Splits only overlong code labels so ordinary labels remain atomic at line boundaries. */
internal fun inlineCodeChunks(
    value: String,
    maxContentWidth: Float,
    measureWidth: (String) -> Float,
): List<String> {
    if (value.isEmpty() || measureWidth(value) <= maxContentWidth) return listOf(value)
    val chunks = mutableListOf<String>()
    var start = 0
    while (start < value.length) {
        var low = start + 1
        var high = value.length
        var bestEnd = low
        while (low <= high) {
            val middle = (low + high) ushr 1
            if (measureWidth(value.substring(start, middle)) <= maxContentWidth) {
                bestEnd = middle
                low = middle + 1
            } else {
                high = middle - 1
            }
        }
        if (bestEnd < value.length && value[bestEnd - 1].isHighSurrogate() && value[bestEnd].isLowSurrogate()) {
            bestEnd = if (bestEnd == start + 1) bestEnd + 1 else bestEnd - 1
        }
        chunks += value.substring(start, bestEnd)
        start = bestEnd
    }
    return chunks
}
