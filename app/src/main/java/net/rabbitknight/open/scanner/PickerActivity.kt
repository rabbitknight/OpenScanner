package net.rabbitknight.open.scanner

import android.Manifest.permission.CAMERA
import android.Manifest.permission.INTERNET
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import net.rabbitknight.open.scanner.core.C
import net.rabbitknight.open.scanner.core.OpenScanner
import net.rabbitknight.open.scanner.core.Scanner
import net.rabbitknight.open.scanner.core.config.InitOption
import net.rabbitknight.open.scanner.core.image.wrap
import net.rabbitknight.open.scanner.engine.hwscankit.HWScanKitEngine
import net.rabbitknight.open.scanner.engine.mlkit.MLKitEngine
import net.rabbitknight.open.scanner.engine.wechat.WeChatEngine
import net.rabbitknight.open.scanner.engine.wechat_ncnn.WeChatNCNNEngine
import net.rabbitknight.open.scanner.engine.zbar.ZBarEngine
import net.rabbitknight.open.scanner.engine.zxing.ZXingEngine
import java.io.File


class PickerActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_IMAGE_CODE = 0x20
        const val REQUEST_PERMISSION_CODE = 0x10
    }

    private val ioThread = HandlerThread("OpenScanner").also { it.start() }
    private val ioHandler by lazy { Handler(ioThread.looper) }
    private val mainHandler = Handler(Looper.getMainLooper())

    // Engine列表
    private val engines = arrayOf(
        WeChatEngine::class.java,
//        WeChatNCNNEngine::class.java,
        HWScanKitEngine::class.java,
        ZBarEngine::class.java,
        ZXingEngine::class.java,
        MLKitEngine::class.java
    )

    // scanner创建
    private val scanner: Scanner by lazy {
        val init = InitOption()
        OpenScanner.create(init, engines)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 权限申请
        checkOrRequestPermission()

        setContentView(R.layout.activity_picker)
        // 点击获取照片
        findViewById<View>(R.id.btn_image).setOnClickListener {
            requestSelectImage()
        }

        scanner.getResult { raw, result ->
            // 时间戳
            val timestamp = raw.timestamp
            // 是否成功检测
            val success = result.code == C.CODE_SUCCESS
            // 所有检测结果
            result.result.forEach {
                Log.w(
                    "PickerActivity",
                    "process ${timestamp}: format = ${it.format},box = ${it.rect}, content = ${it.payload} size = ${raw.width}x${raw.height}"
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // 释放
        scanner.release()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_IMAGE_CODE -> {
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this.applicationContext, "noting selected!", Toast.LENGTH_SHORT)
                        .show()
                    return
                }

                val result = PictureSelector.obtainSelectorList(data)
                if (result.isEmpty()) {
                    Toast.makeText(this.applicationContext, "noting selected!", Toast.LENGTH_SHORT)
                        .show()
                    return
                }
                val uri = result[0].path.let { Uri.parse(it) }
                processImage(uri)
            }
        }
    }

    private fun processImage(uri: Uri) {
        ioHandler.post {
            // 获取bitmap
            val bitmap = Glide.with(this).asBitmap().load(File(uri.path)).submit().get()
            // 封装bitmap
            val image = bitmap.wrap(System.currentTimeMillis()) {
//                it.recycle()
            }
            // scanner处理
            scanner.process(image)


            mainHandler.post {
                findViewById<ImageView>(R.id.iv_image).setImageBitmap(bitmap)
            }


        }
    }


    /**
     * request to system image picker
     */
    private fun requestSelectImage() {
        PictureSelector.create(this)
            .openSystemGallery(SelectMimeType.ofImage())
            .forSystemResultActivity(REQUEST_IMAGE_CODE)
    }

    private fun checkOrRequestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE, CAMERA, INTERNET),
            REQUEST_PERMISSION_CODE
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults.firstOrNull()) {
                return
            } else {
                Toast.makeText(this.applicationContext, "not has permission!", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }
}