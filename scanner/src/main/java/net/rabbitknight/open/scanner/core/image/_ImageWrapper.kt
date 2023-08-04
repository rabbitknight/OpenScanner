package net.rabbitknight.open.scanner.core.image

import android.graphics.Bitmap
import android.media.Image

interface ByteArrayOwner : WrapperOwner<ByteArray>
interface BitmapOwner : WrapperOwner<Bitmap>
interface Image2Owner : WrapperOwner<Image>

fun ByteArray.wrap(
    @ImageFormat.Format format: String,
    width: Int,
    height: Int,
    timestamp: Long,
    owner: (ByteArray) -> Unit,
): ImageWrapper<ByteArray> = this.wrap(format, width, height, timestamp, object : ByteArrayOwner {
    override fun close(payload: ByteArray) {
        owner(payload)
    }
})

fun ByteArray.wrap(
    @ImageFormat.Format format: String,
    width: Int,
    height: Int,
    timestamp: Long,
    owner: WrapperOwner<ByteArray>,
): ImageWrapper<ByteArray> =
    ByteArrayImage(owner, this, format, width, height, timestamp)

fun Image.wrap(
    owner: (Image) -> Unit
): ImageWrapper<Image> = this.wrap(object : Image2Owner {
    override fun close(payload: Image) {
        owner(payload)
    }
})

fun Image.wrap(
    owner: WrapperOwner<Image>
): ImageWrapper<Image> =
    Camera2Image(owner, this)

fun Bitmap.wrap(
    timestamp: Long,
    owner: (Bitmap) -> Unit,
): ImageWrapper<Bitmap> = this.wrap(timestamp, object : BitmapOwner {
    override fun close(payload: Bitmap) {
        owner(payload)
    }
})

fun Bitmap.wrap(
    timestamp: Long,
    owner: WrapperOwner<Bitmap>,
): ImageWrapper<Bitmap> =
    BitmapImage(owner, this, timestamp)
