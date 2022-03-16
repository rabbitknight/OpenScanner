package net.rabbitknight.open.scanner.core.image

import android.graphics.Bitmap
import android.media.Image


fun ByteArray.wrap(
    owner: WrapperOwner<ByteArray>,
    @ImageFormat.Format format: String,
    width: Int,
    height: Int,
    timestamp: Long
): ImageWrapper<Any> =
    ByteArrayImage(owner, this, format, width, height, timestamp)

fun Image.wrap(
    owner: WrapperOwner<Image>
): ImageWrapper<Any> =
    Camera2Image(owner, this)

fun Bitmap.wrap(
    owner: WrapperOwner<Bitmap>,
    timestamp: Long
): ImageWrapper<Any> =
    BitmapImage(owner, this, timestamp)
