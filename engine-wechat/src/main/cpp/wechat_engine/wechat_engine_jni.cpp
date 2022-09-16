// This file is part of OpenCV project.
// It is subject to the license terms in the LICENSE file found in the top-level directory
// of this distribution and at http://opencv.org/license.html.
//
// Tencent is pleased to support the open source community by making WeChat QRCode available.
// Copyright (C) 2020 THL A29 Limited, a Tencent company. All rights reserved.
#include "wechat_engine.hpp"
#include <jni.h>

using namespace cv::wechat_qrcode;
using namespace std;

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

extern "C"
JNIEXPORT jintArray JNICALL
Java_net_rabbitknight_open_scanner_engine_wechat_WeChatQRCode_detect(JNIEnv *env, jobject thiz,
                                                                     jlong peer, jbyteArray image,
                                                                     jint width, jint height) {
    if (0 == peer) {
        return nullptr;
    }
    auto *qrcode = (WeChatEngine *) peer;
    auto *buffer = env->GetByteArrayElements(image, NULL);
    // cvt bytebuffer to Mat
    cv::Mat gray = cv::Mat(width, height, CV_8UC1, buffer);
    // decode
    auto points = qrcode->detect(gray);
    // release bytebuffer
    env->ReleaseByteArrayElements(image, buffer, 0);

    // cvt result
    int size = (jsize) points.size() * 4;
    jintArray result = env->NewIntArray(size);
    int *rects = new int[size];
    for (int i = 0; i < points.size(); ++i) {
        auto point = points[i];
        rects[i * 4 + 0] = (int) point.at<float>(0, 0); // left
        rects[i * 4 + 1] = (int) point.at<float>(0, 1); // top
        rects[i * 4 + 2] = (int) point.at<float>(2, 0); // right
        rects[i * 4 + 3] = (int) point.at<float>(2, 1); // bottom
    }
    env->SetIntArrayRegion(result, 0, size, rects);
    return result;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_net_rabbitknight_open_scanner_engine_wechat_WeChatQRCode_decode(JNIEnv *env, jobject thiz,
                                                                     jlong peer, jbyteArray image,
                                                                     jint width, jint height,
                                                                     jintArray candidate_point) {
    if (0 == peer) return nullptr;
    auto *qrcode = (WeChatEngine *) peer;
    auto *buffer = env->GetByteArrayElements(image, NULL);
    // cvt bytebuffer to Mat
    cv::Mat gray = cv::Mat(width, height, CV_8UC1, buffer);
    // cvt candidate_points
    auto *rect = env->GetIntArrayElements(candidate_point, NULL);
    auto point = cv::Mat(4, 2, CV_32FC1);
    int left = rect[0];
    int top = rect[1];
    int right = rect[2];
    int bottom = rect[3];
    point.at<float>(0, 0) = left;
    point.at<float>(0, 1) = top;
    point.at<float>(1, 0) = right;
    point.at<float>(1, 1) = top;
    point.at<float>(2, 0) = right;
    point.at<float>(2, 1) = bottom;
    point.at<float>(3, 0) = left;
    point.at<float>(3, 1) = bottom;
    env->ReleaseIntArrayElements(candidate_point, rect, 0);
    auto points = vector<cv::Mat>();
    points.push_back(point);
    // decode
    auto res_points = vector<cv::Mat>();
    auto res_texts = qrcode->decode(gray, points, res_points);
    // release bytebuffer
    env->ReleaseByteArrayElements(image, buffer, 0);

    // cvt result
    if (res_points.empty() || res_texts.empty()) return nullptr;
    auto text = res_texts[0];
    return env->NewStringUTF(text.c_str());
}
