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
import net.rabbitknight.open.scanner.core.process.base.BaseModule

class ScannerImpl(
    private val initOption: InitOption,
    private val engines: Array<Class<out Engine>>
) :
    Scanner {
    private val inputModule = InputModule(this)
    private val engineModule = EngineModule(this)
    private val outputModule = OutputModule(this)
    private val modules = listOf(inputModule, engineModule, outputModule)

    private val cachePoolMap = mutableMapOf<String, ByteArrayPool>()

    private var config = Config()

    internal fun getConfig() = config

    internal fun getEngines() = engines

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

    override fun config(config: Config) {
        this.config = config
        modules.forEach { it.onConfigChanged(config) }
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