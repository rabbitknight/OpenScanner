package net.rabbitknight.open.scanner.core.process

import net.rabbitknight.open.scanner.core.image.ImageFormat
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.result.BarcodeResult
import net.rabbitknight.open.scanner.core.result.Rect

/**
 * 中间图像
 */
data class ImageFrame(
    /**
     * 原始数据
     */
    val raw: ImageWrapper<Any>,
    /**
     * 剪裁数据
     */
    val cropImage: ImageWrapper<ByteArray>,
    /**
     * 剪裁框
     */
    val cropRect: Rect,

    /**
     * 格式转换
     */
    val cvtImage: MutableMap<@ImageFormat.Format String, ImageWrapper<Any>> = mutableMapOf(),
    /**
     * 处理后的兴趣区域
     */
    val rois: MutableList<Rect> = mutableListOf(),

    /**
     * 该帧检测结果
     */
    val result: MutableList<BarcodeResult> = mutableListOf(),
)