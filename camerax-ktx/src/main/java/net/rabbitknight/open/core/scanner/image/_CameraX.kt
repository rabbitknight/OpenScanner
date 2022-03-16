package net.rabbitknight.open.core.scanner.image

import androidx.camera.core.ImageProxy
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.image.WrapperOwner

fun ImageProxy.wrap(
    owner: WrapperOwner<ImageProxy>
): ImageWrapper<Any> =
    CameraXImage(owner, this)