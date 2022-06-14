package net.rabbitknight.open.scanner.core.process

import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.image.WrapperOwner
import net.rabbitknight.open.scanner.core.image.pool.ByteArrayPool
import net.rabbitknight.open.scanner.core.process.base.BaseModule
import net.rabbitknight.open.scanner.core.result.*
import net.rabbitknight.open.scanner.core.utils.ImageUtils

/**
 * 预处理引擎
 * 根据取景框的配置 对图像数据进行剪裁
 * 将所有输入的乱七八糟的图形格式，转换为ByteArray格式，规则如下
 *  + Bitmap -> ARGB
 *  + RGBA -> ARGB
 *  + YUV_420 -> YV12
 *  + NV21 -> YV12
 * 根据晃动检测模块的开关 判断是否处理图像
 */
class InputModule() : BaseModule() {
    private lateinit var config: Config
    private val byteArrayCache = ByteArrayPool()

    override fun onConfig(config: Config) {
        this.config = config
    }

    fun process(image: ImageWrapper<Any>): Boolean {
        val config = this.config

        val size = getSource().size
        if (size > config.inputCapacity) {
            return false
        }

        // 计算剪裁区域，只有在剪裁区域的图像才是需要被剪裁的
        val finderRect = config.finderRect
        val finderTolerance = config.finderTolerance
        val cropRect = finderRect.let {
            val centerX = finderRect.centerX()
            val centerY = finderRect.centerY()
            val halfWidth = finderRect.width() * (1 + finderTolerance) / 2
            val halfHeight = finderRect.height() * (1 + finderTolerance) / 2
            val crop = Rect(
                ((centerX - halfWidth).coerceAtLeast(0.0f) * image.width).toInt(),
                ((centerY - halfHeight).coerceAtLeast(0.0f) * image.height).toInt(),
                ((centerX + halfWidth).coerceAtMost(1.0f) * image.width).toInt(),
                ((centerY + halfHeight).coerceAtMost(1.0f) * image.height).toInt()
            )
            crop
        }
        val timestamp = image.timestamp
        val frame = ImageFrame(image, cropRect = cropRect, timestamp = timestamp)

        return getSource().offer(frame)
    }

    override fun onProcess(frame: ImageFrame) {
        val nextImage = frame.cropImage
        val cropRect = frame.cropRect

        // 缓存申请
        val cache = byteArrayCache.acquire(cropRect.width(), cropRect.height(), nextImage.format)
        // 图像剪裁
        val cropImage = ImageUtils.cropImage(
            nextImage, cropRecycleOwner,
            cropRect.left, cropRect.top, cropRect.width(), cropRect.height(),
            cache
        )
        frame.let {
            it.cropImage = cropImage
        }

        // 输出
        getSink().offer(frame)
    }

    /**
     * cropImage回收会到这个接口此处完成真正的数据回收
     */
    private val cropRecycleOwner = object : WrapperOwner<ByteArray> {
        override fun close(payload: ByteArray) {
            byteArrayCache.release(payload)
        }
    }
}