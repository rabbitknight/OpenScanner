package net.rabbitknight.open.media.data

import net.rabbitknight.open.media.data.format.ImageFormat
import java.nio.ByteBuffer

/**
 * 图像包装
 * 1. 只应该是对图像数据的包装,不应该再开辟任何内存
 */
interface ImageWrapper<out T : Any> {
    /**
     * 用来回收buffer
     */
    val owner: WrapperOwner<out T>

    /**
     * Closes the underlying [android.media.Image].
     *
     * @see android.media.Image.close
     */
    fun close()

    /**
     * Returns the image format.
     * @see ImageFormat
     */
    @ImageFormat.Format
    val format: String

    /**
     * Returns the image width.
     *
     * @see android.media.Image.getWidth
     */
    val width: Int

    /**
     * Returns the image height.
     *
     * @see android.media.Image.getHeight
     */
    val height: Int

    /**
     * timestamp
     */
    val timestamp: Long

    /**
     * Returns the array of planes.
     *
     * @see android.media.Image.getPlanes
     */
    val planes: Array<PlaneWrapper>

    val payload: T

    /**
     * A plane proxy which has an analogous interface as [android.media.Image.Plane].
     */
    interface PlaneWrapper {
        /**
         * Returns the row stride.
         *
         * @see android.media.Image.Plane.getRowStride
         */
        val rowStride: Int

        /**
         * Returns the pixel stride.
         *
         * @see android.media.Image.Plane.getPixelStride
         */
        val pixelStride: Int

        /**
         * Returns the pixels buffer.
         *
         * @see android.media.Image.Plane.getBuffer
         */
        val buffer: ByteBuffer
    }
}

/**
 * ImageWrapper只是个包装类
 * 不应该对payload做任何变更
 * 当wrapper不再使用时，通知外面自行处理
 */
interface WrapperOwner<T : Any> {
    /**
     * 资源被使用完，触发该方法
     * Owner应自行处理回收
     */
    fun close(payload: T)
}