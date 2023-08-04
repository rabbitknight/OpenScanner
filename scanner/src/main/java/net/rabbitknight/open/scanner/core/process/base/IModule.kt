package net.rabbitknight.open.scanner.core.process.base

import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.config.InitOption
import net.rabbitknight.open.scanner.core.process.ImageFrame

/**
 * 基础模块
 */
interface IModule {

    /**
     * 模块创建
     */
    fun onCreate(option: InitOption)

    /**
     * 模块销毁
     */
    fun onDestroy()

    /**
     * 配置修改
     */
    fun onConfigChanged(config: Config)

    /**
     * 处理数据
     */
    fun onProcess(frame: ImageFrame)
}