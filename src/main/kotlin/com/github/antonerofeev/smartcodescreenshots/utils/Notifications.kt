package com.github.antonerofeev.smartcodescreenshots.utils

import com.github.antonerofeev.smartcodescreenshots.utils.Constants.NOTIFICATION_GROUP
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

object Notifications {
    fun showError(project: Project, msg: String) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup(NOTIFICATION_GROUP)
            .createNotification(msg, NotificationType.ERROR)
            .notify(project)
    }
}
