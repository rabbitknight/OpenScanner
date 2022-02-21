package net.rabbitknight.open.scanner.core.lifecycle

import net.rabbitknight.open.scanner.core.config.ScannerConfig

interface IModule {
    fun onConfig(config: ScannerConfig)

    fun onStart()

    fun onStop()

    fun onStep()
}