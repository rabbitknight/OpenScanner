package net.rabbitknight.open.media.data.image

import android.media.Image
import net.rabbitknight.open.media.data.ImageWrapper
import net.rabbitknight.open.media.data.WrapperOwner
import net.rabbitknight.open.media.data.format.ImageFormat
import net.rabbitknight.open.media.data.pool.ByteBufferPool
import java.nio.ByteBuffer

class Camera2Image(
    private val allocator: ByteBufferPool,
    override val owner: WrapperOwner<Image>,
    private val image: Image,
) : ImageWrapper<Image> {
    private val imageWidth = image.width
    private val imageHeight = image.height
    private val imageTs = image.timestamp

    private val planeWrappers: Array<ImageWrapper.PlaneWrapper> =
        image.planes.map { plane ->
            val rowStride = plane.rowStride
            val pixelStride = plane.pixelStride
            val buffer = plane.buffer
            Image2Plane(rowStride, pixelStride, buffer)
        }.toTypedArray()

    override fun close() {
        owner.close(payload)
    }

    override val format: String = ImageFormat.A420
    override val height: Int = imageHeight
    override val width: Int = imageWidth
    override val planes: Array<ImageWrapper.PlaneWrapper> = planeWrappers
    override val payload: Image = image
    override val timestamp: Long = imageTs

    internal class Image2Plane(
        override val rowStride: Int,
        override val pixelStride: Int,
        override val buffer: ByteBuffer
    ) : ImageWrapper.PlaneWrapper
}