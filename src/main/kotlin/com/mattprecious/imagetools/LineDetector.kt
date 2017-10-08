package com.mattprecious.imagetools

import java.awt.BasicStroke
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Detects lines in the luminance channel of an image using a
 * [Hough transform](https://en.wikipedia.org/wiki/Hough_transform).
 *
 * The results are exported as a scatter plot with `theta` as the x-axis and `r` as the y-axis.
 * Lines that meet our confidence threshold are drawn back on top of the input and are exported
 * as a separate image.
 */
object LineDetector {
  @JvmStatic
  fun main(args: Array<String>) {
    val image = ImageIO.read(File("card.jpg"))
    val yChannel = YuvSplitter.getChannel(image, YuvSplitter.Channel.Y)
    val edges = EdgeDetector.detectEdges(yChannel, EdgeDetector.Kernel.SOBEL_BOTH)

    val result = detectLines(edges)
    ImageIO.write(result.first, "jpg", File("out_lines_plot.jpg"))

    // Draw the lines back onto the input image.
    for ((r, theta) in result.second) {
      image.drawLineRTheta(r, theta)
    }

    ImageIO.write(image, "jpg", File("out_lines.jpg"))
  }

  private fun detectLines(image: BufferedImage): Pair<BufferedImage, Set<Line>> {
    val rRange = (Math.hypot(image.width.toDouble(), image.height.toDouble()) * 2).toInt()
    val thetaBuckets = 180
    val thetaStep = Math.PI / thetaBuckets

    // Don't use an image for this in practice.
    val histogram = BufferedImage(thetaBuckets, rRange, BufferedImage.TYPE_INT_RGB)

    val lines = mutableSetOf<Line>()

    for (y in 0 until image.height) {
      for (x in 0 until image.width) {
        val v = image.getValueFromRgbGray(x, y)
        // Check if the pixel is strong enough to be considered an edge.
        // An arbitrary value was chosen for this script. In practice, this should probably be based
        // on the size of the input image.
        if (v < 128) continue

        for (thetaBucket in 0 until thetaBuckets) {
          val theta = thetaBucket * thetaStep
          val r = computeR(theta, x, y)

          // Once again, an arbitrary value was chosen as a threshold for this script.
          if (increment(histogram, r, rRange, thetaBucket) == 128) {
            lines.add(Line(r, theta))
          }
        }
      }
    }

    return histogram to lines
  }

  private fun increment(accumulator: BufferedImage, r: Double, rRange: Int, thetaBucket: Int): Int {
    // r can be negative, so add half the range to bring it above zero.
    val rInt = (r + 0.5 + rRange / 2).toInt()

    // Lazy solution to make this fit into an RGB representation. In practice you may be
    // throwing away important information by doing this.
    val rClamped = rInt.clamp(0, rRange - 1)

    val newValue = accumulator.getValueFromRgbGray(thetaBucket, rClamped) + 1
    accumulator.setRGB(thetaBucket, rClamped, newValue.toRgbGray())
    return newValue
  }

  private fun computeR(theta: Double, x: Int, y: Int): Double {
    return x * Math.cos(theta) + y * Math.sin(theta)
  }

  private fun BufferedImage.drawLineRTheta(r: Double, theta: Double) {
    val sinTheta = Math.sin(theta)
    val cosTheta = Math.cos(theta)
    if (sinTheta != 0.0) {
      val x1 = 0
      val x2 = width
      val y1 = (r - x1 * cosTheta) / sinTheta
      val y2 = (r - x2 * cosTheta) / sinTheta
      drawLine(x1, y1.toInt(), x2, y2.toInt())
    } else {
      val y1 = 0
      val y2 = height
      val x1 = (r - y1 * sinTheta) / cosTheta
      val x2 = (r - y2 * sinTheta) / cosTheta
      drawLine(x1.toInt(), y1, x2.toInt(), y2)
    }
  }

  private fun BufferedImage.drawLine(x1: Int, y1: Int, x2: Int, y2: Int) {
    val graphics2D = graphics as Graphics2D
    graphics2D.color = Color.RED
    graphics2D.stroke = BasicStroke(2f)
    graphics2D.drawLine(x1, y1, x2, y2)
    graphics2D.dispose()
  }

  private data class Line(val r: Double, val theta: Double)
}