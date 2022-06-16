package net.rabbitknight.open.scanner.core.config

import net.rabbitknight.open.scanner.core.C

data class InitOption(
    /**
     * 输入缓存大小
     */
    val inputCapacity: Int = C.DEFAULT_INPUT_CAPACITY,
)