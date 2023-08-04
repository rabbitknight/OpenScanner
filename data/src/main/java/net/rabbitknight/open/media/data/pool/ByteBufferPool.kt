package net.rabbitknight.open.media.data.pool

import net.rabbitknight.open.media.data.format.ImageFormat
import java.nio.ByteBuffer

/**
 * ByteBufferPool like [Glide#LruBitmapPool]
 */
class ByteBufferPool(maxSize: Long = 1024 * 1024 * 20) : BaseCachePool<ByteBuffer>() {

    /**
     * 获取一个buffer
     */
    @Synchronized
    fun acquire(width: Int, height: Int, @ImageFormat.Format format: String): ByteBuffer {
        val pixelSize = ImageFormat.getBitsPerPixel(format)
        val wantedSize = width * height * pixelSize / 8.0f
        return acquire(wantedSize.toInt())
    }

    /**
     * 回到到缓存
     */
    override fun release(buffer: ByteBuffer): Boolean {
        if (!buffer.isDirect) return false
        return super.release(buffer)
    }

    override fun createCache(size: Int): ByteBuffer = ByteBuffer.allocateDirect(size)

    override fun getSize(cache: ByteBuffer): Int = cache.capacity()
}