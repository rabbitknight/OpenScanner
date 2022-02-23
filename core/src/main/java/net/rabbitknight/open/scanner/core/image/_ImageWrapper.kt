package net.rabbitknight.open.scanner.core.image

import android.media.Image


fun ByteArray.wrap(format: Int, width: Int, height: Int, timestamp: Long): ImageWrapper =
    ByteArrayImage(this, format, width, height, timestamp)

fun Image.wrap(): ImageWrapper = Camera2Image(this)


