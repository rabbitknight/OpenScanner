// This file is part of OpenCV project.
// It is subject to the license terms in the LICENSE file found in the top-level directory
// of this distribution and at http://opencv.org/license.html.
//
// Tencent is pleased to support the open source community by making WeChat QRCode available.
// Copyright (C) 2020 THL A29 Limited, a Tencent company. All rights reserved.
#include "wechat_engine.hpp"
#include <jni.h>

using namespace cv::wechat_engine;

extern "C"
JNIEXPORT jlong JNICALL
Java_net_rabbitknight_open_scanner_engine_wechat_WeChatQRCode_init(JNIEnv *env, jobject thiz,
                                                                   jstring detector_proto,
                                                                   jstring detector_caffe,
                                                                   jstring sr_proto,
                                                                   jstring sr_caffe) {
    auto detector_prototxt_path = env->GetStringUTFChars(detector_proto, NULL);
    auto detector_caffe_model_path = env->GetStringUTFChars(detector_caffe, NULL);
    auto super_resolution_prototxt_path = env->GetStringUTFChars(sr_proto, NULL);
    auto super_resolution_caffe_model_path = env->GetStringUTFChars(sr_caffe, NULL);

    auto *qrcode = new WeChatEngine(std::string(detector_prototxt_path),
                                    std::string(detector_caffe_model_path),
                                    std::string(super_resolution_prototxt_path),
                                    std::string(super_resolution_caffe_model_path)
    );
    if (!qrcode) {
        return 0;
    }
    return ((jlong) qrcode);
}

extern "C"
JNIEXPORT void JNICALL
Java_net_rabbitknight_open_scanner_engine_wechat_WeChatQRCode_release(JNIEnv *env, jobject thiz,
                                                                      jlong peer) {
    if (0 != peer) {
        auto *qrcode = (WeChatEngine *) peer;
        delete qrcode;
    }
}