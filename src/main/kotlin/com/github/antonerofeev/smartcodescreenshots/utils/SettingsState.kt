package com.github.antonerofeev.smartcodescreenshots.utils

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.SettingsCategory
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.ui.JBColor
import com.intellij.util.xmlb.XmlSerializerUtil
import java.awt.Color

@State(
    name = "CodeScreenshotsOptions",
    storages = [Storage("codeScreenshots.xml")],
    category = SettingsCategory.PLUGINS
)
class SettingsState : PersistentStateComponent<SettingsState.State> {

    var dataState: State = State()

    companion object {
        fun getInstance(): SettingsState =
            ApplicationManager.getApplication().getService(SettingsState::class.java)
    }

    override fun getState(): State = dataState

    override fun loadState(state: State) {
        XmlSerializerUtil.copyBean(state, this.dataState)
    }

    data class State(
        var scale: Double = 1.5,
        var removeIndentation: Boolean = true,
        var innerPadding: Double = 16.0,
        var outerPaddingHoriz: Double = 10.0,
        var outerPaddingVert: Double = 10.0,
        var windowRoundness: Int = 10,
        var showWindowControls: Boolean = true,
        var backgroundColor: Int = 0xffabb8c3.toInt(),
        var showFileName: Boolean = true
    ) {
        fun getBackgroundJbColor(): Color =
            JBColor(Color(backgroundColor, true), Color(backgroundColor, true))
    }
}