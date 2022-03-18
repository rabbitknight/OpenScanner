package net.rabbitknight.open.scanner.core.engine

import android.content.Context
import net.rabbitknight.open.scanner.core.format.BarcodeFormat
import net.rabbitknight.open.scanner.core.image.ImageFormat
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.result.ImageResult

/**
 * Engine的行为
 * 对于任何引擎的实现类，
 * 1. 只希望它提供支持的条码
 * 2. 只希望它对提供的图像计算出结果 而不做任何裁切/处理
 * 3. 需要告知倾向使用的图像格式
 */
interface Engine {

    fun init(context: Context)

    fun release()

    /**
     * 是否引擎支持format
     */
    fun supportBarFormat(format: BarcodeFormat): Boolean

    /**
     * 设置检测的格式
     */
    fun setBarFormat(vararg format: BarcodeFormat)

    /**
     * 解码
     */
    fun decode(image: ImageWrapper<Any>): ImageResult

    /**
     * 倾向的图像格式
     */
    @ImageFormat.Format
    fun preferImageFormat(): String
}