package net.rabbitknight.open.scanner.core.process

import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.rabbitknight.open.scanner.core.result.Rect

/**
 * 中间图像
 */
data class ImageFrame(
    /**
     * 原始数据
     */
    val raw: ImageWrapper,
    /**
     * 剪裁数据
     */
    val cropImage: ImageWrapper,
    /**
     * 建材矿
     */
    val cropRect: Rect,
    /**
     * 处理后的兴趣区域
     */
    val rois: List<Rect>,

    /**
     * 该帧检测结果
     */
    val result: ImageResult?,
)