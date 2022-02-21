package net.rabbitknight.open.scanner.core.source

import net.rabbitknight.open.scanner.core.image.ImageProxy

interface Source {
    /**
     * 可获取图片的数量
     */
    fun available(): Int

    /**
     * 取出一帧图片
     */
    fun take(): ImageProxy

    fun close()
}