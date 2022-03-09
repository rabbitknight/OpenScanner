package net.rabbitknight.open.scanner.core.preprocess

import android.content.Context
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.lifecycle.IModule
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

/**
 * 预处理引擎
 * + 根据取景框的配置 对图像数据进行剪裁
 * + 对原始的数据进行处理 处理成符合各个引擎的数据
 */
class Preprocessor(context: Context) : IModule {
    private lateinit var config: Config
    private var source = LinkedBlockingQueue<ImageWrapper>()
    private var sink: BlockingQueue<ImageWrapper>? = null

    override fun onConfig(config: Config) {
        this.config = config
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onStep() {

    }

    fun process(image: ImageWrapper): Boolean {
        val size = source.size
        if (size > config.inputCapacity) {
            return false
        }
        return source.offer(image)
    }

    fun setSink(blockingQueue: BlockingQueue<ImageWrapper>) {
        this.sink = blockingQueue
    }
}