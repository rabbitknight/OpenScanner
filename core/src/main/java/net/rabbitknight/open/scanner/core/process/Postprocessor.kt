package net.rabbitknight.open.scanner.core.process

import android.os.Handler
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.lifecycle.IModule
import net.rabbitknight.open.scanner.core.result.ImageResult
import java.util.concurrent.LinkedBlockingQueue

class Postprocessor() : IModule {
    private var resultListener: Pair<Handler?, (ImageResult) -> Unit>? = null
    private val source = LinkedBlockingQueue<ImageFrame>()

    fun getOutput(handler: Handler?, callback: (ImageResult) -> Unit) {
        resultListener = Pair(handler, callback)
    }

    fun getSource() = source

    override fun onConfig(config: Config) {
    }

    override fun onStart() {
    }

    override fun onStop() {

    }

    override fun onStep() {
    }
}