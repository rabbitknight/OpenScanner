package net.rabbitknight.open.media.data.image

import android.graphics.Bitmap
import android.media.Image
import net.rabbitknight.open.media.data.ImageWrapper
import net.rabbitknight.open.media.data.WrapperOwner
import net.rabbitknight.open.media.data.format.ImageFormat
import net.rabbitknight.open.media.data.pool.ByteBufferPool

interface ByteArrayOwner : WrapperOwner<ByteArray>
interface BitmapOwner : WrapperOwner<Bitmap>
interface Image2Owner : WrapperOwner<Image>

fun ByteArray.wrap(
    allocator: ByteBufferPool,
    @ImageFormat.Format format: String,
    width: Int,
    height: Int,
    timestamp: Long,
    release: (ByteArray) -> Unit,
): ImageWrapper<ByteArray> =
    this.wrap(allocator, format, width, height, timestamp, object : ByteArrayOwner {
        override fun close(payload: ByteArray) {
            release(payload)
        }
    })

fun ByteArray.wrap(
    allocator: ByteBufferPool,
    @ImageFormat.Format format: String,
    width: Int,
    height: Int,
    timestamp: Long,
    owner: WrapperOwner<ByteArray>,
): ImageWrapper<ByteArray> =
    ByteArrayImage(allocator, owner, this, format, width, height, timestamp)

fun Image.wrap(
    allocator: ByteBufferPool,
    release: (Image) -> Unit
): ImageWrapper<Image> = this.wrap(allocator, object : Image2Owner {
    override fun close(payload: Image) {
        release(payload)
    }
})

fun Image.wrap(
    allocator: ByteBufferPool,
    owner: WrapperOwner<Image>
): ImageWrapper<Image> =
    Camera2Image(allocator, owner, this)

fun Bitmap.wrap(
    allocator: ByteBufferPool,
    timestamp: Long,
    release: (Bitmap) -> Unit,
): ImageWrapper<Bitmap> = this.wrap(allocator, timestamp, object : BitmapOwner {
    override fun close(payload: Bitmap) {
        release(payload)
    }
})

fun Bitmap.wrap(
    allocator: ByteBufferPool,
    timestamp: Long,
    owner: WrapperOwner<Bitmap>,
): ImageWrapper<Bitmap> = BitmapImage(allocator, owner, this, timestamp)
