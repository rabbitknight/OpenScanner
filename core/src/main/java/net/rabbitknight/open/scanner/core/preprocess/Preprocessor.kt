package net.rabbitknight.open.scanner.core.preprocess

import android.content.Context
import net.rabbitknight.open.scanner.core.config.ScannerConfig
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.lifecycle.IModule
import net.rabbitknight.open.scanner.core.source.Source
import java.util.concurrent.BlockingQueue

/**
 * 预处理引擎
 * + 根据取景框的配置 对图像数据进行剪裁
 * + 对原始的数据进行处理 处理成符合各个引擎的数据
 */
class Preprocessor(context: Context) : IModule {
    private lateinit var config: ScannerConfig
    private var source: Source? = null
    private var sink: BlockingQueue<ImageWrapper>? = null

    override fun onConfig(config: ScannerConfig) {
        this.config = config
    }

    override fun onStart() {
    }

    override fun onStop() {
    }

    override fun onStep() {

    }

    fun setSource(source: Source) {
        this.source = source
    }

    fun setSink(blockingQueue: BlockingQueue<ImageWrapper>) {
        this.sink = blockingQueue
    }
}