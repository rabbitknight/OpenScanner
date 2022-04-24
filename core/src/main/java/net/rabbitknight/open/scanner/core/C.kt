package net.rabbitknight.open.scanner.core

object C {
    const val CODE_SUCCESS = 0
    const val CODE_FAIL = -1

    /**
     * 调度频率 每16ms执行一次检测
     */
    const val SCHEDULE_PERIOD_MILS = 16L

    /**
     * 默认输入缓存大小
     */
    const val DEFAULT_INPUT_CAPACITY = 3

    /**
     * 默认晃动检测
     */
    const val DEFAULT_SHAKE_DETECTOR_ENABLE = false


    /**
     * 引擎检测模式：所有引擎都将参与解码 结果会比较后输出
     */
    const val ENGINE_MUTIMODE_ALL = 0x1

    /**
     * 引擎检测模式: 一旦有一个engine检测出结果 其他engine就不会再继续执行
     */
    const val ENGINE_MUTIMODE_ONCE = 0x2
}