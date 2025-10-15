package com.github.antonerofeev.smartcodescreenshots.ui.settings

import com.github.antonerofeev.smartcodescreenshots.utils.SettingsState
import com.github.antonerofeev.smartcodescreenshots.utils.Constants.BACKGROUND_COLOR
import com.github.antonerofeev.smartcodescreenshots.utils.WatermarkStyle
import com.intellij.ui.ColorChooserService
import com.intellij.ui.JBColor
import java.awt.Color
import java.util.*
import javax.swing.*

class SettingsUI {
    companion object {
        private const val SLIDER_SCALE = 0.25
    }

    private lateinit var ABB8C3FormattedTextField: JFormattedTextField
    private lateinit var dataVis: JLabel
    private lateinit var editButton: JButton
    private lateinit var innerPaddingInp: JSlider
    private lateinit var innerPaddingVis: JLabel
    private lateinit var outerPaddingInp: JSlider
    private lateinit var outerPaddingVis: JLabel
    private lateinit var panel: JPanel
    private lateinit var removeIndent: JCheckBox
    private lateinit var roundnessVis: JLabel
    private lateinit var scaleInp: JSlider
    private lateinit var showFileName: JCheckBox
    private lateinit var showWindowControls: JCheckBox
    private lateinit var windowRoundnessInp: JSlider
    private lateinit var showWatermark: JCheckBox
    private lateinit var watermarkText: JTextField
    private lateinit var watermarkStyle: JComboBox<String>

    private var initialBgColor: Color = BACKGROUND_COLOR

    fun init() {
        watermarkStyle.model = DefaultComboBoxModel(WatermarkStyle.entries.map { it.displayName }.toTypedArray())
        
        updateWatermarkFieldsState()
        
        showWatermark.addActionListener {
            updateWatermarkFieldsState()
        }
        
        scaleInp.addChangeListener {
            dataVis.text = String.format(Locale.ENGLISH, "%.2f", scaleInp.value * SLIDER_SCALE)
        }
        innerPaddingInp.addChangeListener {
            innerPaddingVis.text = innerPaddingInp.value.toString()
        }
        outerPaddingInp.addChangeListener {
            outerPaddingVis.text = outerPaddingInp.value.toString()
        }
        windowRoundnessInp.addChangeListener {
            roundnessVis.text = windowRoundnessInp.value.toString()
        }
        editButton.addActionListener {
            val chosen = ColorChooserService.instance
                .showDialog(editButton, "Choose a Color", initialBgColor, true)
            if (chosen != null) {
                initialBgColor = chosen
                updateColorPreview()
            }
        }
        updateColorPreview()
    }

    private fun updateColorPreview() {
        ABB8C3FormattedTextField.text = formatColor(initialBgColor)
        ABB8C3FormattedTextField.foreground = JBColor(
            Color(initialBgColor.red, initialBgColor.green, initialBgColor.blue),
            Color(initialBgColor.red, initialBgColor.green, initialBgColor.blue)
        )
    }

    fun toState(): SettingsState.State =
        SettingsState.State().apply {
            scale = scaleInp.value * SLIDER_SCALE
            removeIndentation = removeIndent.isSelected
            innerPadding = innerPaddingInp.value.toDouble()
            outerPaddingVert = outerPaddingInp.value.toDouble()
            outerPaddingHoriz = outerPaddingInp.value.toDouble()
            windowRoundness = windowRoundnessInp.value
            showWindowControls = this@SettingsUI.showWindowControls.isSelected
            showFileName = this@SettingsUI.showFileName.isSelected
            backgroundColor = initialBgColor.rgb
            showWatermark = this@SettingsUI.showWatermark.isSelected
            watermarkText = this@SettingsUI.watermarkText.text
            watermarkStyle = WatermarkStyle.fromDisplayName(this@SettingsUI.watermarkStyle.selectedItem as String)
        }

    fun fromState(state: SettingsState.State) {
        scaleInp.value = (state.scale / SLIDER_SCALE).toInt()
        removeIndent.isSelected = state.removeIndentation
        innerPaddingInp.value = state.innerPadding.toInt()
        outerPaddingInp.value = state.outerPaddingHoriz.toInt()
        windowRoundnessInp.value = state.windowRoundness
        showWindowControls.isSelected = state.showWindowControls
        showFileName.isSelected = state.showFileName
        showWatermark.isSelected = state.showWatermark
        watermarkText.text = state.watermarkText
        watermarkStyle.selectedItem = state.watermarkStyle.displayName
        initialBgColor = state.getBackgroundJbColor()
        updateColorPreview()
        updateWatermarkFieldsState()
    }

    fun getPanel(): JPanel = panel

    private fun updateWatermarkFieldsState() {
        watermarkText.isEnabled = showWatermark.isSelected
        watermarkStyle.isEnabled = showWatermark.isSelected
    }

    private fun formatColor(c: Color): String =
        String.format(
            Locale.ENGLISH,
            "A: %02.0f%%, R: %02.0f%%, G: %02.0f%%, B: %02.0f%%",
            c.alpha / 255f * 100,
            c.red / 255f * 100,
            c.green / 255f * 100,
            c.blue / 255f * 100
        )
}
