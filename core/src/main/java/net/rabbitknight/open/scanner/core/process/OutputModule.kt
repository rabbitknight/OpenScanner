package net.rabbitknight.open.scanner.core.process

import android.os.Handler
import net.rabbitknight.open.scanner.core.C
import net.rabbitknight.open.scanner.core.config.Config
import net.rabbitknight.open.scanner.core.image.ImageWrapper
import net.rabbitknight.open.scanner.core.process.base.BaseModule
import net.rabbitknight.open.scanner.core.result.BarcodeResult
import net.rabbitknight.open.scanner.core.result.ImageResult
import net.rabbitknight.open.scanner.core.result.Rect
import net.rabbitknight.open.scanner.core.result.centerX
import net.rabbitknight.open.scanner.core.result.centerY
import net.rabbitknight.open.scanner.core.result.height
import net.rabbitknight.open.scanner.core.result.width
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 后处理
 * 1. 检测结果是否复合
 */
class OutputModule() : BaseModule() {
    companion object {
        private const val TAG = "OutputModule"
    }

    private var resultListener: Pair<Handler, (ImageWrapper<Any>, ImageResult) -> Unit>? = null

    fun getOutput(handler: Handler, callback: (ImageWrapper<Any>, ImageResult) -> Unit) {
        resultListener = Pair(handler, callback)
    }

    override fun moduleName(): String = TAG

    override fun onConfig(config: Config) {
    }

    override fun onProcess(frame: ImageFrame) {
        // 不同engine检测结果去重
        val mergeResults = mutableSetOf<BarcodeResult>()

        val allResults = mutableListOf<BarcodeResult>()

        frame.result.forEach {
            allResults.addAll(it.result)
        }

        allResults.forEach { result ->
            // 找到相同的两个结果 (可能是不同引擎输出的
            val match = mergeResults.find {
                it.format == result.format  // 格式相同
                    && it.payload == result.payload // 结果相同
                    && cross(it.rect, result.rect)  // roi框相交
            }
            // 如果可以找到 则合并结果
            val merge = match?.let {
                val merge = merge(it.rect, result.rect)
                val replace = BarcodeResult(
                    result.format, merge, result.payload, result.rawBytes
                )
                mergeResults.remove(match)
                replace
            } ?: result
            mergeResults.add(merge)
        }

        // crop的坐标 转化为 图像的坐标
        val results = mergeResults.map {
            val left = it.rect.left + frame.cropRect.left
            val top = it.rect.top + frame.cropRect.top
            val right = it.rect.right + frame.cropRect.left
            val bottom = it.rect.bottom + frame.cropRect.top
            val rect = Rect(left, top, right, bottom)
            BarcodeResult(it.format, rect, it.payload, it.rawBytes)
        }

        // 结果
        val code = if (results.isNotEmpty()) {
            C.CODE_SUCCESS
        } else {
            // TODO: code 高优先级
            frame.result.first().code
        }

        // 结果输出
        val imageResult = ImageResult(
            code, frame.timestamp, results, ""
        )
        val rawImage = frame.raw
        // 帧回调通知
        resultListener?.let {
            val handler = it.first
            val listener = it.second
            handler.post {
                listener.invoke(rawImage, imageResult)
            }
        }

        // frame通知
        frame.frameListener.get()?.let {
            it.invoke(rawImage, imageResult)
        }
        // 内存回收
        frame.let {
            // 剪裁后的图像回收
            it.cropImage.close()
            // 原始图像回收
            it.cvtImage.values.forEach {
                it.close()
            }
        }
        // todo 焦距
    }

    /**
     * 判断两个矩形是否相交
     */
    internal fun cross(a: Rect, b: Rect): Boolean {
        val aCenterX = a.centerX()
        val aCenterY = a.centerY()
        val bCenterX = b.centerX()
        val bCenterY = b.centerY()
        return (abs(aCenterX - bCenterX) <= (a.width() + b.width()) / 2.0f)
            && (abs(aCenterY - bCenterY) <= (a.height() + b.height()) / 2.0f)
    }

    /**
     * 合并两个矩形
     */
    internal fun merge(a: Rect, b: Rect): Rect {
        val left = min(a.left, b.left)
        val top = min(a.top, b.top)
        val right = max(a.right, b.right)
        val bottom = max(a.bottom, b.bottom)
        return Rect(left, top, right, bottom)
    }
}