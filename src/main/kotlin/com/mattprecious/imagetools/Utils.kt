package com.mattprecious.imagetools

import java.awt.image.BufferedImage

@Suppress("NOTHING_TO_INLINE") // See InlineTest.
inline fun BufferedImage.getValueFromRgbGray(x: Int, y: Int): Int {
  // R, G, and B are the same because it's grayscale, so just take the blue value.
  return getRGB(x, y) and 0x0000ff
}

fun Int.toRgbGray(): Int {
  return (this shl 16) or (this shl 8) or this
}

fun Int.clamp(min: Int, max: Int): Int {
  if (this < min) return min
  if (this > max) return max
  return this
}