package net.rabbitknight.open.media.ext.camerax

import androidx.camera.core.ImageProxy
import net.rabbitknight.open.media.data.ImageWrapper
import net.rabbitknight.open.media.data.WrapperOwner
import net.rabbitknight.open.media.data.pool.ByteBufferPool


interface CameraXOwner : WrapperOwner<ImageProxy>

fun ImageProxy.wrap(
    allocator: ByteBufferPool,
    release: (ImageProxy) -> Unit
): ImageWrapper<ImageProxy> = this.wrap(allocator, object : CameraXOwner {
    override fun close(payload: ImageProxy) {
        release(payload)
    }
})

fun ImageProxy.wrap(
    allocator: ByteBufferPool,
    owner: WrapperOwner<ImageProxy>
): ImageWrapper<ImageProxy> =
    CameraXImage(allocator, owner, this)