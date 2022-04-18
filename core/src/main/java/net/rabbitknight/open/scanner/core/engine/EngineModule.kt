package net.rabbitknight.open.scanner.core.engine

import net.rabbitknight.open.scanner.core.ContextProvider
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.lifecycle.IModule
import net.rabbitknight.open.scanner.core.process.ImageFrame
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class EngineModule(val engines: Array<Class<out Engine>>) : IModule {
    private val engineImpls = hashMapOf<Class<out Engine>, Engine>()
    private val source = LinkedBlockingQueue<ImageFrame>()
    private var sink: BlockingQueue<ImageFrame>? = null

    override fun onConfig(config: Config) {
    }

    override fun onStart() {
        engines.forEach {
            val impl = it.newInstance()
            engineImpls[it] = impl

            impl.init(ContextProvider.context())
        }
    }

    override fun onStop() {
        engineImpls.forEach {
            val impl = it.value

            impl.release()
        }
        engineImpls.clear()
    }

    override fun onStep() {
        // 图像转换
        // 检测
        // 识别
    }

    fun getSource(): BlockingQueue<ImageFrame> = source

    fun setSink(blockingQueue: BlockingQueue<ImageFrame>) {
        this.sink = blockingQueue
    }
}