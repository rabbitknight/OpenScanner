// This file is part of OpenCV project.
// It is subject to the license terms in the LICENSE file found in the top-level directory
// of this distribution and at http://opencv.org/license.html.
//
// Tencent is pleased to support the open source community by making WeChat QRCode available.
// Copyright (C) 2020 THL A29 Limited, a Tencent company. All rights reserved.
#include "wechat_engine.hpp"
#include <jni.h>
#include "wechat_helper.h"

using namespace cv::wechat_qrcode_ncnn;
using namespace std;

extern "C"
JNIEXPORT jlong JNICALL
Java_net_rabbitknight_open_scanner_engine_wechat_1ncnn_WeChatNCNNQRCode_init(JNIEnv *env, jobject thiz,
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
Java_net_rabbitknight_open_scanner_engine_wechat_1ncnn_WeChatNCNNQRCode_release(JNIEnv *env, jobject thiz,
                                                                      jlong peer) {
    if (0 != peer) {
        auto *qrcode = (WeChatEngine *) peer;
        delete qrcode;
    }
}

extern "C"
JNIEXPORT jintArray JNICALL
Java_net_rabbitknight_open_scanner_engine_wechat_1ncnn_WeChatNCNNQRCode_detect(JNIEnv *env, jobject thiz,
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
JNIEXPORT jobjectArray JNICALL
Java_net_rabbitknight_open_scanner_engine_wechat_1ncnn_WeChatNCNNQRCode_decode(JNIEnv *env, jobject thiz,
                                                                     jlong peer, jbyteArray image,
                                                                     jint width, jint height,
                                                                     jintArray candidate_points,
                                                                     jint candidate_size,
                                                                     jintArray res_points) {
    if (0 == peer) return nullptr;
    auto *qrcode = (WeChatEngine *) peer;
    auto *buffer = env->GetByteArrayElements(image, NULL);
    // cvt bytebuffer to Mat
    cv::Mat gray = cv::Mat(width, height, CV_8UC1, buffer);
    // cvt candidate_points
    auto *rect = env->GetIntArrayElements(candidate_points, NULL);
    auto points = vector<cv::Mat>();
    for (int i = 0; i < candidate_size; ++i) {
        auto point = cv::Mat(4, 2, CV_32FC1);
        auto left = (float) rect[i * 4 + 0];
        auto top = (float) rect[i * 4 + 1];
        auto right = (float) rect[i * 4 + 2];
        auto bottom = (float) rect[i * 4 + 3];
        point.at<float>(0, 0) = left;
        point.at<float>(0, 1) = top;
        point.at<float>(1, 0) = right;
        point.at<float>(1, 1) = top;
        point.at<float>(2, 0) = right;
        point.at<float>(2, 1) = bottom;
        point.at<float>(3, 0) = left;
        point.at<float>(3, 1) = bottom;
        points.push_back(point);
    }
    env->ReleaseIntArrayElements(candidate_points, rect, 0);
    // decode
    auto _res_points = vector<cv::Mat>();
    auto _res_texts = qrcode->decode(gray, points, _res_points);
    // release bytebuffer
    env->ReleaseByteArrayElements(image, buffer, 0);

    // cvt result
    auto size = _res_texts.size();
    if (_res_points.empty() || _res_texts.empty()) return nullptr;
    auto texts = env->NewObjectArray(size, env->FindClass("java/lang/String"),
                                     env->NewStringUTF(""));
    int *rects = new int[size];
    for (int i = 0; i < size; ++i) {
        // set text
        auto text = _res_texts[i];
        env->SetObjectArrayElement(texts, i, env->NewStringUTF(text.c_str()));
        // set point
        auto point = points[i];
        rects[i * 4 + 0] = (int) point.at<float>(0, 0); // left
        rects[i * 4 + 1] = (int) point.at<float>(0, 1); // top
        rects[i * 4 + 2] = (int) point.at<float>(2, 0); // right
        rects[i * 4 + 3] = (int) point.at<float>(2, 1); // bottom
    }
    env->SetIntArrayRegion(res_points, 0, size, rects);
    return texts;
}

extern "C"
JNIEXPORT jint JNICALL
Java_net_rabbitknight_open_scanner_engine_wechat_1ncnn_WeChatNCNNQRCode_detectAndDecode(JNIEnv *env,
                                                                              jobject thiz,
                                                                              jlong peer,
                                                                              jbyteArray image,
                                                                              jint width,
                                                                              jint height,
                                                                              jintArray res_points,
                                                                              jobjectArray res_texts) {
    if (0 == peer) return -1;
    auto *qrcode = (WeChatEngine *) peer;
    auto *buffer = env->GetByteArrayElements(image, NULL);
    // cvt bytebuffer to Mat
    cv::Mat gray = cv::Mat(width, height, CV_8UC1, buffer);
    auto _res_points = vector<cv::Mat>();

    // detect and decode
    auto _res_texts = qrcode->detectAndDecode(gray, _res_points);

    env->ReleaseByteArrayElements(image, buffer, NULL);

    int max_size = env->GetArrayLength(res_texts);
    auto text_size = _res_texts.size();
    // get max size
    auto size = max_size > text_size ? text_size : max_size;
    int *rects = new int[size];
    for (int i = 0; i < size; ++i) {
        // set text
        auto text = _res_texts[i];
        env->SetObjectArrayElement(res_texts, i, env->NewStringUTF(text.c_str()));
        // set point
        auto point = _res_points[i];
        rects[i * 4 + 0] = (int) point.at<float>(0, 0); // left
        rects[i * 4 + 1] = (int) point.at<float>(0, 1); // top
        rects[i * 4 + 2] = (int) point.at<float>(2, 0); // right
        rects[i * 4 + 3] = (int) point.at<float>(2, 1); // bottom
    }
    env->SetIntArrayRegion(res_points, 0, (jint) size, rects);

    return (jint) size;
}

extern "C"
JNIEXPORT jboolean JNICALL
Java_net_rabbitknight_open_scanner_engine_wechat_1ncnn_WeChatNCNNQRCode_setScaleFactor(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jlong peer,
                                                                             jfloat factor) {
    if (0 == peer) return false;
    auto *qrcode = (WeChatEngine *) peer;
    qrcode->setScaleFactor(factor);
    return true;
}

extern "C"
JNIEXPORT jfloat JNICALL
Java_net_rabbitknight_open_scanner_engine_wechat_1ncnn_WeChatNCNNQRCode_getScaleFactor(JNIEnv *env,
                                                                             jobject thiz,
                                                                             jlong peer) {
    if (0 == peer) return -1.0f;
    auto *qrcode = (WeChatEngine *) peer;
    return qrcode->getScaleFactor();
}
