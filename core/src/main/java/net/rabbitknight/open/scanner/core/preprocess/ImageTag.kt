package net.rabbitknight.open.scanner.core.preprocess

import net.rabbitknight.open.scanner.core.result.Rect

/**
 * 给图像打的标记
 */
data class ImageTag(
    val rois: List<Rect>
)