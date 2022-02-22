package net.rabbitknight.open.scanner.core.image

import android.media.Image
import java.nio.ByteBuffer

class Camera2Image(
    private val image: Image
) : ImageWrapper {
    private var planeWrappers: Array<ImageWrapper.PlaneWrapper> = Array(3) { index ->
        val rawPlane = image.planes[index]
        val rowStride = rawPlane.rowStride
        val pixelStride = rawPlane.pixelStride
        val buffer = ByteBuffer.allocate(rawPlane.buffer.capacity()).also {
            it.put(rawPlane.buffer)
            it.flip()
        }
        Image2Plane(rowStride, pixelStride, buffer)
    }

    override fun close() {
        image.close()
    }

    override val format: Int
        get() = image.format
    override val height: Int
        get() = image.height
    override val width: Int
        get() = image.width
    override val planes: Array<ImageWrapper.PlaneWrapper>
        get() = planeWrappers
    override val payload: Any
        get() = image
    override val timestamp: Long
        get() = image.timestamp

    internal class Image2Plane(
        override val rowStride: Int,
        override val pixelStride: Int,
        override val buffer: ByteBuffer
    ) : ImageWrapper.PlaneWrapper
}