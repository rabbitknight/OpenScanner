package net.rabbitknight.open.scanner.core.process

import android.util.Log
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.image.WrapperOwner
import net.rabbitknight.open.scanner.core.image.wrap
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
    companion object {
        private const val TAG = "InputModule"
    }

    private lateinit var config: Config

    override fun onConfig(config: Config) {
        this.config = config
    }

    override fun moduleName(): String = TAG

    /**
     * 图像接入入口
     */
    fun process(image: ImageWrapper<Any>): Boolean {
        val config = this.config

        // 处理队列判断有无溢出
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
        val nextImage = frame.raw
        val cropRect = frame.cropRect

        // 缓存申请
        val cache = this.acquire(cropRect.width(), cropRect.height(), nextImage.format)
        val timestamp = frame.timestamp
        val cropWidth = cropRect.width()
        val cropHeight = cropRect.height()
        // 图像剪裁
        ImageUtils.cropImage(
            nextImage,
            cropRect.left, cropRect.top, cropWidth, cropHeight,
            cache
        ) { rst, format ->
            if (!rst) {
                Log.e(TAG, "cropImage: rect@${cropRect},to@${format},from@${nextImage},fail!!")
            }
            // 通过Unit调用...有点奇葩..
            val cropImage = cache.wrap(cropOwner, format, cropWidth, cropHeight, timestamp)
            frame.cropImage = cropImage
        }

        // 释放原始图像,以后的模块只会使用cropImage
        frame.raw.close()

        // 输出
        getSink().offer(frame)
    }

    /**
     * cropImage回收会到这个接口此处完成真正的数据回收
     */
    private val cropOwner = object : WrapperOwner<ByteArray> {
        override fun close(payload: ByteArray) {
            release(payload)
        }
    }
}