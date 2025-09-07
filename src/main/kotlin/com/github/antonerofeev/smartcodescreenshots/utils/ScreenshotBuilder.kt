package com.github.antonerofeev.smartcodescreenshots.utils

import com.github.antonerofeev.smartcodescreenshots.utils.SettingsState
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.*
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.util.TextRange
import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import com.intellij.ui.scale.JBUIScale
import java.awt.*
import java.awt.geom.AffineTransform
import java.awt.geom.Point2D
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import javax.swing.JComponent

import com.github.antonerofeev.smartcodescreenshots.utils.Constants.GREEN
import com.github.antonerofeev.smartcodescreenshots.utils.Constants.RED
import com.github.antonerofeev.smartcodescreenshots.utils.Constants.YELLOW
import com.intellij.codeInsight.hint.EditorFragmentComponent.getBackgroundColor

class ScreenshotBuilder(
    private val editor: Editor,
    private val fileName: String?
) {
    companion object {
        private val LOG = Logger.getInstance(ScreenshotBuilder::class.java)

        private const val WINDOW_CONTROL_DIAMETER = 10
        private const val WINDOW_CONTROL_PADDING = 6
        private const val FILE_NAME_FONT_SIZE = 13
        private val FILE_NAME_SHADOW = Color(0, 0, 0, 50)

        private fun getRange(editor: Editor): TextRange {
            val sel = editor.selectionModel
            return TextRange(sel.selectionStart, sel.selectionEnd)
        }
    }

    fun createImage(): BufferedImage? {
        val range = getRange(editor)
        val document = editor.document

        val state = EditorState.from(editor)

        return try {
            resetEditor()
            val options = SettingsState.getInstance().state
            val scale = options.scale

            val contentComponent = editor.contentComponent
            val g = contentComponent.graphics as Graphics2D
            val at = AffineTransform(g.transform)
            at.scale(scale, scale)

            // flush glyph cache
            paint(contentComponent, at, 1, 1, JBColor.BLACK, options, 0)

            val selectionRect = getSelectionRectangle(range, document.getText(range), options)
            at.translate(-selectionRect.x, -selectionRect.y)

            paint(
                contentComponent,
                at,
                (selectionRect.width * scale).toInt(),
                (selectionRect.height * scale).toInt(),
                getBackgroundColor(editor, false),
                options,
                (options.innerPadding * scale).toInt()
            )
        } catch (e: Exception) {
            LOG.error("Failed to create screenshot", e)
            null
        } finally {
            state.restore(editor)
        }
    }

    private fun paint(
        contentComponent: JComponent,
        at: AffineTransform,
        width: Int,
        height: Int,
        backgroundColor: Color,
        state: SettingsState.State,
        innerPadding: Int
    ): BufferedImage {
        // TODO: rework as BuildImageParameters function
        val outerPaddingH = (state.outerPaddingHoriz * state.scale).toInt()
        val outerPaddingV = (state.outerPaddingVert * state.scale).toInt()
        val indicatorSize = WINDOW_CONTROL_DIAMETER * state.scale
        val windowControlsPadding = WINDOW_CONTROL_PADDING * state.scale
        val sysScale = JBUIScale.sysScale(contentComponent)

        val paddingX = innerPadding + outerPaddingH
        val paddingY = innerPadding + outerPaddingV

        val fileNameFont = Font("SansSerif", Font.PLAIN, (FILE_NAME_FONT_SIZE * state.scale).toInt())
        val metrics = contentComponent.getFontMetrics(fileNameFont)
        val fileNameHeight = metrics.height

        val controlsHeight = windowControlsPadding + indicatorSize + windowControlsPadding
        val titleBarHeight = maxOf(controlsHeight, fileNameHeight + windowControlsPadding * 2)
        val topPadding = if (hasTitleBar(state)) maxOf(titleBarHeight, innerPadding.toDouble()) else innerPadding.toDouble()

        val imgWidth = (outerPaddingH + innerPadding + width * sysScale + innerPadding + outerPaddingH).toInt()
        val imgHeight = (outerPaddingV + (if (hasTitleBar(state)) topPadding else innerPadding.toDouble()) +
                height * sysScale + innerPadding + outerPaddingV).toInt()

        val img = BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_ARGB)
        val g = prepareGraphics(img)

        drawWindowBackground(g, imgWidth, imgHeight, state, outerPaddingH, outerPaddingV, backgroundColor)
        val xOffset = drawWindowControls(g, state, outerPaddingH, outerPaddingV, titleBarHeight, indicatorSize, windowControlsPadding)
        drawFileName(g, state, fileName, fileNameFont, metrics, outerPaddingH, outerPaddingV, xOffset, titleBarHeight)

        paintEditorContent(
            g, contentComponent, at, width, height, paddingX,
            if (hasTitleBar(state)) outerPaddingV + topPadding else paddingY.toDouble()
        )

        g.dispose()
        return img
    }

    private fun prepareGraphics(img: BufferedImage): Graphics2D =
        img.createGraphics().apply {
            setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
            setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)
        }

    private fun drawWindowBackground(
        g: Graphics2D,
        imgWidth: Int,
        imgHeight: Int,
        state: SettingsState.State,
        outerPaddingH: Int,
        outerPaddingV: Int,
        backgroundColor: Color
    ) {
        g.paint = state.getBackgroundJbColor()
        g.fillRect(0, 0, imgWidth, imgHeight)

        g.paint = backgroundColor
        val roundness = (state.windowRoundness * state.scale).toInt()
        g.fillRoundRect(
            outerPaddingH, outerPaddingV,
            imgWidth - outerPaddingH * 2,
            imgHeight - outerPaddingV * 2,
            roundness, roundness
        )
    }

    private fun drawWindowControls(
        g: Graphics2D,
        state: SettingsState.State,
        outerPaddingH: Int,
        outerPaddingV: Int,
        titleBarHeight: Double,
        indicatorSize: Double,
        padding: Double
    ): Double {
        if (!state.showWindowControls) return 0.0

        var xOffset = 0.0
        for (color in arrayOf(RED, YELLOW, GREEN)) {
            g.paint = color
            g.fillOval(
                (outerPaddingH + padding + xOffset).toInt(),
                (outerPaddingV + (titleBarHeight - indicatorSize) / 2).toInt(),
                indicatorSize.toInt(),
                indicatorSize.toInt()
            )
            xOffset += indicatorSize + padding
        }
        return xOffset
    }

    private fun drawFileName(
        g: Graphics2D,
        state: SettingsState.State,
        fileName: String?,
        font: Font,
        metrics: FontMetrics,
        outerPaddingH: Int,
        outerPaddingV: Int,
        xOffset: Double,
        titleBarHeight: Double
    ) {
        if (!state.showFileName || fileName.isNullOrEmpty()) return

        g.font = font

        val textY = (outerPaddingV + (titleBarHeight + metrics.ascent - metrics.descent) / 2).toInt()
        val textX = (outerPaddingH + xOffset + WINDOW_CONTROL_PADDING * state.scale).toInt()

        g.paint = FILE_NAME_SHADOW
        g.drawString(fileName, textX + 1, textY + 1)

        g.paint = Gray._220
        g.drawString(fileName, textX, textY)
    }

    private fun paintEditorContent(
        g: Graphics2D,
        contentComponent: JComponent,
        at: AffineTransform,
        width: Int,
        height: Int,
        paddingX: Int,
        paddingY: Double
    ) {
        val scaledWidth = (width * JBUIScale.sysScale(contentComponent)).toInt()
        val scaledHeight = (height * JBUIScale.sysScale(contentComponent)).toInt()

        g.translate(paddingX.toDouble(), paddingY)
        g.clipRect(0, 0, scaledWidth, scaledHeight)
        g.transform(at)
        contentComponent.paint(g)
    }

    private fun hasTitleBar(state: SettingsState.State): Boolean =
        state.showWindowControls || state.showFileName

    private fun resetEditor() {
        val document = editor.document
        val range = getRange(editor)
        editor.selectionModel.setSelection(0, 0)

        val offset = if (range.startOffset == 0) {
            document.getLineEndOffset(document.lineCount - 1)
        } else 0
        editor.caretModel.moveToOffset(offset)

        if (editor is EditorEx) {
            editor.setCaretEnabled(false)
        }
        editor.settings.isCaretRowShown = false
    }

    private fun getSelectionRectangle(
        range: TextRange,
        text: String,
        options: SettingsState.State
    ): Rectangle2D {
        val start = range.startOffset
        val end = range.endOffset
        val r = Rectangle2D.Double()


        for (i in start until end) {
            addCharBounds(r, i, text, range, options)
        }

        for (inlay in editor.inlayModel.getInlineElementsInRange(start, end)) {
            val bounds = inlay.bounds
            if (bounds != null) r.add(bounds)
        }
        return r
    }

    private fun addCharBounds(
        r: Rectangle2D,
        offset: Int,
        text: String,
        range: TextRange,
        options: SettingsState.State
    ) {
        if (options.removeIndentation && text[offset - range.startOffset].isWhitespace()) return

        val point = editor.offsetToXY(offset, false, false)
        includePoint(r, point)
        includePoint(r, Point2D.Double(point.x.toDouble(), point.y + editor.lineHeight.toDouble()))

        val next = editor.offsetToXY(offset + 1, false, true)
        includePoint(r, next)
        includePoint(r, Point2D.Double(next.x.toDouble(), next.y + editor.lineHeight.toDouble()))
    }

    private fun includePoint(r: Rectangle2D, p: Point2D) {
        if (r.isEmpty) {
            r.setFrame(p, Dimension(1, 1))
        } else {
            r.add(p)
        }
    }

    data class EditorState(
        val range: TextRange,
        val offset: Int,
        val caretRow: Boolean
    ) {
        companion object {
            fun from(editor: Editor): EditorState =
                EditorState(
                    getRange(editor),
                    editor.caretModel.offset,
                    editor.settings.isCaretRowShown
                )
        }

        fun restore(editor: Editor) {
            editor.settings.isCaretRowShown = caretRow

            if (editor is EditorEx) {
                editor.setCaretEnabled(true)
            }

            val caretModel = editor.caretModel
            caretModel.moveToOffset(offset)

            val selectionModel = editor.selectionModel
            selectionModel.setSelection(range.startOffset, range.endOffset)
        }
    }

}
