package com.rocybyte.weisome.repository.article

import com.rocybyte.weisome.article.MarkdownDocument
import com.rocybyte.weisome.article.MarkdownDocumentParser
import com.rocybyte.weisome.article.MarkdownToWechatHtml
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException

internal class DesktopWechatArticleRepository : WechatArticleRepository {
    override fun preview(markdown: String): MarkdownDocument = MarkdownDocumentParser.parse(markdown)

    override fun copyAsHtml(markdown: String): Boolean = runCatching {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(HtmlTransferable(MarkdownToWechatHtml.render(markdown)), null)
    }.isSuccess
}

private class HtmlTransferable(private val html: String) : Transferable {
    private val htmlFlavor = DataFlavor("text/html;class=java.lang.String")
    private val flavors = arrayOf(htmlFlavor, DataFlavor.stringFlavor)

    override fun getTransferDataFlavors(): Array<DataFlavor> = flavors.copyOf()

    override fun isDataFlavorSupported(flavor: DataFlavor): Boolean = flavor in flavors

    override fun getTransferData(flavor: DataFlavor): Any =
        if (flavor in flavors) html else throw UnsupportedFlavorException(flavor)
}
