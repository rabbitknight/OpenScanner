package net.rabbitknight.open.scanner.core.process.base

import android.util.Log
import androidx.annotation.CallSuper
import net.rabbitknight.open.scanner.core.C
import net.rabbitknight.open.scanner.core.C.SCHEDULE_PERIOD_MILS
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.config.InitOption
import net.rabbitknight.open.scanner.core.image.pool.ByteArrayPool
import net.rabbitknight.open.scanner.core.impl.ScannerImpl
import net.rabbitknight.open.scanner.core.process.ImageFrame
import java.util.concurrent.BlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

abstract class BaseModule(val scanner: ScannerImpl) : IModule {
    private var source = LinkedBlockingQueue<ImageFrame>()
    private lateinit var sink: BlockingQueue<ImageFrame>

    private lateinit var executor: ScheduledExecutorService
    private lateinit var processFuture: ScheduledFuture<*>

    internal lateinit var cachePool: ByteArrayPool

    private lateinit var initOption: InitOption

    /**
     * 模块名
     */
    open fun moduleName(): String = this.javaClass.simpleName

    @CallSuper
    override fun onCreate(option: InitOption) {
        this.initOption = option
        executor = Executors.newScheduledThreadPool(1)
        processFuture = executor.scheduleAtFixedRate(
            processThread,
            0L,
            SCHEDULE_PERIOD_MILS,
            TimeUnit.MILLISECONDS
        )
    }

    @CallSuper
    override fun onDestroy() {
        processFuture.cancel(true)
        executor.shutdown()
    }

    override fun onConfigChanged(config: Config) {
    }

    protected fun getOption() = initOption

    /**
     * 获取数据源
     */
    fun getSource(): BlockingQueue<ImageFrame> = source

    /**
     * 获取输出
     */
    fun getSink(): BlockingQueue<ImageFrame> = sink

    /**
     * 设置输出
     */
    fun setSink(sink: BlockingQueue<ImageFrame>) {
        this.sink = sink
    }

    // region 缓存池
    /**
     * 获取缓存
     */
    protected fun acquire(width: Int, height: Int, format: String): ByteArray {
        return cachePool.acquire(width, height, format)
    }

    /**
     * 释放缓存
     */
    protected fun release(cache: ByteArray) {
        cachePool.release(cache)
    }
    // endregion 缓存池

    /**
     * 核心处理
     */
    private val processThread = Runnable {
        // 线程重命名
        Thread.currentThread().name = C.TAG.substring(0, 4) + ":" + moduleName()

        val frame: ImageFrame? = try {
            source.take()
        } catch (e: Exception) {
            Log.w(moduleName(), "take frame err: ", e)
            null
        }
        frame ?: return@Runnable

        try {
            // 处理
            onProcess(frame)
        } catch (e: Exception) {
            Log.w(moduleName(), "process frame err: ", e)
        }
    }
}