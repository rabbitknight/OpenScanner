package net.rabbitknight.open.scanner.core.impl

import android.os.Handler
import net.rabbitknight.open.scanner.core.Scanner
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.engine.EngineModule
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.process.InputProcessor
import net.rabbitknight.open.scanner.core.process.OutputProcessor
import net.rabbitknight.open.scanner.core.result.ImageResult

class ScannerImpl(val engines: Array<Class<out Engine>>) : Scanner {
    private val inputModule: InputProcessor = InputProcessor()
    private val engineModule = EngineModule(engines)
    private val outputModule = OutputProcessor()
    private val modules = listOf(inputModule, engineModule, outputModule)

    init {
        // 流串联
        inputModule.setSink(engineModule.getSource())
        engineModule.setSink(outputModule.getSource())

        // 模块启动
        modules.forEach { it.onCreate() }
    }

    override fun setConfig(config: Config) {
        modules.forEach { it.onConfig(config) }
    }

    override fun process(image: ImageWrapper<Any>): Boolean {
        return inputModule.process(image)
    }

    override fun getResult(handler: Handler?, callback: (ImageResult) -> Unit) =
        outputModule.getOutput(handler!!, callback)

    override fun release() {
        modules.forEach { it.onDestroy() }
    }
}