package net.rabbitknight.open.scanner

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import net.rabbitknight.open.scanner.core.C
import net.rabbitknight.open.scanner.core.OpenScanner
import net.rabbitknight.open.scanner.core.config.InitOption
import net.rabbitknight.open.scanner.core.image.wrap
import net.rabbitknight.open.scanner.engine.hwscankit.HWScanKitEngine
import net.rabbitknight.open.scanner.engine.mlkit.MLKitEngine
import net.rabbitknight.open.scanner.engine.wechat.WeChatEngine
import net.rabbitknight.open.scanner.engine.zbar.ZBarEngine
import net.rabbitknight.open.scanner.engine.zxing.ZXingEngine

class DemoApp : Application() {
    companion object {
        private const val TAG = "DemoApp"
    }

    override fun onCreate() {
        super.onCreate()

        // 参数配置
        val option = InitOption()

        // Engine列表
        val engines = arrayOf(
            WeChatEngine::class.java,
            HWScanKitEngine::class.java,
            ZBarEngine::class.java,
            ZXingEngine::class.java,
            MLKitEngine::class.java
        )

        // 实例创建
        val scanner = OpenScanner.create(option, engines)

        // 图像创建
        val bitmap = assets.open("qrcode/test.1.png").use {
            BitmapFactory.decodeStream(it)
        }
//        val bitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888)

        // 图像封装
        val image = bitmap.wrap(0L) {
            // 类型回收
            Log.i(TAG, "close: raw image payload release!")
        }

        // 每帧 结果
        scanner.getResult { raw, result ->
            // 时间戳
            val timestamp = raw.timestamp
            // 是否成功检测
            val success = result.code == C.CODE_SUCCESS
            // 所有检测结果
            result.result.forEach {
                Log.i(
                    TAG,
                    "process ${timestamp}: format = ${it.format},box = ${it.rect}, content = ${it.payload}"
                )
            }
        }

        // 图像处理
        val inputRst = scanner.process(image) { raw, result ->
            // 这里是单次结果回调

            // 原始数据 此时已经被回收了
            Log.i(TAG, "scanner.process() output: image is ${raw} ")
            // 处理结果 获取
            Log.i(TAG, "scanner.process() output: result is ${result}")
        }

        // 是否被scanner正常接收
        Log.d(TAG, "scanner.process() input rst = ${inputRst}")
    }
}