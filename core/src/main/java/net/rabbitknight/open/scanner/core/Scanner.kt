package net.rabbitknight.open.scanner.core

import android.os.Handler
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.rabbitknight.open.scanner.core.source.Source
import java.util.concurrent.BlockingQueue

interface Scanner {
    /**
     * 配置
     */
    fun setConfig(config: Config)

    /**
     * 设置数据源
     */
    fun setSource(source: Source)

    /**
     * 获取结果
     */
    fun getResult(): BlockingQueue<ImageResult>

    /**
     * 获取结果
     */
    fun getResult(handler: Handler? = null, callback: (ImageResult) -> Unit)

    /**
     * 开始检测
     */
    fun start()

    /**
     * 结束检测
     */
    fun stop()
}