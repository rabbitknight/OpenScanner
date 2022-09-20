package net.rabbitknight.open.scanner.engine.wechat

import android.util.Log
import net.rabbitknight.open.scanner.core.engine.AssetsLoader
import net.rabbitknight.open.scanner.core.result.Rect

class WeChatQRCode {
    private var peer: Long = 0

    companion object {
        private const val TAG = "WeChatQRCode"
        private var isAvailable = false

        /**
         * max detect size
         */
        private const val MAX_SIZE = 10

        init {
            try {
                System.loadLibrary("wechat_engine")
                isAvailable = true
            } catch (e: Exception) {
                Log.w(TAG, "static initializer() fail!", e)
            }
        }
    }

    val isAvailable: Boolean
        get() = Companion.isAvailable

    /**
     * qrcode检测 返回兴趣区域
     * @param image 图像 必须是Y8数据，与CV_8UC1 对应
     * @param width y 宽度
     * @param height y 高度
     * @param out 输出的容器
     * @return true代表正常检测(但是不一定有结果),false代表检测失败
     */
    fun detect(image: ByteArray, width: Int, height: Int, out: MutableList<Rect>): Boolean {
        out.clear()
        if (peer == 0L) return false
        val result = detect(peer, image, width, height) ?: return false
        for (i in 0 until result.size / 4) {
            val left = result[i * 4 + 0]
            val top = result[i * 4 + 1]
            val right = result[i * 4 + 2]
            val bottom = result[i * 4 + 3]
            out.add(Rect(left, top, right, bottom))
        }
        return true
    }

    /**
     * qrcode 识别
     * @param image 图像 必须是Y8数据，与CV_8UC1 对应
     * @param width y 宽度
     * @param height y 高度
     * @param rois  region of interest,如果没有则进行一次检测
     * @param outRects 二维码框
     * @param outTexts 二维码值
     * @return true代表正常检测(但是不一定有结果),false代表检测失败
     */
    fun decode(
        image: ByteArray,
        width: Int,
        height: Int,
        rois: List<Rect>,
        outRects: MutableList<Rect>,
        outTexts: MutableList<String>
    ): Boolean {
        outRects.clear()
        outTexts.clear()
        if (peer == 0L) return false
        // if not roi,detect once
        val rects = rois.ifEmpty {
            val result = mutableListOf<Rect>()
            detect(image, width, height, result)
            result
        }
        // decode every roi to get barcode
        val candidate_points = IntArray(rects.size * 4);
        rects.forEachIndexed { index, rect ->
            candidate_points[index * 4 + 0] = rect.left
            candidate_points[index * 4 + 1] = rect.top
            candidate_points[index * 4 + 2] = rect.right
            candidate_points[index * 4 + 3] = rect.bottom
        }
        val res_points = IntArray(rects.size * 4)
        val texts = decode(peer, image, width, height, candidate_points, rects.size, res_points)
        texts?.forEachIndexed { index, text ->
            val left = res_points[index * 4 + 0]
            val top = res_points[index * 4 + 1]
            val right = res_points[index * 4 + 2]
            val bottom = res_points[index * 4 + 3]
            outRects.add(Rect(left, top, right, bottom))
            outTexts.add(text)
        }
        return true
    }

    /**
     * 检测并识别
     */
    fun detectAndDecode(
        image: ByteArray,
        width: Int,
        height: Int,
        outRects: MutableList<Rect>,
        outTexts: MutableList<String>
    ): Boolean {
        outRects.clear()
        outRects.clear()
        if (peer == 0L) return false
        val res_points = IntArray(MAX_SIZE * 4);
        val res_texts = Array(MAX_SIZE) { "" }
        val count = detectAndDecode(peer, image, width, height, res_points, res_texts)
        if (count == -1) {
            return false
        }
        for (index in 0 until count) {
            val left = res_points[index * 4 + 0]
            val top = res_points[index * 4 + 1]
            val right = res_points[index * 4 + 2]
            val bottom = res_points[index * 4 + 3]
            outRects.add(Rect(left, top, right, bottom))
            outTexts.add(res_texts[index])
        }
        return true
    }

    /**
     * @brief set scale factor
     * QR code detector use neural network to detect QR.
     * Before running the neural network, the input image is pre-processed by scaling.
     * By default, the input image is scaled to an image with an area of 160000 pixels.
     * The scale factor allows to use custom scale the input image:
     * width = scaleFactor*width
     * height = scaleFactor*width
     *
     * scaleFactor valuse must be > 0 and <= 1, otherwise the scaleFactor value is set to -1
     * and use default scaled to an image with an area of 160000 pixels.
     */
    fun setScaleFactor(factor: Float): Boolean {
        if (peer == 0L) return false
        return setScaleFactor(peer, factor)
    }

    fun getScaleFactor(): Float {
        if (peer == 0L) return -1.0f
        return getScaleFactor(peer)
    }

    /**
     * 释放解码器资源
     * 一旦调用这个方法 将不能再被使用
     */
    fun release() {
        release(peer)
        peer = 0
    }

    // region Native Bridge
    /**
     * init wechat_engine with model/proto
     */
    private external fun init(
        detector_prototxt_path: String,
        detector_caffe_model_path: String,
        super_resolution_prototxt_path: String,
        super_resolution_caffe_model_path: String
    ): Long

    /**
     * release wechat_engine
     */
    private external fun release(peer: Long)

    /**
     * detect candidate_points for image
     */
    private external fun detect(peer: Long, image: ByteArray, width: Int, height: Int): IntArray?

    /**
     * decode with single candidate_point
     * combine all points on outside
     */
    private external fun decode(
        peer: Long,
        image: ByteArray,
        width: Int,
        height: Int,
        candidate_points: IntArray,
        candidate_size: Int,
        res_points: IntArray,
    ): Array<String>?

    /**
     * detect and decode the image
     */
    private external fun detectAndDecode(
        peer: Long,
        image: ByteArray,
        width: Int,
        height: Int,
        res_points: IntArray,
        res_texts: Array<String>
    ): Int

    private external fun setScaleFactor(peer: Long, factor: Float): Boolean

    private external fun getScaleFactor(peer: Long): Float

    // endregion Native Bridge

    init {
        val detector_prototxt_path = AssetsLoader.getFile(C.ASSET_DETECT_PROTO)?.absolutePath ?: ""
        val detector_caffe_model_path =
            AssetsLoader.getFile(C.ASSET_DETECT_CAFFE)?.absolutePath ?: ""
        val super_resolution_prototxt_path =
            AssetsLoader.getFile(C.ASSET_SR_PROTO)?.absolutePath ?: ""
        val super_resolution_caffe_model_path =
            AssetsLoader.getFile(C.ASSET_SR_CAFFE)?.absolutePath ?: ""
        peer = init(
            detector_prototxt_path,
            detector_caffe_model_path,
            super_resolution_prototxt_path,
            super_resolution_caffe_model_path
        )
    }
}