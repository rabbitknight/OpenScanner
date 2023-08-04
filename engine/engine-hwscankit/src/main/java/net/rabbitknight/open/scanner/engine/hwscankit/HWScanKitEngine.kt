package net.rabbitknight.open.scanner.engine.hwscankit

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.huawei.hms.ml.scan.HmsScanBase.ALL_SCAN_TYPE
import com.huawei.hms.ml.scan.HmsScanBase.AZTEC_SCAN_TYPE
import com.huawei.hms.ml.scan.HmsScanBase.CODABAR_SCAN_TYPE
import com.huawei.hms.ml.scan.HmsScanBase.CODE128_SCAN_TYPE
import com.huawei.hms.ml.scan.HmsScanBase.CODE39_SCAN_TYPE
import com.huawei.hms.ml.scan.HmsScanBase.CODE93_SCAN_TYPE
import com.huawei.hms.ml.scan.HmsScanBase.DATAMATRIX_SCAN_TYPE
import com.huawei.hms.ml.scan.HmsScanBase.EAN13_SCAN_TYPE
import com.huawei.hms.ml.scan.HmsScanBase.EAN8_SCAN_TYPE
import com.huawei.hms.ml.scan.HmsScanBase.ITF14_SCAN_TYPE
import com.huawei.hms.ml.scan.HmsScanBase.PDF417_SCAN_TYPE
import com.huawei.hms.ml.scan.HmsScanBase.QRCODE_SCAN_TYPE
import com.huawei.hms.ml.scan.HmsScanBase.UPCCODE_A_SCAN_TYPE
import com.huawei.hms.ml.scan.HmsScanBase.UPCCODE_E_SCAN_TYPE
import net.rabbitknight.open.scanner.core.C
import net.rabbitknight.open.scanner.core.C.TAG
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.format.BarcodeFormat
import net.rabbitknight.open.scanner.core.image.ImageFormat
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.result.BarcodeResult
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.rabbitknight.open.scanner.core.result.Rect
import java.lang.ref.WeakReference
import java.nio.ByteBuffer
import kotlin.system.measureTimeMillis

class HWScanKitEngine : Engine {
    private lateinit var context: WeakReference<Context>

    private var option: HmsScanAnalyzerOptions =
        HmsScanAnalyzerOptions.Creator().setHmsScanTypes(ALL_SCAN_TYPE)
            .setPhotoMode(true).create()

    private var bitmap = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888)
    private var buffer = ByteBuffer.allocate(1280 * 720 * 4)

    override fun init(context: Context) {
        this.context = WeakReference(context)
    }

    override fun release() {
        // no-op
    }

    override fun supportBarFormat(format: BarcodeFormat): Boolean {
        return map(format) != null
    }

    override fun setBarFormat(format: BarcodeFormat, vararg formats: BarcodeFormat) {
        val types = (listOf(format) + formats).mapNotNull { map(it) }
        val scanType = types.firstOrNull() ?: return
        val scanTypes = types.let {
            if (types.size < 2) intArrayOf()
            else types.subList(1, types.size - 1).toIntArray()
        }
        option = HmsScanAnalyzerOptions.Creator().setHmsScanTypes(scanType, *scanTypes)
            .setPhotoMode(true).create()
    }

    override fun decode(image: ImageWrapper<ByteArray>): ImageResult {
        val context =
            context.get() ?: return ImageResult(C.CODE_FAIL, image.timestamp, emptyList(), name())
        if (image.format != ImageFormat.ARGB) {
            return ImageResult(C.CODE_FAIL, image.timestamp, emptyList(), name())
        }

        // image prepare
        measureTimeMillis {
            // 缓存拷贝
            if (buffer.capacity() < image.payload.size) {
                buffer = ByteBuffer.allocate(image.payload.size)
            }
            buffer.let {
                it.position(0)
                it.put(image.payload)
                it.position(0)
            }
            // bitmap 构造
            if (bitmap.allocationByteCount < image.width * image.height * 4) {
                bitmap.recycle()
                bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
            }
            bitmap.width = image.width
            bitmap.height = image.height
            bitmap.copyPixelsFromBuffer(buffer)
        }.let {
            Log.i(TAG, "[${name()}] decode: image prepare cost ${it}ms")
        }

        // image decode
        val hmsScans: Array<HmsScan>?
        measureTimeMillis {
            hmsScans = ScanUtil.decodeWithBitmap(context, bitmap, option)
        }.let {
            Log.i(TAG, "[${name()}] decode: image decode cost ${it}ms")
        }
        // 结果转换
        val results = hmsScans?.map {
            val rect = it.borderRect.let { border ->
                Rect(border.left, border.top, border.right, border.bottom)
            }
            val rawData = it.originValueByte
            val result = it.originalValue
            val format = map(it.scanType)!!
            BarcodeResult(format, rect, result, rawData)
        } ?: emptyList()
        val code = if (results.isEmpty()) C.CODE_FAIL else C.CODE_SUCCESS

        // 包装&输出
        return ImageResult(code, image.timestamp, results, name())
    }

    override fun preferImageFormat(): String = ImageFormat.ARGB

    override fun name(): String = "HWScan"

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