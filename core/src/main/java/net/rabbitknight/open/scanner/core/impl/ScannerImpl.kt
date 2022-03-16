package net.rabbitknight.open.scanner.core.impl

import android.content.Context
import android.os.Handler
import net.rabbitknight.open.scanner.core.Scanner
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.engine.EngineModule
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.process.Postprocessor
import net.rabbitknight.open.scanner.core.process.Preprocessor
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.rabbitknight.open.scanner.core.thread.CoreThread

class ScannerImpl(context: Context, vararg engines: Class<out Engine>) : Scanner, Runnable {
    private val coreThread = CoreThread(this)
    private val preprocessor: Preprocessor = Preprocessor(context)
    private val engineModule = EngineModule(*engines)
    private val postprocessor = Postprocessor(context)
    private val modules = listOf(coreThread, preprocessor, engineModule, postprocessor)

    init {
        preprocessor.setSink(engineModule.getSource())
        engineModule.setSink(postprocessor.getSource())
    }

    override fun setConfig(config: Config) {
        modules.forEach { it.onConfig(config) }
    }

    override fun process(image: ImageWrapper<Any>): Boolean {
        return preprocessor.process(image)
    }

    override fun getResult(handler: Handler?, callback: (ImageResult) -> Unit) =
        postprocessor.getOutput(handler, callback)

    override fun start() {
        modules.forEach { it.onStart() }
    }

    override fun stop() {
        modules.forEach { it.onStop() }
    }

    override fun run() {
        modules.forEach { it.onStep() }
    }
}