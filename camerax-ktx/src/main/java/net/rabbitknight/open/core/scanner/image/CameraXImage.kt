package net.rabbitknight.open.core.scanner.image

import androidx.camera.core.ImageProxy
import net.rabbitknight.open.scanner.core.image.ImageFormat
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.image.WrapperOwner
import java.nio.ByteBuffer

class CameraXImage(
    override val owner: WrapperOwner<ImageProxy>,
    private val image: ImageProxy,
) : ImageWrapper<ImageProxy> {
    private var planeWrappers: Array<ImageWrapper.PlaneWrapper> = Array(3) { index ->
        val rawPlane = image.planes[index]
        val rowStride = rawPlane.rowStride
        val pixelStride = rawPlane.pixelStride
        val buffer = rawPlane.buffer
        Image2Plane(rowStride, pixelStride, buffer)
    }

    override fun close() {
        owner.close(image)
    }

    override val format: String
        get() = ImageFormat.A420
    override val height: Int
        get() = image.height
    override val width: Int
        get() = image.width
    override val planes: Array<ImageWrapper.PlaneWrapper>
        get() = planeWrappers
    override val payload: ImageProxy
        get() = image
    override val timestamp: Long
        get() = image.imageInfo.timestamp

    internal class Image2Plane(
        override val rowStride: Int,
        override val pixelStride: Int,
        override val buffer: ByteBuffer
    ) : ImageWrapper.PlaneWrapper
}