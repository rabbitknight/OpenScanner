package net.rabbitknight.open.scanner.core.process

import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.image.WrapperOwner
import net.rabbitknight.open.scanner.core.image.pool.ByteArrayPool
import net.rabbitknight.open.scanner.core.lifecycle.IModule
import net.rabbitknight.open.scanner.core.result.*
import net.rabbitknight.open.scanner.core.utils.ImageUtils
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

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
class Preprocessor() : IModule {
    private lateinit var config: Config
    private var source = LinkedBlockingQueue<ImageWrapper<Any>>()
    private lateinit var sink: BlockingQueue<ImageFrame>

    private val byteArrayCache = ByteArrayPool()

    override fun onConfig(config: Config) {
        this.config = config
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onStep() {
        val nextImage = source.take()
        nextImage ?: return

        val config = this.config
        // 计算剪裁
        val finderRect = config.finderRect
        val finderTolerance = config.finderTolerance
        val cropRect: Rect = let {
            val imageWidth = nextImage.width
            val imageHeight = nextImage.height
            val centerX = finderRect.centerX()
            val centerY = finderRect.centerY()
            val halfWidth = (finderRect.right - finderRect.left) * (1 + finderTolerance) / 2
            val halfHeight = (finderRect.bottom - finderRect.top) * (1 + finderTolerance) / 2
            val crop = Rect(
                ((centerX - halfWidth).coerceAtLeast(0.0f) * imageWidth).toInt(),
                ((centerY - halfHeight).coerceAtLeast(0.0f) * imageHeight).toInt(),
                ((centerX + halfWidth).coerceAtMost(1.0f) * imageWidth).toInt(),
                ((centerY + halfHeight).coerceAtMost(1.0f) * imageHeight).toInt()
            )
            crop
        }
        // 缓存申请
        val cache = byteArrayCache.acquire(cropRect.width(), cropRect.height(), nextImage.format)
        // 图像剪裁
        val cropImage = ImageUtils.cropImage(
            nextImage, cropRecycleOwner,
            cropRect.left, cropRect.top, cropRect.width(), cropRect.height(),
            cache
        )
        // 封装imageFrame
        val imageFrame = ImageFrame(
            nextImage, cropImage, cropRect,
        )
        // 输出
        sink.offer(imageFrame)
    }

    fun process(image: ImageWrapper<Any>): Boolean {
        val size = source.size
        if (size > config.inputCapacity) {
            return false
        }
        return source.offer(image)
    }

    fun setSink(sink: BlockingQueue<ImageFrame>) {
        this.sink = sink
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