package net.rabbitknight.open.scanner.core.image

import android.graphics.Bitmap
import android.media.Image


fun ByteArray.wrap(format: Int, width: Int, height: Int, timestamp: Long): ImageWrapper =
    ByteArrayImage(this, format, width, height, timestamp)

fun Bitmap.wrap(): ImageWrapper {
    TODO("impl")
}

fun Image.wrap(): ImageWrapper = Camera2Image(this)

fun ImageWrapper.from(byteArray: ByteArray, format: Int, width: Int, height: Int) {

}

fun ImageWrapper.from(bitmap: Bitmap) {

}

