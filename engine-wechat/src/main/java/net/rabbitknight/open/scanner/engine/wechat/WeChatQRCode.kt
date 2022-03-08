package net.rabbitknight.open.scanner.engine.wechat

import android.util.Log
import net.rabbitknight.open.scanner.core.result.Rect
import net.rabbitknight.open.scanner.engine.wechat.AssetsLoader.getFile

class WeChatQRCode {
    private var peer: Long = 0

    companion object {
        private const val TAG = "WeChatQRCode"
        private var isAvailable = false

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
        rects.forEach {
            val candidate_point = intArrayOf(it.left, it.top, it.right, it.bottom)
            val text = decode(peer, image, width, height, candidate_point)
            if (text != null) {
                outRects.add(it)
                outTexts.add(text)
            }
        }
        return true
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
        candidate_point: IntArray
    ): String?

    // endregion Native Bridge

    init {
        val detector_prototxt_path = getFile(C.ASSET_DETECT_PROTO)?.absolutePath ?: ""
        val detector_caffe_model_path = getFile(C.ASSET_DETECT_CAFFE)?.absolutePath ?: ""
        val super_resolution_prototxt_path = getFile(C.ASSET_SR_PROTO)?.absolutePath ?: ""
        val super_resolution_caffe_model_path = getFile(C.ASSET_SR_CAFFE)?.absolutePath ?: ""
        peer = init(
            detector_prototxt_path,
            detector_caffe_model_path,
            super_resolution_prototxt_path,
            super_resolution_caffe_model_path
        )
    }
}