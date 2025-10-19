package com.github.antonerofeev.smartcodescreenshots.utils

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.github.antonerofeev.smartcodescreenshots.ui.preview.ImagePreviewDialog
import com.github.antonerofeev.smartcodescreenshots.utils.Constants.NOTIFICATION_GROUP
import com.github.antonerofeev.smartcodescreenshots.utils.Constants.PLUGIN_NAME
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import java.awt.image.BufferedImage

object ScreenshotHandler {

    fun process(project: Project, editor: Editor) {
        if (!editor.selectionModel.hasSelection()) {
            Notifications.showError(project, "Select code to screenshot first")
            return
        }

        val fileName = getCurrentFileName(project)
        val image = ScreenshotBuilder(editor, fileName).createImage()

        image?.let {
            Clipboard.copy(it)
            showNotification(project, it)
        }
    }

    private fun getCurrentFileName(project: Project): String? {
        val files = FileEditorManager.getInstance(project).selectedFiles
        return files.firstOrNull()?.name
    }


    private fun showNotification(project: Project, image: BufferedImage) {
        val notification = NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP)
            .createNotification("Image copied", NotificationType.INFORMATION)
            .setTitle(PLUGIN_NAME)

        notification.addAction(createAction("Show Preview") { _ ->
            val dialog = ImagePreviewDialog(project, image)
            dialog.showCentered()
            notification.notify(project)
        })

        notification.addAction(createAction("Save to File") {
            ImageUtil.saveToFile(image, project)
        })

        notification.notify(project)
    }

    private fun createAction(text: String, handler: (AnActionEvent) -> Unit): AnAction =
        object : AnAction(text) {
            override fun actionPerformed(e: AnActionEvent) = handler(e)
        }
}