package net.rabbitknight.open.scanner.core.result

import net.rabbitknight.open.scanner.core.format.BarFormat

data class Result(
    val format: BarFormat,
    val rect: Rect,
    val payload: String
)

data class Rect(val left: Int, val top: Int, val right: Int, val bottom: Int)
