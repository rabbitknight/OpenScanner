package net.rabbitknight.open.scanner.core.lifecycle

import net.rabbitknight.open.scanner.core.config.Config

/**
 * 基础模块
 */
interface IModule {
    /**
     * 配置
     */
    fun onConfig(config: Config)

    /**
     * 模块启动回调
     */
    fun onStart()

    /**
     * 模块停止回调
     */
    fun onStop()

    /**
     * 模块运行回调
     */
    fun onStep()
}