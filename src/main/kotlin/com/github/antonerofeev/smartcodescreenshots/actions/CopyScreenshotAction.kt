package com.github.antonerofeev.smartcodescreenshots.actions

import com.github.antonerofeev.smartcodescreenshots.utils.ScreenshotHandler
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.DumbAwareAction

class CopyScreenshotAction : DumbAwareAction() {

    override fun getActionUpdateThread() = ActionUpdateThread.EDT

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: return
        val editor = getEditor(e) ?: return
        ScreenshotHandler.process(project, editor)
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        val editor = getEditor(e)
        e.presentation.isEnabled = project != null && editor != null && editor.selectionModel.hasSelection()
    }

    private fun getEditor(e: AnActionEvent): Editor? =
        CommonDataKeys.EDITOR.getData(e.dataContext)
}
