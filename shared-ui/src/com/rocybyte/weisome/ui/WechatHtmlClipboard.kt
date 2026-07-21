package com.rocybyte.weisome.ui

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.datatransfer.UnsupportedFlavorException

object WechatHtmlClipboard {

    private val htmlFlavor = DataFlavor("text/html;class=java.lang.String")
    private val flavors = arrayOf(htmlFlavor, DataFlavor.stringFlavor)

    fun copy(html: String) {
        Toolkit.getDefaultToolkit().systemClipboard.setContents(HtmlTransferable(html), null)
    }

    private class HtmlTransferable(private val html: String) : Transferable {

        override fun getTransferDataFlavors(): Array<DataFlavor> = flavors.copyOf()

        override fun isDataFlavorSupported(flavor: DataFlavor): Boolean = flavor in flavors

        override fun getTransferData(flavor: DataFlavor): Any =
            if (flavor in flavors) html else throw UnsupportedFlavorException(flavor)
    }
}
