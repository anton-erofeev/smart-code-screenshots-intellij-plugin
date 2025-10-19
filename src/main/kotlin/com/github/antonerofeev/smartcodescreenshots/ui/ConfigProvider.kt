package com.github.antonerofeev.smartcodescreenshots.ui

import com.github.antonerofeev.smartcodescreenshots.utils.SettingsState
import com.github.antonerofeev.smartcodescreenshots.ui.settings.SettingsUI
import com.github.antonerofeev.smartcodescreenshots.utils.Constants.PLUGIN_NAME
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.util.NlsContexts
import javax.swing.JComponent

class ConfigProvider : SearchableConfigurable, Configurable.NoScroll {

    private var panel: SettingsUI? = null

    override fun getId(): String = "code-screenshots"

    override fun getDisplayName(): @NlsContexts.ConfigurableName String = PLUGIN_NAME

    override fun createComponent(): JComponent? {
        panel = SettingsUI().apply { init() }
        return panel?.getPanel()
    }

    override fun isModified(): Boolean {
        val service = SettingsState.getInstance()
        return panel?.let { service.state != it.toState() } ?: false
    }

    override fun apply() {
        val service = SettingsState.getInstance()
        panel?.let { service.loadState(it.toState()) }
    }

    override fun reset() {
        val service = SettingsState.getInstance()
        panel?.fromState(service.state)
    }

    override fun disposeUIResources() {
        panel = null
    }
}