package net.rabbitknight.open.scanner.core.image

import java.nio.ByteBuffer

/**
 * 图像代理
 */
interface ImageWrapper {
    /**
     * Closes the underlying [android.media.Image].
     *
     * @see android.media.Image.close
     */
    fun close()

    /**
     * Returns the image format.
     *
     *
     *  The image format can be one of the [android.graphics.ImageFormat] or
     * [android.graphics.PixelFormat] constants.
     *
     * @see android.media.Image.getFormat
     */
    val format: Int

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

    val payload: Any?

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