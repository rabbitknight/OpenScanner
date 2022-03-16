package net.rabbitknight.open.scanner.core.process

import android.content.Context
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.lifecycle.IModule
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

/**
 * 预处理引擎
 * + 根据取景框的配置 对图像数据进行剪裁
 * + 对原始的数据进行处理 处理成[net.rabbitknight.open.scanner.core.C.Y8]的数据
 * + 根据晃动检测模块的开关 判断是否处理图像
 */
class Preprocessor(context: Context) : IModule {
    private lateinit var config: Config
    private var source = LinkedBlockingQueue<ImageWrapper<Any>>()
    private var sink: BlockingQueue<ImageFrame>? = null

    override fun onConfig(config: Config) {
        this.config = config
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onStep() {
        // TODO: 晃动检测
        val nextImage = source.poll()
        // TODO: 根据取景框剪裁
        // TODO: 二维码检测
        // TODO: camera 协作器
        // TODO: 标记/输出到下游模块
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