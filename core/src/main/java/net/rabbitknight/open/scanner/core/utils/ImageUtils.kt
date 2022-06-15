package net.rabbitknight.open.scanner.core.utils

import android.graphics.Bitmap
import android.media.Image
import android.util.Log
import net.rabbitknight.open.scanner.core.OpenScanner
import net.rabbitknight.open.scanner.core.ScannerException
import net.rabbitknight.open.scanner.core.image.ImageFormat
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.yuvutils.YuvUtils

object ImageUtils {
    private const val TAG = "ImageUtils"
    private val bufferPool = OpenScanner.sharedBufferPool
    private val arrayPool = OpenScanner.sharedArrayPool

    /**
     * 将ByteArray数据转换为ARGB
     */
    fun convertByteArrayToARGB(input: ImageWrapper<ByteArray>, dest: ByteArray): Boolean {
        val format = YuvUtils.fourcc(input.format)
        val rst = YuvUtils.convertToARGB(
            input.payload, 0, input.payload.size,
            dest, 0, input.width, 0, 0, input.width, input.height,
            input.width, input.height, 0, format
        )
        if (rst != 0) {
            Log.w(TAG, "convertByteArrayToARGB: ${input},fail [${rst}]")
        }
        return rst == 0
    }

    /**
     * 将ByteArray数据转换为YV12
     */
    fun convertByteArrayToYV12(input: ImageWrapper<ByteArray>, dest: ByteArray): Boolean {
        val format = YuvUtils.fourcc(input.format)
        val width = input.width
        val height = input.height
        val yLen = width * height
        val uLen = width / 2 * height / 2
        val vLen = width / 2 * height / 2
        val rst = YuvUtils.convertToI420(
            input.payload, 0, input.payload.size,
            dest, 0, width,
            dest, yLen + uLen, width / 2,
            dest, yLen, width / 2,
            0, 0, width, height,
            width, height, 0, format
        )
        if (rst != 0) {
            Log.w(TAG, "convertByteArrayToYV12: ${input},fail [${rst}]")
        }
        return rst == 0
    }

    /**
     * 将图像转化为YV12
     */
    fun convertImageToYV12(
        input: ImageWrapper<Image>,
        dest: ByteArray
    ): Boolean {
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
        val rst = YuvUtils.convertAndroid420ToI420(
            planeY.buffer, planeY.rowStride,
            planeU.buffer, planeU.rowStride,
            planeV.buffer, planeV.rowStride,
            planeU.pixelStride,
            dest, 0, width,
            dest, yLen + uLen, width / 2,
            dest, yLen, width / 2,
            width, height
        )
        return rst == 0
    }

    /**
     * 图像剪裁
     */
    fun cropImage(
        input: ImageWrapper<Any>,
        left: Int, top: Int, cropWidth: Int, cropHeight: Int,
        dest: ByteArray, result: (Boolean, @ImageFormat.Format String) -> Unit
    ) {
        val format: String
        val rst = when (input.payload) {
            // bitmap类型数据
            is Bitmap -> {
                format = ImageFormat.ARGB
                cropBitmapImage(
                    input as ImageWrapper<Bitmap>,
                    left, top, cropWidth, cropHeight, dest
                )
            }
            // bytearray类型数据
            is ByteArray -> {
                if (ImageFormat.isYUV(input.format)) {
                    format = ImageFormat.I420
                    cropYUVByteArrayImage(
                        input as ImageWrapper<ByteArray>,
                        left, top, cropWidth, cropHeight, dest
                    )
                } else {
                    format = ImageFormat.ARGB
                    cropRGBByteArrayImage(
                        input as ImageWrapper<ByteArray>,
                        left, top, cropWidth, cropHeight, dest
                    )

                }
            }
            // 其他类型，固定为Camera2或CameraX 都必须YUV_420_888
            else -> {
                format = ImageFormat.I420
                cropCamera2Image(input, left, top, cropWidth, cropHeight, dest)
            }
        }
        result.invoke(rst, format)
    }

    /**
     * 剪裁Bitmap数据
     * bitmap数据在内部已提取到缓存中了
     * 输出的格式应该就是ARGB的
     */
    private fun cropBitmapImage(
        input: ImageWrapper<Bitmap>,
        left: Int, top: Int, cropWidth: Int, cropHeight: Int,
        dest: ByteArray
    ): Boolean {
        val plane = input.planes[0]
        val format = YuvUtils.fourcc(input.format)
        val rst = YuvUtils.convertToARGB(
            plane.buffer, plane.buffer.limit(),
            dest, 0, cropWidth,
            left, top, cropWidth, cropHeight,
            input.width, input.height,
            0, format
        )
        if (rst != 0) {
            Log.w(TAG, "cropImage: ${input},fail [${rst}]")
        }
        // 通过convert已经转化为了ARGB类型
        return rst == 0
    }

    /**
     * 裁切RGB ByteArray的数据，
     * RGB支持的都是单通道的数据
     */
    private fun cropRGBByteArrayImage(
        input: ImageWrapper<ByteArray>,
        left: Int, top: Int, cropWidth: Int, cropHeight: Int,
        dest: ByteArray
    ): Boolean {
        val format = YuvUtils.fourcc(input.format)
        val raw = input.payload
        val rst = YuvUtils.convertToARGB(
            raw, 0, raw.size,
            dest, 0, cropWidth,
            left, top, cropWidth, cropHeight,
            input.width, input.height,
            0, format
        )
        if (rst != 0) Log.w(TAG, "cropRGBByteArrayImage: ${input},fail [${rst}]")
        return rst == 0
    }

    /**
     * 剪裁YUV ByteArray的数据
     * YUV的使用同一个ByteArray存储
     */
    private fun cropYUVByteArrayImage(
        input: ImageWrapper<ByteArray>,
        left: Int, top: Int, cropWidth: Int, cropHeight: Int,
        dest: ByteArray
    ): Boolean {
        val format = YuvUtils.fourcc(input.format)
        val raw = input.payload
        val yLen = cropWidth * cropHeight
        val uLen = cropWidth / 2 * cropHeight / 2
        val vLen = cropWidth / 2 * cropHeight / 2
        val rst = YuvUtils.convertToI420(
            raw, 0, raw.size,
            dest, 0, cropWidth,
            dest, yLen, cropWidth / 2,
            dest, yLen + uLen, cropWidth / 2,
            left, top, cropWidth, cropHeight,
            input.width, input.height, 0, format
        )
        if (rst != 0) Log.w(TAG, "cropYUVByteArrayImage: ${input},fail [${rst}]")
        return rst == 0
    }

    /**
     * 剪裁 Image 的数据
     * Image数据要先提取成I420数据
     */
    private fun cropCamera2Image(
        input: ImageWrapper<Any>,
        left: Int, top: Int, cropWidth: Int, cropHeight: Int,
        dest: ByteArray
    ): Boolean {
        if (input.format != ImageFormat.YUV_420_888) {
            throw ScannerException("input.format not YUV_420_888 at $input")
        }
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

        // 申请缓存
        val tmpBuffer = arrayPool.acquire(width, height, input.format)

        // 转换为i420
        val cvtRst = YuvUtils.convertAndroid420ToI420(
            planeY.buffer, planeY.rowStride,
            planeU.buffer, planeU.rowStride,
            planeV.buffer, planeV.rowStride,
            planeU.pixelStride,
            tmpBuffer, 0, width,
            tmpBuffer, yLen, width / 2,
            tmpBuffer, yLen + uLen, width / 2,
            width, height
        )
        if (cvtRst != 0) Log.w(
            TAG,
            "cropImageImage: ${input},convertAndroid420ToI420 fail [${cvtRst}]"
        )

        // 剪裁
        val cropYLen = cropWidth * cropHeight
        val cropULen = cropWidth / 2 * cropHeight / 2
        val format = YuvUtils.fourcc(ImageFormat.I420)
        // 剪裁
        val cropRst = YuvUtils.convertToI420(
            tmpBuffer, 0, tmpBuffer.size,
            dest, 0, cropWidth,
            dest, cropYLen, cropWidth / 2,
            dest, cropYLen + cropULen, cropWidth / 2,
            left, top, cropWidth, cropHeight,
            input.width, input.height, 0, format
        )
        if (cropRst != 0) Log.w(TAG, "cropImageImage: ${input},convertToI420 fail [${cropRst}]")

        // 释放缓存
        arrayPool.release(tmpBuffer)
        // 注意: 这里已经转换为了I420的数据
        return cvtRst == 0
    }
}