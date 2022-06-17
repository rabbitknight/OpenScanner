package net.rabbitknight.open.scanner.core.config

import android.util.Size
import androidx.annotation.FloatRange
import net.rabbitknight.open.scanner.core.C
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
     * 多引擎模式
     */
    val multimode: Int = C.ENGINE_MUTIMODE_ALL,

    /**
     * 检测模块最小支持的框
     */
    val minRoiSize: Size = Size(C.DEFAULT_COORDINATOR_ROI_SIZE, C.DEFAULT_COORDINATOR_ROI_SIZE),
) {
}