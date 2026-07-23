package com.rocybyte.weisome.repository.article

import com.rocybyte.weisome.article.MarkdownDocumentParser
import com.rocybyte.weisome.article.MarkdownToWechatHtml
import com.rocybyte.weisome.article.MarkdownBlock
import com.rocybyte.weisome.article.MarkdownDocument
import com.rocybyte.weisome.repository.code.CodeHighlightRepo
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException

internal class DesktopWechatArticleRepository(
    private val codeHighlightRepo: CodeHighlightRepo,
) : WechatArticleRepository {
    /** Parses Markdown for display by the shared article preview. */
    override fun preview(markdown: String): MarkdownDocument = MarkdownDocumentParser.parse(markdown)
        .withCodeHighlights()

    /** Places rendered HTML on the desktop clipboard and reports whether it succeeded. */
    override fun copyAsHtml(markdown: String): Boolean = runCatching {
        val html = MarkdownToWechatHtml.render(preview(markdown))
        Toolkit.getDefaultToolkit().systemClipboard.setContents(HtmlTransferable(html), null)
    }.isSuccess

    /** Places the original Markdown on the clipboard as plain text for Juejin. */
    override fun copyForJuejin(markdown: String): Boolean = runCatching {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(createJuejinTransferable(markdown), null)
    }.isSuccess

    /** Enriches supported code blocks with the shared renderer-neutral highlight spans. */
    private fun MarkdownDocument.withCodeHighlights(): MarkdownDocument = copy(
        blocks = blocks.map { block ->
            val language = (block as? MarkdownBlock.CodeBlock)?.language
            if (block is MarkdownBlock.CodeBlock && language != null) {
                block.copy(highlights = codeHighlightRepo.highlight(language, block.code))
            } else {
                block
            }
        },
    )
}

/** Creates a plain-string clipboard payload so Juejin inserts the original Markdown. */
internal fun createJuejinTransferable(markdown: String): Transferable = StringSelection(markdown)

private class HtmlTransferable(private val html: String) : Transferable {
    private val htmlFlavor = DataFlavor("text/html;class=java.lang.String")
    private val flavors = arrayOf(htmlFlavor, DataFlavor.stringFlavor)

    /** Returns defensive copies of the HTML and plain-text clipboard flavors. */
    override fun getTransferDataFlavors(): Array<DataFlavor> = flavors.copyOf()

    /** Reports whether this transferable can provide the requested clipboard flavor. */
    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean = flavor in flavors

    /** Returns clipboard content for a supported flavor and rejects unknown flavors. */
    override fun getTransferData(flavor: DataFlavor): Any =
        if (flavor in flavors) html else throw UnsupportedFlavorException(flavor)
}
