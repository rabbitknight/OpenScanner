package net.rabbitknight.open.scanner.core.process.base

import android.util.Log
import net.rabbitknight.open.scanner.core.C
import net.rabbitknight.open.scanner.core.C.SCHEDULE_PERIOD_MILS
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.process.ImageFrame
import java.util.concurrent.*

abstract class BaseModule : IModule {
    private var source = LinkedBlockingQueue<ImageFrame>()
    private lateinit var sink: BlockingQueue<ImageFrame>

    private lateinit var executor: ScheduledExecutorService
    private lateinit var processFuture: ScheduledFuture<*>


    /**
     * 模块名
     */
    open fun moduleName(): String = this.javaClass.simpleName

    override fun onCreate() {
        executor = Executors.newScheduledThreadPool(1)
        processFuture = executor.scheduleAtFixedRate(
            processThread,
            0L,
            SCHEDULE_PERIOD_MILS,
            TimeUnit.MILLISECONDS
        )
    }

    override fun onDestroy() {
        processFuture.cancel(true)
        executor.shutdown()
    }

    override fun onConfig(config: Config) {
    }


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

    /**
     * 核心处理
     */
    private val processThread = Runnable {
        // 线程重命名
        Thread.currentThread().name = C.TAG.substring(0, 4) + ":" + moduleName()

        val frame: ImageFrame? = try {
            source.take()
        } catch (e: Exception) {
            Log.e(moduleName(), "take frame err: ", e)
            null
        }
        frame ?: return@Runnable

        try {
            // 处理
            onProcess(frame)
        } catch (e: Exception) {
            Log.e(moduleName(), "process frame err: ", e)
        }
    }
}