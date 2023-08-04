package net.rabbitknight.open.media.data.image

import android.graphics.Bitmap
import androidx.core.graphics.BitmapCompat
import net.rabbitknight.open.media.data.ImageWrapper
import net.rabbitknight.open.media.core.MediaException
import net.rabbitknight.open.media.data.WrapperOwner
import net.rabbitknight.open.media.data.format.ImageFormat
import net.rabbitknight.open.media.data.pool.ByteBufferPool
import java.nio.ByteBuffer

class BitmapImage(
    private val allocator: ByteBufferPool,
    override val owner: WrapperOwner<Bitmap>,
    private val bitmap: Bitmap,
    override val timestamp: Long,
) : ImageWrapper<Bitmap> {
    private val internalBuffer: ByteBuffer
    private val bitmapFormat: String
    private val bitmapWidth: Int
    private val bitmapHeight: Int
    private val bitmapPlanes: Array<ImageWrapper.PlaneWrapper>

    init {
        val config = bitmap.config
        if (config != Bitmap.Config.ARGB_8888 && config != Bitmap.Config.RGB_565) {
            throw MediaException("not support $config")
        }
        bitmapFormat =
            if (config == Bitmap.Config.ARGB_8888) ImageFormat.ARGB else ImageFormat.RGB_565
        bitmapWidth = bitmap.width
        bitmapHeight = bitmap.height
        val byteSize = BitmapCompat.getAllocationByteCount(bitmap)
        // extra buffer
        internalBuffer = allocator.acquire(byteSize)
        internalBuffer.position(0)
        internalBuffer.limit(byteSize)
        bitmap.copyPixelsToBuffer(internalBuffer)
        // plane
        val pixelStride = if (config == Bitmap.Config.ARGB_8888) 4 else 2
        val rowStride = bitmap.rowBytes
        val plane = BitmapPlane(rowStride, pixelStride, internalBuffer)
        bitmapPlanes = arrayOf(plane)
    }

    override fun close() {
        allocator.release(internalBuffer)
        owner.close(bitmap)
    }

    override val format: String = bitmapFormat
    override val width: Int = bitmapWidth
    override val height: Int = bitmapHeight
    override val planes: Array<ImageWrapper.PlaneWrapper> = bitmapPlanes
    override val payload: Bitmap = bitmap

    internal class BitmapPlane(
        override val rowStride: Int,
        override val pixelStride: Int,
        override val buffer: ByteBuffer
    ) : ImageWrapper.PlaneWrapper
}