package net.rabbitknight.open.scanner.core.config

import android.util.Size
import androidx.annotation.FloatRange
import net.rabbitknight.open.scanner.core.C
import net.rabbitknight.open.scanner.core.C.DEFAULT_INPUT_CAPACITY
import net.rabbitknight.open.scanner.core.C.DEFAULT_SHAKE_DETECTOR_ENABLE
import net.rabbitknight.open.scanner.core.result.RectF

data class Config(
    /**
     * 取景器位置
     */
    val finderRect: RectF = RectF(0f, 0f, 1.0f, 1.0f),
    /**
     * 取景器扩大倍数
     */
    @FloatRange(from = 0.0, to = 1.0)
    val finderTolerance: Float = 0.0f,

    /**
     * 输入缓存大小
     */
    val inputCapacity: Int = DEFAULT_INPUT_CAPACITY,

    /**
     * 多引擎模式
     */
    val multimode: Int = C.ENGINE_MUTIMODE_ALL,

    /**
     * 是否开启相机协作
     *
     */
    val enableCameraCoordinator: Boolean = false,

    val minRoiSize: Size = Size(C.DEFAULT_COORDINATOR_ROI_SIZE, C.DEFAULT_COORDINATOR_ROI_SIZE)
)