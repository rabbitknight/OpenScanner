package net.rabbitknight.open.scanner.core.engine

import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.lifecycle.IModule
import net.rabbitknight.open.scanner.core.process.ImageFrame
import java.util.concurrent.BlockingQueue
import java.util.concurrent.LinkedBlockingQueue

class EngineModule(val engines: Array<Class<out Engine>>) : IModule {
    private val engineImpls = hashMapOf<Class<out Engine>, Engine>()
    private val source = LinkedBlockingQueue<ImageFrame>()
    private var sink: BlockingQueue<ImageFrame>? = null

    init {
        engines.forEach {
            val impl = it.newInstance()
            engineImpls[it] = impl
        }
    }

    override fun onConfig(config: Config) {
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onStep() {

    }

    fun getSource(): BlockingQueue<ImageFrame> = source

    fun setSink(blockingQueue: BlockingQueue<ImageFrame>) {
        this.sink = blockingQueue
    }
}