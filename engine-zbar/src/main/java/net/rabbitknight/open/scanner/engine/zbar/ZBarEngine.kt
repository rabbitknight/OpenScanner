package net.rabbitknight.open.scanner.engine.zbar

import android.content.Context
import net.rabbitknight.open.scanner.core.C.CODE_FAIL
import net.rabbitknight.open.scanner.core.C.CODE_SUCCESS
import net.rabbitknight.open.scanner.core.ScannerException
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.format.BarcodeFormat
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.*
import net.rabbitknight.open.scanner.core.image.ImageFormat
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.result.BarcodeResult
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.rabbitknight.open.scanner.core.result.Rect
import net.rabbitknight.open.scanner.engine.zbar.C.ZBAR_FORMAT_Y800
import net.sourceforge.zbar.Config
import net.sourceforge.zbar.Image
import net.sourceforge.zbar.ImageScanner
import net.sourceforge.zbar.Symbol

class ZBarEngine() : Engine {
    private val zbarScanner = ImageScanner().also {
        it.setConfig(Symbol.QRCODE, Config.ENABLE, 1)
    }
    private var buffer = ByteArray(1024 * 1024 * 1)
    private var zbarImage = Image(ZBAR_FORMAT_Y800)

    override fun init(context: Context) {

    }

    override fun release() {

    }

    override fun supportBarFormat(format: BarcodeFormat): Boolean {
        return map(format) != null
    }

    override fun setBarFormat(format: BarcodeFormat, vararg formats: BarcodeFormat) {
        zbarScanner.setConfig(C.ZBAR_ALL, Config.ENABLE, 0)
        val scanTypes = (listOf(format) + formats).mapNotNull { map(it) }
        scanTypes.forEach {
            zbarScanner.setConfig(it, Config.ENABLE, 1)
        }
    }

    override fun decode(image: ImageWrapper<ByteArray>): ImageResult {
        val supports = listOf(
            ImageFormat.Y800,
            ImageFormat.YV12,
            ImageFormat.NV21,
            ImageFormat.YUV_420_888
        )
        if (supports.all { image.format != it }) {
            throw ScannerException("not support format ${image.format}")
        }
        val width = image.width
        val height = image.height
        val timestamp = image.timestamp
        // resize buffer
        buffer.let {
            val wanted = image.width * image.height
            if (wanted > buffer.size) buffer = ByteArray(wanted)
        }
        // to bytebuffer
        buffer.let {
            image.planes[0].toByteArray(it, width, height)
        }
        zbarImage.setSize(width, height)
        zbarImage.setData(buffer)
        val code = zbarScanner.scanImage(zbarImage)
        if (code != 0) {
            val rawResults = zbarScanner.results
            val barcodeResults = rawResults.mapNotNull {
                val format = map(it.type)
                val text = it.data
                val rawBytes = it.dataBytes
                if (format != null) {
                    BarcodeResult(format, Rect(0, 0, 0, 0), text, rawBytes)
                } else {
                    null
                }
            }
            return ImageResult(CODE_SUCCESS, timestamp, barcodeResults)
        } else {
            return ImageResult(CODE_FAIL, timestamp, emptyList())
        }
    }

    override fun preferImageFormat(): String = ImageFormat.YV12

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

    private fun ImageWrapper.PlaneWrapper.toByteArray(out: ByteArray, width: Int, height: Int) {
        buffer.rewind()
        val size = buffer.remaining()
        var position = 0
        for (row in 0 until height) {
            buffer.get(out, position, width)
            position += width
            buffer.position(Math.min(size, buffer.position() - width + rowStride))
        }
    }
}