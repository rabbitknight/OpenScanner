package net.rabbitknight.open.scanner.core.image

import net.rabbitknight.open.scanner.core.OpenScanner
import net.rabbitknight.open.scanner.core.ScannerException
import java.nio.ByteBuffer

/**
 * 对ByteArray容纳的YUV数据进行装载
 * 这些数据的平面尺寸是固定的，并不需要像Android_420_888一样拆分
 */
class ByteArrayImage(
    override val owner: WrapperOwner<ByteArray>,
    private val byteArray: ByteArray,
    override val format: String,
    override val width: Int,
    override val height: Int,
    override val timestamp: Long,
) : ImageWrapper<ByteArray> {
    private var internalBuffer: ByteBuffer
    private var planeWrappers: Array<ImageWrapper.PlaneWrapper>

    init {
        val supportsYUV =
            arrayOf(ImageFormat.YV12, ImageFormat.NV21, ImageFormat.I420, ImageFormat.Y800)
        val supportsRGB =
            arrayOf(ImageFormat.RGBA, ImageFormat.ARGB, ImageFormat.BGRA, ImageFormat.RGBP)
        if (!supportsYUV.any { it == format } && !supportsRGB.any { it == format }) {
            throw ScannerException("not support format${format}")
        }

        internalBuffer = OpenScanner.sharedBufferPool.acquire(width, height, format)

        planeWrappers = when (format) {
            ImageFormat.Y800 -> initForY800(byteArray, width, height, internalBuffer)
            ImageFormat.I420 -> initForI420(byteArray, width, height, internalBuffer)
            ImageFormat.YV12 -> initForYV12(byteArray, width, height, internalBuffer)
            ImageFormat.NV21 -> initForNV21(byteArray, width, height, internalBuffer)
            else -> initForRGB(byteArray, width, height, internalBuffer, format)
        }
    }

    private fun initForYV12(
        byteArray: ByteArray,
        width: Int,
        height: Int,
        buffer: ByteBuffer
    ): Array<ImageWrapper.PlaneWrapper> {
        val yLen = width * height
        val uLen = width / 2 * height / 2
        val vLen = width / 2 * height / 2

        buffer.position(0)
        buffer.put(byteArray, 0, yLen + uLen + vLen)

        val planeY = ByteArrayPlane(width, 0, let {
            buffer.position(0)
            buffer.slice().apply { limit(yLen) }
        })
        val planeV = ByteArrayPlane(width / 2, 0, let {
            buffer.position(yLen)
            buffer.slice().apply { limit(vLen) }
        })
        val planeU = ByteArrayPlane(width / 2, 0, let {
            buffer.position(yLen + vLen)
            buffer.slice().apply { limit(uLen) }
        })
        return arrayOf(planeY, planeU, planeV)
    }

    private fun initForI420(
        byteArray: ByteArray,
        width: Int,
        height: Int,
        buffer: ByteBuffer
    ): Array<ImageWrapper.PlaneWrapper> {
        val planes = initForYV12(byteArray, width, height, buffer)
        return arrayOf(planes[0], planes[2], planes[1])
    }

    private fun initForNV21(
        byteArray: ByteArray,
        width: Int,
        height: Int,
        buffer: ByteBuffer
    ): Array<ImageWrapper.PlaneWrapper> {
        val yLen = width * height
        val uvLen = width * height / 2

        buffer.position(0)
        buffer.put(byteArray, 0, yLen + uvLen)

        val planeY = ByteArrayPlane(width, 0, let {
            buffer.position(0)
            buffer.slice().apply { limit(yLen) }
        })
        val planeUV = ByteArrayPlane(width, 1, let {
            buffer.position(yLen)
            buffer.slice().apply { limit(uvLen) }
        })
        return arrayOf(planeY, planeUV)
    }

    private fun initForY800(
        byteArray: ByteArray,
        width: Int,
        height: Int,
        buffer: ByteBuffer
    ): Array<ImageWrapper.PlaneWrapper> {
        val yLen = width * height

        buffer.position(0)
        buffer.put(byteArray, 0, yLen)

        val planeY = ByteArrayPlane(width, 0, let {
            buffer.position(0)
            buffer.slice().apply { limit(yLen) }
        })
        return arrayOf(planeY)
    }

    private fun initForRGB(
        byteArray: ByteArray,
        width: Int,
        height: Int,
        buffer: ByteBuffer,
        format: String
    ): Array<ImageWrapper.PlaneWrapper> {
        val length = width * height * ImageFormat.getBitsPerPixel(format) / 8
        val pixelStride = when (format) {
            ImageFormat.RGBP -> 2
            else -> 4
        }
        buffer.position(0)
        buffer.put(byteArray, 0, length)
        val plane = ByteArrayPlane(width, pixelStride, buffer)
        return arrayOf(plane)
    }

    override fun close() {
        // release buffer
        OpenScanner.sharedBufferPool.release(internalBuffer)
        // close
        owner.close(byteArray)
    }

    override val planes: Array<ImageWrapper.PlaneWrapper> = planeWrappers
    override val payload: ByteArray = byteArray

    internal class ByteArrayPlane(
        override val rowStride: Int,
        override val pixelStride: Int,
        override val buffer: ByteBuffer
    ) : ImageWrapper.PlaneWrapper
}