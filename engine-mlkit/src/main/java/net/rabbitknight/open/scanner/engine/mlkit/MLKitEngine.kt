package net.rabbitknight.open.scanner.engine.mlkit

import android.content.Context
import android.util.Log
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import net.rabbitknight.open.scanner.core.C
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.format.BarcodeFormat
import net.rabbitknight.open.scanner.core.image.ImageFormat
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.result.BarcodeResult
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.rabbitknight.open.scanner.core.result.Rect
import java.util.concurrent.Executor
import kotlin.system.measureTimeMillis

class MLKitEngine : Engine {
    private val executor by lazy { DirectExecutor() }
    private val optionBuilder by lazy {
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .setExecutor(executor)
    }
    private lateinit var scanner: BarcodeScanner

    override fun init(context: Context) {
        // 构造设置
        val option = optionBuilder.build()
        // option
        scanner = BarcodeScanning.getClient(option)

    }

    override fun release() {
        // no-op
    }

    override fun supportBarFormat(format: BarcodeFormat): Boolean =
        map(format) != Barcode.FORMAT_UNKNOWN

    override fun setBarFormat(format: BarcodeFormat, vararg formats: BarcodeFormat) {
        val main = map(format)
        val more = formats.map { map(it) }.filter { it != Barcode.FORMAT_UNKNOWN }.toIntArray()
        optionBuilder.setBarcodeFormats(main, *more)
        val option = optionBuilder.build()
        scanner = BarcodeScanning.getClient(option)
    }

    override fun decode(image: ImageWrapper<ByteArray>): ImageResult {
        val timestamp = image.timestamp
        // 图像转换
        val inputImage: InputImage
        measureTimeMillis {
            inputImage = image.toInputImage()
        }.let {
            Log.i(C.TAG, "${name()} decode: image prepare cost ${it}ms")
        }

        // 图像识别
        val barcodes: List<Barcode>
        measureTimeMillis {
            // 任务提交
            val tasks = scanner.process(inputImage)
            // 结果获取 (使用DirectExecutor)
            barcodes = tasks.result
        }.let {
            Log.i(C.TAG, "${name()} decode: image decode cost ${it}ms")
        }

        return barcodes.toImageResult(timestamp)
    }

    override fun preferImageFormat(): String = ImageFormat.YV12
    override fun name(): String = "MLKit"


    private fun ImageWrapper<ByteArray>.toInputImage(): InputImage {
        val payload = this.payload
        val format = InputImage.IMAGE_FORMAT_YV12
        return InputImage.fromByteArray(payload, this.width, this.height, 0, format)
    }

    private fun List<Barcode>.toImageResult(timestamp: Long): ImageResult {
        if (this.isEmpty()) return ImageResult(C.CODE_FAIL, timestamp, emptyList(), name())
        val barcodes = this.map {
            val format = map(it.format)!!
            val box = it.boundingBox!!.let { Rect(it.left, it.top, it.right, it.bottom) }
            val raw = it.rawBytes!!
            val payload = it.rawValue!!
            val corners = it.cornerPoints!!
            BarcodeResult(format, box, payload, raw)
        }
        return ImageResult(C.CODE_SUCCESS, timestamp, barcodes, name())
    }


    private fun map(format: BarcodeFormat): Int {
        return when (format) {
            BarcodeFormat.AZTEC -> Barcode.FORMAT_AZTEC
            BarcodeFormat.CODABAR -> Barcode.FORMAT_CODABAR
            BarcodeFormat.CODE_39 -> Barcode.FORMAT_CODE_39
            BarcodeFormat.CODE_93 -> Barcode.FORMAT_CODE_93
            BarcodeFormat.CODE_128 -> Barcode.FORMAT_CODE_128
            BarcodeFormat.DATA_MATRIX -> Barcode.FORMAT_DATA_MATRIX
            BarcodeFormat.EAN_8 -> Barcode.FORMAT_EAN_8
            BarcodeFormat.EAN_13 -> Barcode.FORMAT_EAN_13
            BarcodeFormat.ITF -> Barcode.FORMAT_ITF
            BarcodeFormat.MAXICODE -> Barcode.FORMAT_UNKNOWN
            BarcodeFormat.PDF_417 -> Barcode.FORMAT_PDF417
            BarcodeFormat.QR_CODE -> Barcode.FORMAT_QR_CODE
            BarcodeFormat.RSS_14 -> Barcode.FORMAT_UNKNOWN
            BarcodeFormat.RSS_EXPANDED -> Barcode.FORMAT_UNKNOWN
            BarcodeFormat.UPC_A -> Barcode.FORMAT_UPC_A
            BarcodeFormat.UPC_E -> Barcode.FORMAT_UPC_E
            BarcodeFormat.UPC_EAN_EXTENSION -> Barcode.FORMAT_UNKNOWN
            else -> Barcode.FORMAT_UNKNOWN
        }
    }

    private fun map(format: Int): BarcodeFormat? {
        return when (format) {
            Barcode.FORMAT_AZTEC -> BarcodeFormat.AZTEC
            Barcode.FORMAT_CODABAR -> BarcodeFormat.CODABAR
            Barcode.FORMAT_CODE_39 -> BarcodeFormat.CODE_39
            Barcode.FORMAT_CODE_93 -> BarcodeFormat.CODE_93
            Barcode.FORMAT_CODE_128 -> BarcodeFormat.CODE_128
            Barcode.FORMAT_DATA_MATRIX -> BarcodeFormat.DATA_MATRIX
            Barcode.FORMAT_EAN_8 -> BarcodeFormat.EAN_8
            Barcode.FORMAT_EAN_13 -> BarcodeFormat.EAN_13
            Barcode.FORMAT_ITF -> BarcodeFormat.ITF
            null -> BarcodeFormat.MAXICODE
            Barcode.FORMAT_PDF417 -> BarcodeFormat.PDF_417
            Barcode.FORMAT_QR_CODE -> BarcodeFormat.QR_CODE
            null -> BarcodeFormat.RSS_14
            null -> BarcodeFormat.RSS_EXPANDED
            Barcode.FORMAT_UPC_A -> BarcodeFormat.UPC_A
            Barcode.FORMAT_UPC_E -> BarcodeFormat.UPC_E
            null -> BarcodeFormat.UPC_EAN_EXTENSION
            else -> null
        }
    }

    // direct run
    internal class DirectExecutor : Executor {
        override fun execute(r: Runnable) {
            r.run()
        }
    }
}