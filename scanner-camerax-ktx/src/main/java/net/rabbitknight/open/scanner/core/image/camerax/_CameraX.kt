package net.rabbitknight.open.scanner.core.image.camerax

import androidx.camera.core.ImageProxy
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.image.WrapperOwner

interface CameraXOwner : WrapperOwner<ImageProxy>

fun ImageProxy.wrap(
    owner: (ImageProxy) -> Unit
): ImageWrapper<ImageProxy> = this.wrap(object : CameraXOwner {
    override fun close(payload: ImageProxy) {
        owner(payload)
    }
})

fun ImageProxy.wrap(
    owner: WrapperOwner<ImageProxy>
): ImageWrapper<ImageProxy> =
    CameraXImage(owner, this)