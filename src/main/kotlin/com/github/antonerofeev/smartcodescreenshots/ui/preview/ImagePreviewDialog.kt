package com.github.antonerofeev.smartcodescreenshots.ui.preview

import com.github.antonerofeev.smartcodescreenshots.utils.Clipboard
import com.github.antonerofeev.smartcodescreenshots.utils.ImageUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent
import java.awt.image.BufferedImage
import javax.swing.AbstractAction
import javax.swing.ActionMap
import javax.swing.InputMap
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.KeyStroke
import javax.swing.SwingUtilities

class ImagePreviewDialog(private val project: Project, private val image: BufferedImage) : DialogWrapper(project) {

    init {
        title = "Screenshot Preview"
        isResizable = true
        init()
    }

    override fun createCenterPanel(): JComponent {
        val root = JPanel(BorderLayout())

        val toolbar = JPanel()
        val saveBtn = JButton("Save")
        val copyBtn = JButton("Copy")
        val zoomInBtn = JButton("+")
        val zoomOutBtn = JButton("-")
        val fitBtn = JButton("Fit")

        toolbar.add(saveBtn)
        toolbar.add(copyBtn)
        toolbar.add(zoomInBtn)
        toolbar.add(zoomOutBtn)
        toolbar.add(fitBtn)

        root.add(toolbar, BorderLayout.NORTH)

        val imagePanel = ImagePanel(image)

        root.add(imagePanel, BorderLayout.CENTER)

        saveBtn.addActionListener {
            ImageUtil.saveToFile(image, project)
        }

        copyBtn.addActionListener {
            Clipboard.copy(image)
        }

        zoomInBtn.addActionListener {
            imagePanel.zoomBy(1.1)
        }

        zoomOutBtn.addActionListener {
            imagePanel.zoomBy(1.0 / 1.1)
        }

        fitBtn.addActionListener {
            imagePanel.computeFit()
        }

        // Keyboard shortcuts for +/-
        val inputMap: InputMap = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
        val actionMap: ActionMap = root.actionMap
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, 0), "zoomIn")
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, KeyEvent.SHIFT_DOWN_MASK), "zoomIn")
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, 0), "zoomOut")
        actionMap.put("zoomIn", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                imagePanel.zoomBy(1.1)
            }
        })
        actionMap.put("zoomOut", object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent?) {
                imagePanel.zoomBy(1.0 / 1.1)
            }
        })

        SwingUtilities.invokeLater { imagePanel.computeFit() }
        return root
    }

    fun showCentered() {
        if (SwingUtilities.isEventDispatchThread()) {
            show()
        } else {
            SwingUtilities.invokeLater { show() }
        }
    }
}