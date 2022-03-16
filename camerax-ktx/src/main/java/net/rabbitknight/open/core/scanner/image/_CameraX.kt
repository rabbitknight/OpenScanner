package net.rabbitknight.open.core.scanner.image

import androidx.camera.core.ImageProxy
import net.rabbitknight.open.scanner.core.image.ImageWrapper

fun ImageProxy.wrap(): ImageWrapper<ImageProxy> = CameraXImage(this)