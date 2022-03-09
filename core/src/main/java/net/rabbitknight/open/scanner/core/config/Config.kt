package net.rabbitknight.open.scanner.core.config

import net.rabbitknight.open.scanner.core.C.DEFAULT_INPUT_CAPACITY
import net.rabbitknight.open.scanner.core.result.RectF

data class Config(
    /**
     * 取景器位置
     */
    val finderRect: RectF,
    /**
     * 取景器扩大倍数
     */
    val finderTolerance: Float,

    /**
     * 是否开启晃动检测
     */
    val enableShakeFilter: Boolean,

    /**
     * 输入缓存大小
     */
    val inputCapacity: Int = DEFAULT_INPUT_CAPACITY
)