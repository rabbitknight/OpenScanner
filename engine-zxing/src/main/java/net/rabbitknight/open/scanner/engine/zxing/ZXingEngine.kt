package net.rabbitknight.open.scanner.engine.zxing

import android.content.Context
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.Result
import com.google.zxing.common.HybridBinarizer
import net.rabbitknight.open.scanner.core.C
import net.rabbitknight.open.scanner.core.ScannerException
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.format.BarcodeFormat
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.AZTEC
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.CODABAR
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.CODE_128
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.CODE_39
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.CODE_93
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.DATA_MATRIX
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.EAN_13
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.EAN_8
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.ITF
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.MAXICODE
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.PDF_417
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.QR_CODE
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.RSS_14
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.RSS_EXPANDED
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.UPC_A
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.UPC_E
import net.rabbitknight.open.scanner.core.format.BarcodeFormat.UPC_EAN_EXTENSION
import net.rabbitknight.open.scanner.core.image.ImageFormat
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.result.BarcodeResult
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.rabbitknight.open.scanner.core.result.Rect
import java.lang.Math.min

class ZXingEngine() : Engine {
    private var buffer = ByteArray(1024 * 1024 * 1)
    private val zxingCore = MultiFormatReader().also {
        val hints = Pair(
            DecodeHintType.POSSIBLE_FORMATS,
            listOf(com.google.zxing.BarcodeFormat.QR_CODE)
        )
        it.setHints(mapOf(hints))
    }

    override fun init(context: Context) {

    }

    override fun release() {

    }

    override fun supportBarFormat(format: BarcodeFormat): Boolean {
        return map(format) != null
    }

    override fun setBarFormat(format: BarcodeFormat, vararg formats: BarcodeFormat) {
        val hintTypes = (listOf(format) + formats).mapNotNull { map(it) }
        val hints = mapOf(Pair(DecodeHintType.POSSIBLE_FORMATS, hintTypes))
        zxingCore.setHints(hints)
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
        val bitmap = buffer.let {
            val source = PlanarYUVLuminanceSource(it, width, height, 0, 0, width, height, false)
            BinaryBitmap(HybridBinarizer(source))
        }
        val result: ImageResult = try {
            zxingCore.decode(bitmap).toImageResult(timestamp)
        } catch (e: Exception) {
            ImageResult(C.CODE_FAIL, timestamp, emptyList())
        }
        return result
    }

    override fun preferImageFormat(): String = ImageFormat.YV12

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

    private fun map(format: com.google.zxing.BarcodeFormat): BarcodeFormat? {
        return when (format) {
            com.google.zxing.BarcodeFormat.AZTEC -> AZTEC
            com.google.zxing.BarcodeFormat.CODABAR -> CODABAR
            com.google.zxing.BarcodeFormat.CODE_39 -> CODE_39
            com.google.zxing.BarcodeFormat.CODE_93 -> CODE_93
            com.google.zxing.BarcodeFormat.CODE_128 -> CODE_128
            com.google.zxing.BarcodeFormat.DATA_MATRIX -> DATA_MATRIX
            com.google.zxing.BarcodeFormat.EAN_8 -> EAN_8
            com.google.zxing.BarcodeFormat.EAN_13 -> EAN_13
            com.google.zxing.BarcodeFormat.ITF -> ITF
            com.google.zxing.BarcodeFormat.MAXICODE -> MAXICODE
            com.google.zxing.BarcodeFormat.PDF_417 -> PDF_417
            com.google.zxing.BarcodeFormat.QR_CODE -> QR_CODE
            com.google.zxing.BarcodeFormat.RSS_14 -> RSS_14
            com.google.zxing.BarcodeFormat.RSS_EXPANDED -> RSS_EXPANDED
            com.google.zxing.BarcodeFormat.UPC_A -> UPC_A
            com.google.zxing.BarcodeFormat.UPC_E -> UPC_E
            com.google.zxing.BarcodeFormat.UPC_EAN_EXTENSION -> UPC_EAN_EXTENSION
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
            buffer.position(min(size, buffer.position() - width + rowStride))
        }
    }

    private fun Result.toImageResult(timestamp: Long): ImageResult {
        val format = map(this.barcodeFormat)!!
        val barcodeResult = BarcodeResult(
            format, Rect(0, 0, 0, 0), this.text, this.rawBytes
        )
        return ImageResult(C.CODE_SUCCESS, timestamp, listOf(barcodeResult))
    }
}