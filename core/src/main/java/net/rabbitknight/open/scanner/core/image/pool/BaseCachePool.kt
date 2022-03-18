package net.rabbitknight.open.scanner.core.image.pool

import java.util.*

/**
 * BaseCachePool like [Glide#LruBitmapPool]
 */
abstract class BaseCachePool<T>(private val maxSize: Long = 1024 * 1024 * 20) {
    /**
     * size to remain count
     */
    private val sortedSize = TreeMap<Int, Int>()

    /**
     * size to Buffer
     */
    private val cacheMap = GroupedLinkedMap<Int, T>()

    private var level: Int = 0

    /**
     * 获取一个buffer
     */
    @Synchronized
    open fun acquire(wantedSize: Int): T {
        val possibleSize = sortedSize.ceilingKey(wantedSize) ?: wantedSize
        val remainCount = possibleSize.let { sortedSize[it] } ?: 0
        return if (remainCount == 0) {
            createCache(wantedSize)
        } else {
            sortedSize[possibleSize] = remainCount - 1
            val buffer = cacheMap.get(possibleSize)
            if (buffer != null) {
                level -= getSize(buffer)
            }
            return buffer ?: createCache(wantedSize)
        }
    }

    /**
     * 回到到缓存
     */
    @Synchronized
    open fun release(buffer: T): Boolean {
        val capacity = getSize(buffer)
        cacheMap.put(capacity, buffer)

        val remain = sortedSize[capacity] ?: 0
        sortedSize[capacity] = remain + 1

        level += capacity

        trimToSize(maxSize)
        return true
    }

    fun getLevel() = level

    protected abstract fun createCache(size: Int): T

    protected abstract fun getSize(cache: T): Int

    private fun trimToSize(maxSize: Long) {
        while (level > maxSize) {
            val last = removeLast()
            if (last == null) {
                level = 0
                return
            }
            level -= getSize(last)
        }
    }

    private fun removeLast(): T? {
        val removed = cacheMap.removeLast()
        removed?.let {
            val capacity = getSize(it)
            val remain = sortedSize[capacity]!!
            sortedSize[capacity] = remain - 1
        }
        return removed
    }
}