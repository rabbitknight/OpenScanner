package net.rabbitknight.open.scanner.engine.zxing

import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.format.BarcodeFormat
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.*
import net.rabbitknight.open.scanner.core.image.ImageProxy
import net.rabbitknight.open.scanner.core.result.ImageResult

class ZXingEngine : Engine {
    private val zxingCore = MultiFormatReader().also {
        this.setFormat(QR_CODE)
    }

    override fun supportFormat(format: BarcodeFormat): Boolean {
        return map(format) != null
    }

    override fun setFormat(vararg format: BarcodeFormat) {
        val formats = format.mapNotNull { map(it) }
        val hints = mapOf(Pair(DecodeHintType.POSSIBLE_FORMATS, formats))
        zxingCore.setHints(hints)
    }

    override fun decode(image: ImageProxy): ImageResult {
        TODO("map")
    }

    private fun map(format: BarcodeFormat): com.google.zxing.BarcodeFormat? {
        return when (format) {
            AZTEC -> com.google.zxing.BarcodeFormat.AZTEC
            CODABAR -> com.google.zxing.BarcodeFormat.CODABAR
            CODE_39 -> com.google.zxing.BarcodeFormat.CODE_39
            CODE_93 -> com.google.zxing.BarcodeFormat.CODE_93
            CODE_128 -> com.google.zxing.BarcodeFormat.CODE_128
            DATA_MATRIX -> com.google.zxing.BarcodeFormat.DATA_MATRIX
            EAN_8 -> com.google.zxing.BarcodeFormat.EAN_8
            EAN_13 -> com.google.zxing.BarcodeFormat.EAN_13
            ITF -> com.google.zxing.BarcodeFormat.ITF
            MAXICODE -> com.google.zxing.BarcodeFormat.MAXICODE
            PDF_417 -> com.google.zxing.BarcodeFormat.PDF_417
            QR_CODE -> com.google.zxing.BarcodeFormat.QR_CODE
            RSS_14 -> com.google.zxing.BarcodeFormat.RSS_14
            RSS_EXPANDED -> com.google.zxing.BarcodeFormat.RSS_EXPANDED
            UPC_A -> com.google.zxing.BarcodeFormat.UPC_A
            UPC_E -> com.google.zxing.BarcodeFormat.UPC_E
            UPC_EAN_EXTENSION -> com.google.zxing.BarcodeFormat.UPC_EAN_EXTENSION
            else -> null
        }
    }
}