package net.rabbitknight.open.scanner.core.source

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import net.rabbitknight.open.scanner.core.ScannerException
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.image.wrap
import java.io.File

class FileSource(file: File) : Source {
    private var bitmap: Bitmap? = null
    private var proxy: ImageWrapper? = null

    init {
        val bitmap = BitmapFactory.decodeFile(file.absolutePath)
        this.bitmap = bitmap
        this.proxy = bitmap.wrap()
    }

    override fun available(): Int = if (bitmap == null) 0 else 1

    override fun take(): ImageWrapper {
        if (available() <= 0 || proxy == null) {
            throw ScannerException("cannot decode file!")
        }
        return proxy as ImageWrapper
    }

    override fun close() {
        bitmap?.recycle()
        proxy?.close()
    }
}