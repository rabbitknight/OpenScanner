package net.rabbitknight.open.scanner.core.utils

import android.graphics.ImageFormat
import net.rabbitknight.open.scanner.core.ScannerException
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.image.wrap
import net.rabbitknight.open.yuvutils.YuvUtils

object ImageUtils {

    /**
     * 将图像转化为YV12
     */
    fun convertImageToYV12(input: ImageWrapper, dest: ByteArray): ImageWrapper {
        val width = input.width
        val height = input.height
        val yLen = width * height
        val uLen = width / 2 * height / 2
        val vLen = width / 2 * height / 2
        if (dest.size < yLen + uLen + vLen) {
            throw ScannerException("dest size${dest.size},but wanted ${yLen + uLen + vLen}!")
        }
        val planeY = input.planes[0]
        val planeU = input.planes[1]
        val planeV = input.planes[2]
        // 借用i420转换为yv12
        YuvUtils.convertAndroid420ToI420(
            planeY.buffer, planeY.rowStride,
            planeU.buffer, planeU.rowStride,
            planeV.buffer, planeV.rowStride,
            planeU.pixelStride,
            dest, 0, width,
            dest, yLen + uLen, width / 2,
            dest, yLen, width / 2,
            width, height
        )
        return dest.wrap(ImageFormat.YV12, width, height, input.timestamp)
    }

    /**
     * 剪裁图像
     */
    fun cropImage(
        input: ImageWrapper,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        dest: ByteArray
    ): ImageWrapper {
        TODO()
    }
}