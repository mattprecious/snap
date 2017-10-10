package com.mattprecious.imagetools

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Takes an image and runs edge detection on its luminance channel. Four images will be created:
 * - One using the [Laplace operator](https://en.wikipedia.org/wiki/Discrete_Laplace_operator)
 * - One using the horizontal approximation of the [Sobel operator](https://en.wikipedia.org/wiki/Sobel_operator)
 * - One using the vertical approximation of the [Sobel operator](https://en.wikipedia.org/wiki/Sobel_operator)
 * - One for the combined [Sobel](https://en.wikipedia.org/wiki/Sobel_operator) approximations
 */
object EdgeDetector {
  @JvmStatic
  fun main(args: Array<String>) {
    val image = ImageIO.read(File("card.jpg"))
    val yChannel = YuvSplitter.getChannel(image, YuvSplitter.Channel.Y)

    ImageIO.write(detectEdges(yChannel, Kernel.LAPLACE), "jpg",
        File("out_edges_laplace.jpg"))
    ImageIO.write(detectEdges(yChannel, Kernel.SOBEL_HORIZONTAL), "jpg",
        File("out_edges_sobel_horizontal.png"))
    ImageIO.write(detectEdges(yChannel, Kernel.SOBEL_VERTICAL), "jpg",
        File("out_edges_sobel_vertical.png"))
    ImageIO.write(detectEdges(yChannel, Kernel.SOBEL_BOTH), "jpg",
        File("out_edges_sobel_combined.png"))
  }

  fun detectEdges(image: BufferedImage, kernel: Kernel): BufferedImage {
    val out = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)

    for (y in 1 until image.height - 1) {
      for (x in 1 until image.width - 1) {
        val accumulator = when (kernel) {
          Kernel.LAPLACE -> applyLaplacian(image, x, y)
          Kernel.SOBEL_HORIZONTAL -> applySobelHorizontal(image, x, y)
          Kernel.SOBEL_VERTICAL -> applySobelVertical(image, x, y)
          Kernel.SOBEL_BOTH -> {
            val horizontal = applySobelHorizontal(image, x, y)
            val vertical = applySobelVertical(image, x, y)

            Math.hypot(horizontal.toDouble(), vertical.toDouble()).toInt()
          }
        }

        val absAccumulator = Math.abs(accumulator)
        // Lazy solution to make this fit into an RGB representation. In practice you may be
        // throwing away important information by doing this.
        val clamped = absAccumulator.clamp(0, 255)
        val rgb = clamped.toRgbGray()

        out.setRGB(x, y, rgb)
      }
    }

    return out
  }

  enum class Kernel {
    LAPLACE,
    SOBEL_HORIZONTAL,
    SOBEL_VERTICAL,
    SOBEL_BOTH
  }

  private fun applyLaplacian(image: BufferedImage, x: Int, y: Int): Int {
    // Kernel:
    // 0  1  0
    // 1 -4  1
    // 0  1  0
    return (1 * image.getValueFromRgbGray(x, y - 1)
        + 1 * image.getValueFromRgbGray(x - 1, y)
        + -4 * image.getValueFromRgbGray(x, y)
        + 1 * image.getValueFromRgbGray(x + 1, y)
        + 1 * image.getValueFromRgbGray(x, y + 1))
  }

  private fun applySobelHorizontal(image: BufferedImage, x: Int, y: Int): Int {
    // Kernel:
    // -1 0 1
    // -2 0 2
    // -1 0 1
    return (-1 * image.getValueFromRgbGray(x - 1, y - 1)
        + 1 * image.getValueFromRgbGray(x + 1, y - 1)
        + -2 * image.getValueFromRgbGray(x - 1, y)
        + 2 * image.getValueFromRgbGray(x + 1, y)
        + -1 * image.getValueFromRgbGray(x - 1, y + 1)
        + 1 * image.getValueFromRgbGray(x + 1, y + 1))
  }

  private fun applySobelVertical(image: BufferedImage, x: Int, y: Int): Int {
    // Kernel:
    // -1 -2 -1
    //  0  0  0
    //  1  2  1
    return (-1 * image.getValueFromRgbGray(x - 1, y - 1)
        + -2 * image.getValueFromRgbGray(x, y - 1)
        + -1 * image.getValueFromRgbGray(x + 1, y - 1)
        + 1 * image.getValueFromRgbGray(x - 1, y + 1)
        + 2 * image.getValueFromRgbGray(x, y + 1)
        + 1 * image.getValueFromRgbGray(x + 1, y + 1))
  }
}