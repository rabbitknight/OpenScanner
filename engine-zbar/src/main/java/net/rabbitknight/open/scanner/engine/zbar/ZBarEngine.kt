package net.rabbitknight.open.scanner.engine.zbar

import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.format.BarcodeFormat
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.*
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.sourceforge.zbar.ImageScanner
import net.sourceforge.zbar.Symbol

class ZBarEngine : Engine {
    private val imageScanner = ImageScanner()
    override fun supportFormat(format: BarcodeFormat): Boolean {
        return map(format) != null
    }

    override fun setFormat(vararg format: BarcodeFormat) {
    }

    override fun decode(image: ImageWrapper): ImageResult {
    }

    private fun map(format: BarcodeFormat): Int? {
        return when (format) {
            AZTEC -> null
            CODABAR -> Symbol.CODABAR
            CODE_39 -> Symbol.CODE39
            CODE_93 -> Symbol.CODE93
            CODE_128 -> Symbol.CODE128
            DATA_MATRIX -> null
            EAN_8 -> Symbol.EAN8
            EAN_13 -> Symbol.EAN13
            ITF -> Symbol.I25
            MAXICODE -> null
            PDF_417 -> Symbol.PDF417
            QR_CODE -> Symbol.QRCODE
            RSS_14 -> Symbol.DATABAR
            RSS_EXPANDED -> Symbol.DATABAR_EXP
            UPC_A -> Symbol.UPCA
            UPC_E -> Symbol.UPCE
            UPC_EAN_EXTENSION -> null
        }
    }

    private fun map(format: Int): BarcodeFormat? {
        return when (format) {
            Symbol.CODABAR -> CODABAR
            Symbol.CODE39 -> CODE_39
            Symbol.CODE93 -> CODE_93
            Symbol.CODE128 -> CODE_128
            Symbol.EAN8 -> EAN_8
            Symbol.EAN13 -> EAN_13
            Symbol.I25 -> ITF
            Symbol.PDF417 -> PDF_417
            Symbol.QRCODE -> QR_CODE
            Symbol.DATABAR -> RSS_14
            Symbol.DATABAR_EXP -> RSS_EXPANDED
            Symbol.UPCA -> UPC_A
            Symbol.UPCE -> UPC_E
            else -> null
        }
    }
}