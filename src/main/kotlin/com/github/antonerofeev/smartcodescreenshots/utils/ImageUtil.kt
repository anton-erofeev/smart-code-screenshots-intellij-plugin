package com.github.antonerofeev.smartcodescreenshots.utils

import com.github.antonerofeev.smartcodescreenshots.utils.Constants.NOTIFICATION_GROUP
import com.github.antonerofeev.smartcodescreenshots.utils.Constants.PLUGIN_NAME
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.fileChooser.FileChooserFactory
import com.intellij.openapi.fileChooser.FileSaverDescriptor
import com.intellij.openapi.fileChooser.FileSaverDialog
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileWrapper
import java.awt.image.BufferedImage
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import javax.imageio.ImageIO

object ImageUtil {
    fun saveToFile(image: BufferedImage, project: Project) {
        val fileSaverDescriptor = FileSaverDescriptor(
            "Choose Image Location",
            "Select a location to save the screenshot to",
            "png")
        val saveDialog: FileSaverDialog = FileChooserFactory.getInstance()
                .createSaveFileDialog(fileSaverDescriptor, project)

        val date = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
        val defaultName = "screenshot-$date.png"

        val saveDialogResult: VirtualFileWrapper = saveDialog.save(defaultName) ?: return

        try {
            FileOutputStream(saveDialogResult.file).use { fos ->
                ImageIO.write(image, "png", fos)
            }
        } catch (t: Throwable) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup(NOTIFICATION_GROUP)
                .createNotification("Failed to write file: ${t::class.simpleName}", NotificationType.ERROR)
                .setTitle(PLUGIN_NAME)
                .setImportant(true)
                .notify(project)
            t.printStackTrace()
        }
    }

}
