package net.rabbitknight.open.scanner.core

import android.os.Handler
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.result.ImageResult

interface Scanner {
    /**
     * 配置
     */
    fun setConfig(config: Config)

    /**
     * 处理图像
     */
    fun process(image: ImageWrapper): Boolean

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