package net.rabbitknight.open.scanner.core.engine


import android.os.FileUtils
import net.rabbitknight.open.scanner.core.ContextProvider
import java.io.File
import java.io.FileOutputStream


/**
 * 将assets加载到files目录
 */
object AssetsLoader {
    private val fileMap = mutableMapOf<String, File>()

    fun getFile(assetFilePath: String): File? = fileMap[assetFilePath]

    /**
     * 加载或者是已经copy过文件
     */
    fun loadOrExist(assetName: String, target: File, force: Boolean): Boolean {
        val assets = ContextProvider.context().assets
        val ins = assets.open(assetName)
        // if already exist
        if (target.exists() && !force) {
            fileMap[assetName] = target
            return true
        }
        // create new File
        if (!target.exists()) {
            if (target.parentFile?.exists() != true) {
                target.parentFile?.mkdir()
            }
            target.createNewFile()
        }
        var success = true
        ins.use {
            val ous = FileOutputStream(target, false)
            ous.use {
                ins.copyTo(ous)
                success = true
            }
        }
        fileMap[assetName] = target
        return success
    }
}