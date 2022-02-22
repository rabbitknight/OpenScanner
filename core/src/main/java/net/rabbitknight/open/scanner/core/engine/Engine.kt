package net.rabbitknight.open.scanner.core.engine

import net.rabbitknight.open.scanner.core.format.BarcodeFormat
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.result.ImageResult

interface Engine {
    /**
     * 是否引擎支持format
     */
    fun supportFormat(format: BarcodeFormat): Boolean

    /**
     * 设置检测的格式
     */
    fun setFormat(vararg format: BarcodeFormat)

    /**
     * 解码
     */
    fun decode(image: ImageWrapper): ImageResult
}