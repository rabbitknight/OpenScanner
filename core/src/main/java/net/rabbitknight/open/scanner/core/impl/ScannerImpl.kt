package net.rabbitknight.open.scanner.core.impl

import android.os.Handler
import net.rabbitknight.open.scanner.core.Scanner
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.engine.EngineModule
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.process.OutputProcessor
import net.rabbitknight.open.scanner.core.process.InputProcessor
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.rabbitknight.open.scanner.core.thread.CoreThread

class ScannerImpl(val engines: Array<Class<out Engine>>) : Scanner, Runnable {
    private val coreThread = CoreThread(this)
    private val preprocessor: InputProcessor = InputProcessor()
    private val engineModule = EngineModule(engines)
    private val postprocessor = OutputProcessor()
    private val modules = listOf(coreThread, preprocessor, engineModule, postprocessor)

    init {
        // 流串联
        preprocessor.setSink(engineModule.getSource())
        engineModule.setSink(postprocessor.getSource())

        // 模块启动
        modules.forEach { it.onInit() }
    }

    override fun setConfig(config: Config) {
        modules.forEach { it.onConfig(config) }
    }

    override fun process(image: ImageWrapper<Any>): Boolean {
        return preprocessor.process(image)
    }

    override fun getResult(handler: Handler?, callback: (ImageResult) -> Unit) =
        postprocessor.getOutput(handler!!, callback)

    override fun release() {
        modules.forEach { it.onDestroy() }
    }

    override fun run() {
        modules.forEach { it.onStep() }
    }
}