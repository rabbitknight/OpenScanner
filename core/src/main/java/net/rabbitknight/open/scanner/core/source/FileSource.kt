package net.rabbitknight.open.scanner.core.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import net.rabbitknight.open.scanner.core.ScannerException
import net.rabbitknight.open.scanner.core.image.ImageProxy
import net.rabbitknight.open.scanner.core.image.toImageProxy
import java.io.File

class FileSource(file: File) : Source {
    private var bitmap: Bitmap? = null
    private var proxy: ImageProxy? = null

    init {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        this.bitmap = bitmap
        this.proxy = bitmap.toImageProxy()
    }

    override fun available(): Int = if (bitmap == null) 0 else 1

    override fun take(): ImageProxy {
        if (available() <= 0 || proxy == null) {
            throw ScannerException("cannot decode file!")
        }
        return proxy as ImageProxy
    }

    override fun close() {
        bitmap?.recycle()
        proxy?.close()
    }
}