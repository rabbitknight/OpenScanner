package net.rabbitknight.open.scanner.core.thread

import net.rabbitknight.open.scanner.core.config.ScannerConfig
import net.rabbitknight.open.scanner.core.lifecycle.IModule
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class CoreThread(private val command: Runnable) : IModule, Runnable {
    private val threadPool = Executors.newScheduledThreadPool(1)
    private lateinit var config: ScannerConfig
    private lateinit var future: ScheduledFuture<out Any>
    private var period = 1000L / 24L
    override fun onConfig(config: ScannerConfig) {
        this.config = config
        period = 1000L / config.decodeFps
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