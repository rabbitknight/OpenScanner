package net.rabbitknight.open.scanner.engine.hwscankit

import android.content.Context
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.huawei.hms.ml.scan.HmsScanBase.*
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.format.BarcodeFormat
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.result.ImageResult

class HWScanKitEngine : Engine {
    override fun init(context: Context) {
        // no-op
    }

    override fun release() {
        // no-op
    }

    override fun supportBarFormat(format: BarcodeFormat): Boolean {
        return map(format) != null
    }

    override fun setBarFormat(vararg format: BarcodeFormat) {
        val formats = format.mapNotNull { map(it) }

//        HmsScanAnalyzerOptions.Creator().setHmsScanTypes(scanType).setPhotoMode(true).create()
    }

    override fun decode(image: ImageWrapper<Any>): ImageResult {
        TODO("Not yet implemented")
    }

    override fun preferImageFormat(): String {
        TODO("Not yet implemented")
    }

    private fun map(format: BarcodeFormat): Int? {
        return when (format) {
            BarcodeFormat.AZTEC -> AZTEC_SCAN_TYPE
            BarcodeFormat.CODABAR -> CODABAR_SCAN_TYPE
            BarcodeFormat.CODE_39 -> CODE39_SCAN_TYPE
            BarcodeFormat.CODE_93 -> CODE93_SCAN_TYPE
            BarcodeFormat.CODE_128 -> CODE128_SCAN_TYPE
            BarcodeFormat.DATA_MATRIX -> DATAMATRIX_SCAN_TYPE
            BarcodeFormat.EAN_8 -> EAN8_SCAN_TYPE
            BarcodeFormat.EAN_13 -> EAN13_SCAN_TYPE
            BarcodeFormat.ITF -> ITF14_SCAN_TYPE
            BarcodeFormat.MAXICODE -> null
            BarcodeFormat.PDF_417 -> PDF417_SCAN_TYPE
            BarcodeFormat.QR_CODE -> QRCODE_SCAN_TYPE
            BarcodeFormat.RSS_14 -> null
            BarcodeFormat.RSS_EXPANDED -> null
            BarcodeFormat.UPC_A -> UPCCODE_A_SCAN_TYPE
            BarcodeFormat.UPC_E -> UPCCODE_E_SCAN_TYPE
            BarcodeFormat.UPC_EAN_EXTENSION -> null
        }
    }

    private fun map(format: Int): BarcodeFormat? {
        return when (format) {
            CODABAR_SCAN_TYPE -> BarcodeFormat.CODABAR
            CODE39_SCAN_TYPE -> BarcodeFormat.CODE_39
            CODE93_SCAN_TYPE -> BarcodeFormat.CODE_93
            CODE128_SCAN_TYPE -> BarcodeFormat.CODE_128
            EAN8_SCAN_TYPE -> BarcodeFormat.EAN_8
            EAN13_SCAN_TYPE -> BarcodeFormat.EAN_13
            ITF14_SCAN_TYPE -> BarcodeFormat.ITF
            PDF417_SCAN_TYPE -> BarcodeFormat.PDF_417
            QRCODE_SCAN_TYPE -> BarcodeFormat.QR_CODE
            AZTEC_SCAN_TYPE -> BarcodeFormat.AZTEC
            DATAMATRIX_SCAN_TYPE -> BarcodeFormat.DATA_MATRIX
            UPCCODE_A_SCAN_TYPE -> BarcodeFormat.UPC_A
            UPCCODE_E_SCAN_TYPE -> BarcodeFormat.UPC_E
            else -> null
        }
    }
}