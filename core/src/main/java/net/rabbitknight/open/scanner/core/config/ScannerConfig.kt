package net.rabbitknight.open.scanner.core.config

import net.rabbitknight.open.scanner.core.result.Rect

data class ScannerConfig(
    /**
     * 取景器位置
     */
    val finderRect: Rect,
    /**
     * 取景器扩大倍数
     */
    val finderTolerance: Float
)