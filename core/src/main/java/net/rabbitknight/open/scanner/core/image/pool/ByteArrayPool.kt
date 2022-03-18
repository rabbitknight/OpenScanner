package net.rabbitknight.open.scanner.core.image.pool

import net.rabbitknight.open.scanner.core.image.ImageFormat

class ByteArrayPool(maxSize: Long = 1024 * 1024 * 20) : BaseCachePool<ByteArray>(maxSize) {

    /**
     * 获取一个buffer
     */
    @Synchronized
    fun acquire(width: Int, height: Int, @ImageFormat.Format format: String): ByteArray {
        val pixelSize = ImageFormat.getBitsPerPixel(format) / 8
        val wantedSize = width * height * pixelSize
        return acquire(wantedSize)
    }

    override fun createCache(size: Int): ByteArray = ByteArray(size)
    override fun getSize(cache: ByteArray): Int = cache.size
}