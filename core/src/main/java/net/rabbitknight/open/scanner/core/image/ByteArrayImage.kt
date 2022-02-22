package net.rabbitknight.open.scanner.core.image

import android.graphics.ImageFormat
import net.rabbitknight.open.scanner.core.ScannerException
import java.nio.ByteBuffer

class ByteArrayImage(
    private val byteArray: ByteArray,
    override val format: Int,
    override val width: Int,
    override val height: Int,
    override val timestamp: Long,
) : ImageWrapper {

    private var planeWrappers: Array<ImageWrapper.PlaneWrapper>

    init {
        if (format != ImageFormat.YV12 || format != ImageFormat.NV21) {
            throw ScannerException("not support format${format}")
        }
        val yLen = width * height
        val uLen = width / 2 * height / 2
        val vLen = width / 2 * height / 2
        val yuvBuffer = ByteBuffer.wrap(byteArray)
        val planeY = ByteArrayPlane(width, 0, let {
            yuvBuffer.position(0)
            yuvBuffer.slice().apply { limit(yLen) }
        })

        val planeV = if (format == ImageFormat.YV12) {
            ByteArrayPlane(width / 2, 0, let {
                yuvBuffer.position(yLen)
                yuvBuffer.slice().apply { limit(vLen) }
            })
        } else {
            ByteArrayPlane(width, 1, let {
                yuvBuffer.position(yLen)
                yuvBuffer.slice().apply { limit(uLen + vLen) }
            })
        }
        val planeU = if (format == ImageFormat.YV12) {
            ByteArrayPlane(width / 2, 0, let {
                yuvBuffer.position(yLen + vLen)
                yuvBuffer.slice().apply { limit(uLen) }
            })
        } else {
            ByteArrayPlane(width, 1, let {
                yuvBuffer.position(yLen + 1)
                yuvBuffer.slice().apply { limit(uLen + vLen - 1) }
            })
        }
        planeWrappers = arrayOf(planeY, planeU, planeV)
    }

    override fun close() {
        // no-op
    }

    override val planes: Array<ImageWrapper.PlaneWrapper>
        get() = planeWrappers

    override val payload: Any
        get() = byteArray

    internal class ByteArrayPlane(
        override val rowStride: Int,
        override val pixelStride: Int,
        override val buffer: ByteBuffer
    ) : ImageWrapper.PlaneWrapper
}