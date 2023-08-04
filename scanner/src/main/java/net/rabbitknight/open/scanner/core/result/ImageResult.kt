package net.rabbitknight.open.scanner.core.result

data class ImageResult(
    val code: Int,
    val timestamp: Long,
    val result: List<BarcodeResult>,
    val engine: String
)