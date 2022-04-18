package net.rabbitknight.open.scanner.core.result

import net.rabbitknight.open.scanner.core.format.BarcodeFormat

data class BarcodeResult(
    val format: BarcodeFormat,
    val rect: Rect,
    val payload: String,
    val rawBytes: ByteArray
)

data class Rect(val left: Int, val top: Int, val right: Int, val bottom: Int)

data class RectF(val left: Float, val top: Float, val right: Float, val bottom: Float)


fun Rect.centerX() = (this.left + this.right) / 2

fun Rect.centerY() = (this.top + this.bottom) / 2

fun RectF.centerX() = (this.left + this.right) / 2

fun RectF.centerY() = (this.top + this.bottom) / 2