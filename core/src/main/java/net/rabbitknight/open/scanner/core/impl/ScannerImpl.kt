package net.rabbitknight.open.scanner.core.impl

import android.content.Context
import android.os.Handler
import net.rabbitknight.open.scanner.core.Scanner
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.engine.EngineModule
import net.rabbitknight.open.scanner.core.preprocess.Preprocessor
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.rabbitknight.open.scanner.core.source.Source
import net.rabbitknight.open.scanner.core.thread.CoreThread
import java.util.concurrent.BlockingQueue

class ScannerImpl(context: Context, vararg engines: Class<out Engine>) : Scanner, Runnable {
    private val coreThread = CoreThread(this)
    private val preprocessor: Preprocessor = Preprocessor(context)
    private val engineModule = EngineModule(*engines)
    private val modules = listOf(coreThread, preprocessor, engineModule)

    override fun setConfig(config: Config) {
        modules.forEach { it.onConfig(config) }
    }

    override fun setSource(source: Source) {
        preprocessor.setSink(engineModule.getInput())
        preprocessor.setSource(source)
    }

    override fun getResult(): BlockingQueue<ImageResult> = engineModule.getOutput()

    override fun getResult(handler: Handler?, callback: (ImageResult) -> Unit) =
        engineModule.getOutput(handler, callback)

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