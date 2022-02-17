package net.rabbitknight.open.scanner.core

import android.os.Handler
import net.rabbitknight.open.scanner.core.config.ScannerConfig
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.rabbitknight.open.scanner.core.source.Source
import java.util.concurrent.BlockingQueue

class OpenScanner private constructor(engines: Array<out Class<out Engine>>) : Scanner {
    companion object {
        fun create(vararg engines: Class<out Engine>): OpenScanner {
            return OpenScanner(engines)
        }
    }

    /**
     * 配置
     */
    override fun setConfig(config: ScannerConfig) {
        TODO("Not yet implemented")
    }

    /**
     * 设置数据源
     */
    override fun setSource(source: Source) {
        TODO("Not yet implemented")
    }

    /**
     * 获取结果
     */
    override fun getResult(): BlockingQueue<ImageResult> {
        TODO("Not yet implemented")
    }

    /**
     * 获取结果
     */
    override fun getResult(handler: Handler?, callback: (ImageResult) -> Unit) {
        TODO("Not yet implemented")
    }

    /**
     * 开始检测
     */
    override fun start() {
        TODO("Not yet implemented")
    }

    /**
     * 结束检测
     */
    override fun stop() {
        TODO("Not yet implemented")
    }

}