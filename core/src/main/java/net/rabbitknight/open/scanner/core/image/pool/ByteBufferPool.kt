package net.rabbitknight.open.scanner.core.image.pool

import net.rabbitknight.open.scanner.core.image.ImageFormat
import net.rabbitknight.open.scanner.core.image.ImageFormat.Format
import java.nio.ByteBuffer
import java.util.*

/**
 * ByteBufferPool like [Glide#LruBitmapPool]
 */
class ByteBufferPool(private val maxSize: Long = 1024 * 1024 * 20) {
    /**
     * size to remain count
     */
    private val sortedSize = TreeMap<Int, Int>()

    /**
     * size to Buffer
     */
    private val cacheMap = GroupedLinkedMap<Int, ByteBuffer>()

    private var level: Int = 0

    /**
     * 获取一个buffer
     */
    @Synchronized
    fun acquire(width: Int, height: Int, @Format format: String): ByteBuffer {
        val pixelSize = ImageFormat.getBitsPerPixel(format) / 8
        val wantedSize = width * height * pixelSize

        val possibleSize = sortedSize.ceilingKey(wantedSize) ?: wantedSize
        val remainCount = possibleSize.let { sortedSize[it] } ?: 0

        return if (remainCount == 0) {
            get(width, height, format)
        } else {
            sortedSize[possibleSize] = remainCount - 1
            val buffer = cacheMap.get(possibleSize)
            if (buffer != null) {
                level -= buffer.capacity()
            }
            return buffer ?: get(width, height, format)
        }
    }

    /**
     * 回到到缓存
     */
    @Synchronized
    fun release(buffer: ByteBuffer): Boolean {
        if (!buffer.isDirect) return false

        val capacity = buffer.capacity()
        cacheMap.put(capacity, buffer)

        val remain = sortedSize[capacity] ?: 0
        sortedSize[capacity] = remain + 1

        level += capacity

        trimToSize(maxSize)
        return true
    }

    fun getLevel() = level

    private fun get(width: Int, height: Int, @Format format: String): ByteBuffer {
        val pixelSize = ImageFormat.getBitsPerPixel(format) / 8
        val wantedSize = width * height * pixelSize
        return ByteBuffer.allocateDirect(wantedSize);
    }

    private fun trimToSize(maxSize: Long) {
        while (level > maxSize) {
            val last = removeLast()
            if (last == null) {
                level = 0
                return
            }
            level -= last.capacity()
        }
    }

    private fun removeLast(): ByteBuffer? {
        val removed = cacheMap.removeLast()
        removed?.let {
            val capacity = it.capacity()
            val remain = sortedSize[it.capacity()]!!
            sortedSize[capacity] = remain - 1
        }
        return removed
    }
}