//
// Created by k on 2022/9/18.
//

#ifndef OPENSCANNER_WECHAT_HELPER_H
#define OPENSCANNER_WECHAT_HELPER_H

#include <stdio.h>
#include <sys/time.h>
#include "android/log.h"


#define TAG "WechatEngine" // 这个是自定义的LOG的标识
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,TAG ,__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN,TAG ,__VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR,TAG ,__VA_ARGS__)
#define LOGF(...) __android_log_print(ANDROID_LOG_FATAL,TAG ,__VA_ARGS__)


/**
 * 获取毫秒级的时间戳
 */
static long get_current_time_mils() {
    struct timeval tv;
    gettimeofday(&tv, NULL);
    return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}

#endif //OPENSCANNER_WECHAT_HELPER_H
