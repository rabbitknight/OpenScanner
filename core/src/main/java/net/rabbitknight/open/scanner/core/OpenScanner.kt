package net.rabbitknight.open.scanner.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.impl.ScannerImpl
import net.rabbitknight.open.scanner.core.result.ImageResult

class OpenScanner private constructor(
    context: Context,
    engines: Array<out Class<out Engine>>
) : Scanner {
    companion object {
        fun create(context: Context, vararg engines: Class<out Engine>): OpenScanner {
            return OpenScanner(context.applicationContext, engines)
        }
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    private val scannerImpl = ScannerImpl(context, *engines)

    /**
     * 配置
     */
    override fun setConfig(config: Config) {
        mainHandler.post {
            scannerImpl.setConfig(config)
        }
    }

    override fun process(image: ImageWrapper<Any>): Boolean {
        return scannerImpl.process(image)
    }

    /**
     * 获取结果
     */
    override fun getResult(handler: Handler?, callback: (ImageResult) -> Unit) {
        scannerImpl.getResult(handler ?: mainHandler, callback)
    }

    /**
     * 开始检测
     */
    override fun start() {
        mainHandler.post { scannerImpl.start() }
    }

    /**
     * 结束检测
     */
    override fun stop() {
        mainHandler.post { scannerImpl.stop() }
    }

}