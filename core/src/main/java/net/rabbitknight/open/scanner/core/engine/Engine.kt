package net.rabbitknight.open.scanner.core.engine

import net.rabbitknight.open.scanner.core.format.BarcodeFormat
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.rabbitknight.open.scanner.core.source.Source
import java.util.concurrent.BlockingQueue

interface Engine {
    /**
     * 是否引擎支持format
     */
    fun supportFormat(format: BarcodeFormat): Boolean

    /**
     * 设置检测的格式
     */
    fun setFormat(vararg format: BarcodeFormat)

    /**
     * 设置数据源
     */
    fun setSource(source: Source)

    /**
     * 获取结果
     */
    fun getResult(): BlockingQueue<ImageResult>

    /**
     * 开始检测
     */
    fun start()

    /**
     * 结束检测
     */
    fun stop()
}