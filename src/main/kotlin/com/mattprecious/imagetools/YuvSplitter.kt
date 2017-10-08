package com.mattprecious.imagetools

import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * Takes an image and splits it into the three channels of the YCbCr format: luminance,
 * blue-difference chroma, and red-difference chroma. Each channel is saved to a separate image.
 *
 * See:
 * - [https://en.wikipedia.org/wiki/YUV](https://en.wikipedia.org/wiki/YUV)
 * - [https://en.wikipedia.org/wiki/YCbCr](https://en.wikipedia.org/wiki/YCbCr)
 * - [http://www.equasys.de/colorconversion.html](http://www.equasys.de/colorconversion.html)
 */
object YuvSplitter {
  @JvmStatic
  fun main(args: Array<String>) {
    val image = ImageIO.read(File("mcfly.jpg"))
    ImageIO.write(getChannel(image, Channel.Y), "jpg", File("out_yuv_y.jpg"))
    ImageIO.write(getChannel(image, Channel.U), "jpg", File("out_yuv_u.jpg"))
    ImageIO.write(getChannel(image, Channel.V), "jpg", File("out_yuv_v.jpg"))
  }

  fun getChannel(image: BufferedImage, channel: Channel): BufferedImage {
    val out = BufferedImage(image.width, image.height, BufferedImage.TYPE_INT_RGB)
    for (y in 0 until image.height) {
      for (x in 0 until image.width) {
        out.setRGB(x, y, channel.extractAsRgb(image.getRGB(x, y)))
      }
    }

    return out
  }

  enum class Channel {
    Y,
    U,
    V;

    fun extractAsRgb(color: Int): Int {
      val r = (color and 0xff0000) shr 16
      val g = (color and 0x00ff00) shr 8
      val b = color and 0x0000ff

      // Since we're only converting one channel at a time, we need a default value for each
      // channel. Y, U, and V have a range of [0, 255] so 128 is our empty value for all 3 channels.
      // http://www.equasys.de/colorconversion.html
      var y = 128
      var u = 128
      var v = 128

      // RGB to YCbCr conversion, but only converting one channel.
      when (this) {
        Y -> y = (0.299f * r + 0.587f * g + 0.114f * b).toInt()
        U -> u = 128 + (-0.168736f * r - 0.331264f * g + 0.5f * b).toInt()
        V -> v = 128 + (0.5f * r - 0.418688f * g - 0.081312f * b).toInt()
      }

      // YCbCr to RGB conversion.
      val newR = (y + 1.402f * (v - 128)).toInt()
      val newG = (y - 0.344136f * (u - 128) - 0.714136f * (v - 128)).toInt()
      val newB = (y + 1.772f * (u - 128)).toInt()

      val rgb = ((newR and 0xFF) shl 16) or ((newG and 0xFF) shl 8) or (newB and 0xFF)
      return rgb
    }
  }
}
