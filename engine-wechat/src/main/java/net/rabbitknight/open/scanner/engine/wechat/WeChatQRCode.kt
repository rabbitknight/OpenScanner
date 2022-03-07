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
     * qrcode识别
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
     * 释放解码器资源
     * 一旦调用这个方法 将不能再被使用
     */
    fun release() {
        release(peer)
        peer = 0
    }

    private external fun init(
        detector_prototxt_path: String,
        detector_caffe_model_path: String,
        super_resolution_prototxt_path: String,
        super_resolution_caffe_model_path: String
    ): Long

    private external fun release(peer: Long)

    private external fun detect(peer: Long, image: ByteArray, width: Int, height: Int): IntArray?

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