package com.github.antonerofeev.smartcodescreenshots.utils

import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.image.BufferedImage

object Clipboard {
    fun copy(image: BufferedImage) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val transferable = object : Transferable {
            override fun getTransferDataFlavors() = arrayOf(DataFlavor.imageFlavor)
            override fun isDataFlavorSupported(flavor: DataFlavor) = flavor == DataFlavor.imageFlavor
            override fun getTransferData(flavor: DataFlavor) = image
        }
        clipboard.setContents(transferable, null)
    }

}