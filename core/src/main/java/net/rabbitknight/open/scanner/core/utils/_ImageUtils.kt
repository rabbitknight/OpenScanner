package net.rabbitknight.open.scanner.core.utils

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.YuvImage
import android.util.Log
import net.rabbitknight.open.scanner.core.TAG
import net.rabbitknight.open.scanner.core.image.ImageFormat
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

internal fun <T : Any> ImageWrapper<T>.download(file: File): Boolean {
    if (payload !is ByteArray) {
        Log.w(TAG, "download: $this fail, only support ByteArray")
        return false
    }
    val supportList = arrayOf(ImageFormat.YV12, ImageFormat.I420, ImageFormat.ARGB)
    if (supportList.all { it != format }) {
        return false
    }
    if (!file.exists()) {
        file.createNewFile()
    }
    when (format) {
        ImageFormat.ARGB -> {
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val buffer = ByteBuffer.wrap(payload as ByteArray)
            bitmap.copyPixelsFromBuffer(buffer)
            val os = FileOutputStream(file)
            val rst = os.use {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            }
            if (!rst) Log.w(TAG, "download: compress fail")
            return rst
        }
        else -> {
            val strides = arrayOf(width, width / 2, width / 2).toIntArray()
            val image = YuvImage(
                payload as ByteArray,
                android.graphics.ImageFormat.NV21,
                width,
                height,
                strides
            )
            val rect = Rect(0, 0, width, height)
            val os = FileOutputStream(file)
            val rst = os.use {
                image.compressToJpeg(rect, 100, it)
            }
            if (!rst) Log.w(TAG, "download: compress fail")
            return rst
        }
    }
    return true
}