/*
 * Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */

package org.intellij.datavis.r.inlays.components

import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.IndexColorModel
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

class ImageInverter(foreground: Color, background: Color) {
  private val rgb = FloatArray(3)
  private val hsl = FloatArray(3)
  private val whiteHsl = FloatArray(3)
  private val blackHsl = FloatArray(3)

  init {
    foreground.getRGBColorComponents(rgb)
    convertRGBtoHSL(rgb, whiteHsl)
    background.getRGBColorComponents(rgb)
    convertRGBtoHSL(rgb, blackHsl)
  }

  fun invert(color: Color): Color {
    val alpha = invert(color.rgb)
    val argb = convertHSLtoRGB(hsl, alpha)
    return Color(argb, true)
  }

  fun invert(content: ByteArray): ByteArray {
    val image = ImageIO.read(ByteArrayInputStream(content)) ?: return content
    val outputImage = invert(image)
    return ByteArrayOutputStream().use { outputStream ->
      ImageIO.write(outputImage, "png", outputStream)
      outputStream.flush()
      outputStream.toByteArray()
    }
  }

  private fun invert(image: BufferedImage): BufferedImage {
    return createImageWithInvertedPalette(image).also { outputImage ->
      for (x in 0 until image.width) {
        for (y in 0 until image.height) {
          val argb = image.getRGB(x, y)
          val alpha = invert(argb)
          outputImage.setRGB(x, y, convertHSLtoRGB(hsl, alpha))
        }
      }
    }
  }

  private fun createImageWithInvertedPalette(image: BufferedImage): BufferedImage {
    val model = image.colorModel
    if (model !is IndexColorModel) {
      return image
    }
    val palette = IntArray(model.mapSize)
    model.getRGBs(palette)
    for ((index, argb) in palette.withIndex()) {
      val alpha = invert(argb)
      palette[index] = convertHSLtoRGB(hsl, alpha)
    }
    val outputModel = IndexColorModel(
      model.pixelSize,
      model.mapSize,
      palette,
      0,
      model.hasAlpha(),
      model.transparentPixel,
      model.transferType
    )
    return BufferedImage(image.width, image.height, BufferedImage.TYPE_BYTE_INDEXED, outputModel)
  }

  // Note: returns alpha, resulting color resides in `hsl`
  private fun invert(argb: Int): Float {
    val alpha = ((argb shr 24) and 255) / 255f
    rgb[R] = ((argb shr 16) and 255) / 255f
    rgb[G] = ((argb shr 8) and 255) / 255f
    rgb[B] = ((argb) and 255) / 255f
    convertRGBtoHSL(rgb, hsl)
    hsl[SATURATION] = hsl[SATURATION] * (50.0f + whiteHsl[SATURATION]) / 1.5f / 100f
    hsl[LUMINANCE] = (100 - hsl[LUMINANCE]) * (whiteHsl[LUMINANCE] - blackHsl[LUMINANCE]) / 100f  + blackHsl[LUMINANCE]
    return alpha
  }

  companion object {
    private const val SATURATION = 1
    private const val LUMINANCE = 2
    private const val R = 0
    private const val G = 1
    private const val B = 2
  }
}
