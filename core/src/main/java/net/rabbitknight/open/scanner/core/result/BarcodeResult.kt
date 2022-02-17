package net.rabbitknight.open.scanner.core.result

import net.rabbitknight.open.scanner.core.format.BarcodeFormat

data class BarcodeResult(
    val format: BarcodeFormat,
    val rect: Rect,
    val payload: String,
    val rawBytes: ByteArray
)

data class Rect(val left: Int, val top: Int, val right: Int, val bottom: Int)
