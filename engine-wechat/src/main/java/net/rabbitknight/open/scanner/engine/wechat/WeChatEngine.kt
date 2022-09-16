package net.rabbitknight.open.scanner.engine.wechat

import android.content.Context
import net.rabbitknight.open.scanner.core.C
import net.rabbitknight.open.scanner.core.ScannerException
import net.rabbitknight.open.scanner.core.engine.Engine
import net.rabbitknight.open.scanner.core.format.BarcodeFormat
import net.rabbitknight.open.scanner.core.image.ImageFormat
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.result.BarcodeResult
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.rabbitknight.open.scanner.core.result.Rect

/**
 * 从OpenCV WeChatQrcode移植
 * 只支持qrcode
 */
class WeChatEngine : Engine {
    private var enable = true
    private var buffer = ByteArray(1024 * 1024 * 1)


    // 必须在load之后调用
    private lateinit var qrcodeEngine: WeChatQRCode

    override fun init(context: Context) {
        AssetsLoader.load(context, "wechat_qrcode")
        qrcodeEngine = WeChatQRCode()
    }

    override fun release() {
        qrcodeEngine.release()
    }

    override fun supportBarFormat(format: BarcodeFormat): Boolean =
        format == BarcodeFormat.QR_CODE

    override fun setBarFormat(format: BarcodeFormat, vararg formats: BarcodeFormat) {
        enable = (format == BarcodeFormat.QR_CODE) || formats.contains(BarcodeFormat.QR_CODE)
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
        val outRects = mutableListOf<Rect>()
        val outTexts = mutableListOf<String>()
        qrcodeEngine.decode(buffer, width, height, emptyList(), outRects, outTexts)
        if (outTexts.isEmpty()) {
            return ImageResult(C.CODE_FAIL, timestamp, emptyList(), name())
        } else {
            val barcodeResults = mutableListOf<BarcodeResult>()
            for (i in 0 until outTexts.size) {
                barcodeResults.add(
                    BarcodeResult(
                        BarcodeFormat.QR_CODE,
                        outRects[i],
                        outTexts[i],
                        outTexts[i].toByteArray()
                    )
                )
            }
            return ImageResult(C.CODE_SUCCESS, timestamp, barcodeResults, name())
        }
    }

    override fun preferImageFormat(): String = ImageFormat.YV12

    override fun name(): String = "WeChatQrcode"

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