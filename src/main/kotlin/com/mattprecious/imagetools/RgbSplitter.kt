package com.mattprecious.imagetools

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Takes an image and splits it into red, green, and blue channels, saving each channel into a
 * separate image.
 */
object RgbSplitter {
  @JvmStatic
  fun main(args: Array<String>) {
    val image = ImageIO.read(File("mcfly.jpg"))
    ImageIO.write(getChannel(image, Channel.RED), "jpg", File("out_rgb_r.jpg"))
    ImageIO.write(getChannel(image, Channel.GREEN), "jpg", File("out_rgb_g.jpg"))
    ImageIO.write(getChannel(image, Channel.BLUE), "jpg", File("out_rgb_b.jpg"))
  }

  private fun getChannel(image: BufferedImage, channel: Channel): BufferedImage {
    val out = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
    for (y in 0 until image.height) {
      for (x in 0 until image.width) {
        out.setRGB(x, y, channel.extract(image.getRGB(x, y)))
      }
    }

    return out
  }

  enum class Channel(private val mask: Int) {
    RED(0xff0000),
    GREEN(0x00ff00),
    BLUE(0x0000ff);

    fun extract(color: Int) = color and mask
  }
}