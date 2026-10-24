package com.github.antonerofeev.smartcodescreenshots.ui.preview

import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import javax.swing.SwingUtilities
import org.junit.Assert.assertEquals
import org.junit.Test

class ImagePanelTest {
    @Test
    fun `zoom keeps the selected point anchored`() {
        SwingUtilities.invokeAndWait {
            val panel = ImagePanel(createTestImage())
            panel.setSize(100, 100)
            panel.computeFit()
            panel.zoomBy(2.0, centerX = 30, centerY = 50)

            val rendered = render(panel)

            assertEquals(Color.RED.rgb, rendered.getRGB(30, 50))
        }
    }

    @Test
    fun `fit mode recalculates scale after resize`() {
        SwingUtilities.invokeAndWait {
            val panel = ImagePanel(createTestImage())
            panel.setSize(100, 100)
            panel.computeFit()
            panel.zoomBy(2.0)
            panel.computeFit()
            panel.setSize(200, 100)

            val rendered = render(panel)

            assertEquals(Color.BLUE.rgb, rendered.getRGB(150, 50))
        }
    }

    private fun createTestImage(): BufferedImage =
        BufferedImage(20, 10, BufferedImage.TYPE_INT_RGB).apply {
            val graphics = createGraphics()
            graphics.color = Color.RED
            graphics.fillRect(0, 0, 10, height)
            graphics.color = Color.BLUE
            graphics.fillRect(10, 0, 10, height)
            graphics.dispose()
        }

    private fun render(panel: ImagePanel): BufferedImage =
        BufferedImage(panel.width, panel.height, BufferedImage.TYPE_INT_RGB).also { image ->
            val graphics: Graphics2D = image.createGraphics()
            panel.paint(graphics)
            graphics.dispose()
        }
}
