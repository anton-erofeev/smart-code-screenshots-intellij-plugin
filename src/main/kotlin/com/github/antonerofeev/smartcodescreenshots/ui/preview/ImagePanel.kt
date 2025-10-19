package com.github.antonerofeev.smartcodescreenshots.ui.preview

import java.awt.Cursor
import java.awt.Dimension
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.Image
import java.awt.Point
import java.awt.RenderingHints
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseWheelEvent
import java.awt.image.BufferedImage
import javax.swing.JComponent
import kotlin.math.pow

class ImagePanel(initialImage: BufferedImage) : JComponent() {
    private var image: BufferedImage = initialImage

    private var scale = 1.0
    private var offsetX = 0
    private var offsetY = 0
    private var lastDrag: Point? = null
    private var fitToWindow = true

    init {
        preferredSize = Dimension(800, 600)

        addMouseListener(object : MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                lastDrag = e.point
                cursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR)
            }

            override fun mouseReleased(e: MouseEvent) {
                lastDrag = null
                cursor = Cursor.getDefaultCursor()
            }
        })

        addMouseMotionListener(object : MouseAdapter() {
            override fun mouseDragged(e: MouseEvent) {
                val p = lastDrag ?: return
                val dx = e.x - p.x
                val dy = e.y - p.y
                offsetX += dx
                offsetY += dy
                lastDrag = e.point
                fitToWindow = false
                repaint()
            }
        })

        addMouseWheelListener { e: MouseWheelEvent ->
            if (e.isControlDown) {
                val oldScale = scale
                val factor = 1.1.pow(-e.wheelRotation.toDouble())
                scale = (scale * factor).coerceIn(0.1, 10.0)

                val centerX = width / 2
                val centerY = height / 2

                val mx = e.x
                val my = e.y

                val imgX = (mx - centerX - offsetX) / oldScale
                val imgY = (my - centerY - offsetY) / oldScale

                offsetX = (mx - centerX - (imgX * scale)).toInt()
                offsetY = (my - centerY - (imgY * scale)).toInt()

                fitToWindow = false
                repaint()
            }
        }

        addComponentListener(object : ComponentAdapter() {
            override fun componentResized(e: ComponentEvent) {
                if (fitToWindow) {
                    computeFit()
                }
            }
        })
    }

    fun computeFit() {
        if (width <= 0 || height <= 0) return
        scale = (width.toDouble() / image.width).coerceAtMost(height.toDouble() / image.height)
        offsetX = 0
        offsetY = 0
        repaint()
    }

    fun zoomBy(factor: Double, centerX: Int? = null, centerY: Int? = null) {
        val old = scale
        scale = (scale * factor).coerceIn(0.1, 10.0)

        val cx = centerX ?: (width / 2)
        val cy = centerY ?: (height / 2)
        val imgCX = (cx - offsetX) / old
        val imgCY = (cy - offsetY) / old
        offsetX = (cx - imgCX * scale).toInt()
        offsetY = (cy - imgCY * scale).toInt()
        fitToWindow = false
        repaint()
    }

    override fun paintComponent(graphics: Graphics) {
        super.paintComponent(graphics)
        val g2 = graphics as Graphics2D
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)

        if (width <= 0 || height <= 0) return

        if (fitToWindow) {
            scale = (width.toDouble() / image.width).coerceAtMost(height.toDouble() / image.height)
            offsetX = 0
            offsetY = 0
        }

        val drawWith = (image.width * scale).toInt()
        val drawHeight = (image.height * scale).toInt()

        val cx = (width - drawWith) / 2
        val cy = (height - drawHeight) / 2

        val x = cx + offsetX
        val y = cy + offsetY

        val scaled: Image = image.getScaledInstance(drawWith, drawHeight, Image.SCALE_SMOOTH)
        g2.drawImage(scaled, x, y, drawWith, drawHeight, null)
    }
}
