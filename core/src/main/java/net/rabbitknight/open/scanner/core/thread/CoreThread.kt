package net.rabbitknight.open.scanner.core.thread

import net.rabbitknight.open.scanner.core.C.SCHEDULE_PERIOD_MILS
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.lifecycle.IModule
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * 线程控制
 */
class CoreThread(private val command: Runnable) : IModule, Runnable {
    private val threadPool = Executors.newScheduledThreadPool(1)
    private lateinit var config: Config
    private lateinit var future: ScheduledFuture<out Any>
    private val period = 1000L / SCHEDULE_PERIOD_MILS
    override fun onConfig(config: Config) {
        this.config = config
    }

    override fun onStart() {
        if (!future.isCancelled) {
            future.cancel(true)
        }
        this.future = threadPool.scheduleAtFixedRate(
            this,
            0L,
            period,
            TimeUnit.MILLISECONDS
        )
    }

    override fun onStop() {
        if (!future.isCancelled) {
            future.cancel(true)
        }
    }

    override fun onStep() {
        // no-op
    }

    override fun run() {
        command.run()
    }
}