package net.rabbitknight.open.scanner.engine.wechat

import android.util.Log
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

    private external fun init(
        detector_prototxt_path: String,
        detector_caffe_model_path: String,
        super_resolution_prototxt_path: String,
        super_resolution_caffe_model_path: String
    ): Long

    private external fun release(peer: Long)

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