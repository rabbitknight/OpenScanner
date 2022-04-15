package net.rabbitknight.open.scanner.core

import android.os.Handler
import android.os.Looper
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.image.pool.ByteBufferPool
import net.rabbitknight.open.scanner.core.impl.ScannerImpl
import net.rabbitknight.open.scanner.core.result.ImageResult

class OpenScanner private constructor(
    engines: Array<Class<out Engine>>
) : Scanner {
    companion object {
        /**
         * 共享缓存池
         */
        val sharedBufferPool = ByteBufferPool()

        fun create(engines: Array<Class<out Engine>>): OpenScanner {
            return OpenScanner(engines)
        }
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    private val scannerImpl = ScannerImpl(engines)

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

    override fun release() {
        mainHandler.post { scannerImpl.release() }
    }

}