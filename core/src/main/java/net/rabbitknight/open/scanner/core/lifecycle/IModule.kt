package net.rabbitknight.open.scanner.core.lifecycle

import net.rabbitknight.open.scanner.core.config.Config

interface IModule {
    fun onConfig(config: Config)

    fun onStart()

    fun onStop()

    fun onStep()
}