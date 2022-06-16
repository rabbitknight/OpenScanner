package net.rabbitknight.open.scanner.core.impl

import android.os.Handler
import net.rabbitknight.open.scanner.core.ScanResultListener
import net.rabbitknight.open.scanner.core.Scanner
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.config.InitOption
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.engine.EngineModule
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.image.pool.ByteArrayPool
import net.rabbitknight.open.scanner.core.process.InputModule
import net.rabbitknight.open.scanner.core.process.OutputModule

class ScannerImpl(private val initOption: InitOption, val engines: Array<Class<out Engine>>) : Scanner {
    private val inputModule: InputModule = InputModule()
    private val engineModule = EngineModule(engines)
    private val outputModule = OutputModule()
    private val modules = listOf(inputModule, engineModule, outputModule)

    private val cachePoolMap = mutableMapOf<String, ByteArrayPool>()

    init {
        // 设置缓存池
        modules.forEach {
            it.cachePool = cachePoolMap.getOrPut(it.moduleName()) {
                ByteArrayPool()
            }
        }
        // 流串联
        inputModule.setSink(engineModule.getSource())
        engineModule.setSink(outputModule.getSource())

        // 模块启动
        modules.forEach { it.onCreate(initOption) }
    }

    override fun setConfig(config: Config) {
        modules.forEach { it.onConfig(config) }
    }

    override fun process(image: ImageWrapper<Any>, frameListener: ScanResultListener): Boolean {
        return inputModule.process(image, frameListener)
    }

    override fun getResult(
        handler: Handler?,
        callback: ScanResultListener
    ) = outputModule.getOutput(handler!!, callback)

    override fun release() {
        modules.forEach { it.onDestroy() }
    }
}