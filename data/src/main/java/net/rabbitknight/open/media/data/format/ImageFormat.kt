package net.rabbitknight.open.media.data.format

import android.graphics.ImageFormat
import androidx.annotation.StringDef

/**
 * 在Android中，图形格式定义是比较混乱的，如[PixelFormat]/[ImageFormat]/[Bitmap#Config].
 * 为了平衡差异性，这里直接使用FOURCC中的定义，并选取了常用的Format作为开放的格式
 * @see [fourcc](https://www.fourcc.org/yuv.php)
 * @see
 */
object ImageFormat {
    const val Y800 = "I400"
    const val I420 = "I420"
    const val YV12 = "YV12"
    const val NV21 = "NV21"
    const val RGBA = "RGBA"
    const val ARGB = "ARGB"
    const val BGRA = "BGRA"
    const val A420 = "420_888"
    const val RGBP = "RGBP"

    const val RGB_565 = RGBP
    const val YUV_420_888 = A420

    @Target(
        AnnotationTarget.PROPERTY,
        AnnotationTarget.VALUE_PARAMETER,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.TYPE
    )
    @StringDef(value = [Y800, I420, YV12, NV21, RGBA, ARGB, BGRA, A420, RGBP])
    annotation class Format

    /**
     * 获取每个格式单位像素所占用的空间
     */
    fun getBitsPerPixel(@Format format: String): Int {
        return when (format) {
            Y800 -> 8
            I420 -> 12
            YV12 -> 12
            NV21 -> 12
            RGBA -> 32
            ARGB -> 32
            BGRA -> 32
            A420 -> 24
            RGBP -> 16
            else -> -1
        }
    }

    /**
     * 获取每个格式 占用的平面数量
     * 注意: 平面数并不是和Pixel占用对其的，对于交错的格式，通常被归到同一个“交错”平面中去，而不是通过pixelOffset区分
     */
    fun getPlaneSize(@Format format: String): Int {
        return when (format) {
            Y800 -> 1
            I420 -> 3
            YV12 -> 3
            NV21 -> 2
            RGBA -> 1
            ARGB -> 1
            BGRA -> 1
            A420 -> 3
            RGBP -> 1
            else -> -1
        }
    }

    /**
     * 是否是yuv数据
     */
    fun isYUV(@Format format: String): Boolean {
        return when (format) {
            Y800 -> true
            I420 -> true
            YV12 -> true
            NV21 -> true
            A420 -> true
            else -> false
        }
    }
}