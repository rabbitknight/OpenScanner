package net.rabbitknight.open.scanner.engine.wechat

import android.content.Context
import java.io.File
import java.io.FileOutputStream


/**
 * 将assets加载到files目录
 */
object AssetsLoader {
    private val fileMap = mutableMapOf<String, File>()

    fun getFile(assetFilePath: String): File? = fileMap[assetFilePath]

    fun load(context: Context, assetPath: String, finished: (() -> Unit)? = null) {
        val assets = context.assets
        val outputDir = context.filesDir
        val sources = assets.list(assetPath)
        sources?.forEach {
            val out = File(outputDir, it)
            if (!out.exists()) {
                val ous = FileOutputStream(out)
                val ins = assets.open("$assetPath/$it")
                ins.use {
                    ous.use {
                        ins.copyTo(ous)
                    }
                }
            }
            fileMap[it] = out
        }
        finished?.invoke()
    }
}