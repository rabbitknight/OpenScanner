package net.rabbitknight.open.scanner.core.result

import net.rabbitknight.open.scanner.core.format.BarcodeFormat

data class ImageResult(
    val code: Int,
    val timestamp: Long,
    val result: Map<BarcodeFormat, List<BarcodeResult>>
)