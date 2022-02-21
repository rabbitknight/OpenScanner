package net.rabbitknight.open.scanner.core.image

import android.graphics.Bitmap
import android.media.Image


fun ByteArray.toImageProxy(format: Int, width: Int, height: Int): ImageProxy {
    TODO("impl")
}

fun Bitmap.toImageProxy(): ImageProxy {
    TODO("impl")
}

fun Image.toImageProxy(): ImageProxy {
    TODO("impl")
}

fun ImageProxy.from(byteArray: ByteArray, format: Int, width: Int, height: Int) {

}

fun ImageProxy.from(bitmap: Bitmap){

}

