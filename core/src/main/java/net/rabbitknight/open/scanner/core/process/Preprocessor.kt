package net.rabbitknight.open.scanner.core.process

import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.lifecycle.IModule
import net.rabbitknight.open.scanner.core.result.RectF
import net.rabbitknight.open.scanner.core.result.centerX
import net.rabbitknight.open.scanner.core.result.centerY
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

/**
 * 预处理引擎
 * + 根据取景框的配置 对图像数据进行剪裁
 * + 对原始的数据进行处理 处理成[net.rabbitknight.open.scanner.core.C.Y8]的数据
 * + 根据晃动检测模块的开关 判断是否处理图像
 */
class Preprocessor() : IModule {
    private lateinit var config: Config
    private var source = LinkedBlockingQueue<ImageWrapper<Any>>()
    private lateinit var sink: BlockingQueue<ImageFrame>

    override fun onConfig(config: Config) {
        this.config = config
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onStep() {
        val nextImage = source.poll()
        nextImage ?: return

        val config = this.config
        // 计算剪裁
        val finderRect = config.finderRect
        val finderTolerance = config.finderTolerance
        val cropRect: RectF = let {
            val centerX = finderRect.centerX()
            val centerY = finderRect.centerY()
            val halfWidth = (finderRect.right - finderRect.left) * (1 + finderTolerance) / 2
            val halfHeight = (finderRect.bottom - finderRect.top) * (1 + finderTolerance) / 2
            val crop = RectF(
                (centerX - halfWidth).coerceAtLeast(0.0f),
                (centerY - halfHeight).coerceAtLeast(0.0f),
                (centerX + halfWidth).coerceAtMost(1.0f),
                (centerY + halfHeight).coerceAtMost(1.0f)
            )
            crop
        }
// todo
//        val frame = ImageFrame(
//            nextImage,
//            )
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
}