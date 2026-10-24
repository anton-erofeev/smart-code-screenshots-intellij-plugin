package com.github.antonerofeev.smartcodescreenshots.utils

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import java.awt.Toolkit
import java.awt.HeadlessException
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.image.BufferedImage

object Clipboard {
    private val LOG = Logger.getInstance(Clipboard::class.java)

    fun copy(image: BufferedImage, project: Project): Boolean {
        try {
            val clipboard = Toolkit.getDefaultToolkit().systemClipboard
            val transferable = object : Transferable {
                override fun getTransferDataFlavors() = arrayOf(DataFlavor.imageFlavor)
                override fun isDataFlavorSupported(flavor: DataFlavor) = flavor == DataFlavor.imageFlavor
                override fun getTransferData(flavor: DataFlavor): Any {
                    if (!isDataFlavorSupported(flavor)) {
                        throw java.awt.datatransfer.UnsupportedFlavorException(flavor)
                    }
                    return image
                }
            }
            clipboard.setContents(transferable, null)
            return true
        } catch (e: IllegalStateException) {
            reportFailure(project, e)
        } catch (e: SecurityException) {
            reportFailure(project, e)
        } catch (e: HeadlessException) {
            reportFailure(project, e)
        }
        return false
    }

    private fun reportFailure(project: Project, error: RuntimeException) {
        LOG.warn("Failed to copy screenshot to clipboard", error)
        Notifications.showError(
            project,
            "Failed to copy image to clipboard: ${error.message ?: "clipboard is unavailable"}"
        )
    }
}