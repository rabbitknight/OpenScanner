package net.rabbitknight.open.scanner.core.process

import net.rabbitknight.open.scanner.core.ScanResultListener
import net.rabbitknight.open.scanner.core.ScannerException
import net.rabbitknight.open.scanner.core.image.ImageFormat
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.image.WrapperOwner
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.rabbitknight.open.scanner.core.result.Rect
import java.lang.ref.WeakReference

/**
 * 中间图像
 */
data class ImageFrame(
    /**
     * 原始数据
     */
    val raw: ImageWrapper<Any>,

    /**
     * 帧回调
     */
    val frameListener: WeakReference<ScanResultListener>,
    /**
     * 剪裁数据
     */
    var cropImage: ImageWrapper<ByteArray> = NullImageWrapper,
    /**
     * 剪裁框
     */
    val cropRect: Rect,

    /**
     * 时间戳
     */
    val timestamp: Long,

    /**
     * 格式转换
     */
    val cvtImage: MutableMap<@ImageFormat.Format String, ImageWrapper<ByteArray>> = mutableMapOf(),
    /**
     * 处理后的兴趣区域
     */
    val rois: MutableList<Rect> = mutableListOf(),

    /**
     * 该帧检测结果
     */
    val result: MutableList<ImageResult> = mutableListOf(),
)


object NullImageWrapper : ImageWrapper<ByteArray> {
    override val owner: WrapperOwner<out ByteArray>
        get() = throw ScannerException("NullImageWrapper:BOOM!")
    override val format: String
        get() = throw ScannerException("NullImageWrapper:BOOM!")
    override val width: Int
        get() = throw ScannerException("NullImageWrapper:BOOM!")
    override val height: Int
        get() = throw ScannerException("NullImageWrapper:BOOM!")
    override val timestamp: Long
        get() = throw ScannerException("NullImageWrapper:BOOM!")
    override val planes: Array<ImageWrapper.PlaneWrapper>
        get() = throw ScannerException("NullImageWrapper:BOOM!")
    override val payload: ByteArray
        get() = throw ScannerException("NullImageWrapper:BOOM!")

    override fun close() {
        // no-op
    }

}