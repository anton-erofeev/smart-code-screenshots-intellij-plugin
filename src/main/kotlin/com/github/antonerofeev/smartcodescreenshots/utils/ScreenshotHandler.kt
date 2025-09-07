package com.github.antonerofeev.smartcodescreenshots.utils

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.fileChooser.FileSaverDialog
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileWrapper
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.image.BufferedImage
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.imageio.ImageIO

object ScreenshotHandler {

    private const val NOTIFICATION_GROUP = "Code Screenshots"

    fun process(project: Project, editor: Editor) {
        if (!editor.selectionModel.hasSelection()) {
            Notifications.showError(project, "Select code to screenshot first")
            return
        }

        val fileName = getCurrentFileName(project)
        val image = ScreenshotBuilder(editor, fileName).createImage()

        image?.let {
            copyToClipboard(it)
            showNotification(project, it, fileName)
        }
    }

    private fun getCurrentFileName(project: Project): String? {
        val files = FileEditorManager.getInstance(project).selectedFiles
        return files.firstOrNull()?.name
    }

    private fun copyToClipboard(image: BufferedImage) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val transferable = object : Transferable {
            override fun getTransferDataFlavors() = arrayOf(DataFlavor.imageFlavor)
            override fun isDataFlavorSupported(flavor: DataFlavor) = flavor == DataFlavor.imageFlavor
            override fun getTransferData(flavor: DataFlavor) = image
        }
        clipboard.setContents(transferable, null)
    }

    private fun showNotification(project: Project, image: BufferedImage, fileName: String?) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP)
            .createNotification("Image copied", NotificationType.INFORMATION)
            .setTitle("Code screenshots")
            .addAction(createAction("Save to File") {
                saveToFileDialog(image, project, fileName)
            })
            .notify(project)
    }

    private fun createAction(text: String, handler: (AnActionEvent) -> Unit): AnAction =
        object : AnAction(text) {
            override fun actionPerformed(e: AnActionEvent) = handler(e)
        }

    private fun saveToFileDialog(image: BufferedImage, project: Project, fileName: String?) {
        val fsd = FileSaverDescriptor(
            "Choose Image Location",
            "Select a location to save the screenshot to",
            "png"
        )
        val saveDialog: FileSaverDialog = FileChooserFactory.getInstance().createSaveFileDialog(fsd, project)

        val date = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val defaultName = "${fileName ?: "screenshot"}-$date.png"

        val save: VirtualFileWrapper = saveDialog.save(defaultName) ?: return

        try {
            FileOutputStream(save.file).use { fos ->
                ImageIO.write(image, "png", fos)
            }
        } catch (t: Throwable) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup(NOTIFICATION_GROUP)
                .createNotification("Failed to write file: ${t::class.simpleName}", NotificationType.ERROR)
                .setTitle("Code screenshots")
                .setImportant(true)
                .notify(project)
            t.printStackTrace()
        }
    }
}