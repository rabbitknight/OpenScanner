#include "yuvutils.h"
#include "YuvConvert.h"

extern "C"
JNIEXPORT void JNICALL
Java_net_rabbitknight_open_yuvutils_NativeLib_yuvCompress(JNIEnv *env, jclass jcls,
                                         jbyteArray nv21Src, jint width,
                                         jint height, jbyteArray i420Dst,
                                         jint dst_width, jint dst_height,
                                         jint mode, jint degree,
                                         jboolean isMirror) {

    jbyte *src_nv21_data = env->GetByteArrayElements(nv21Src, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    jbyte *tmp_dst_i420_data = NULL;

    // nv21转化为i420
    jbyte *i420_data = (jbyte *) malloc(sizeof(jbyte) * width * height * 3 / 2);
    NV21ToI420(src_nv21_data, width, height, i420_data);
    tmp_dst_i420_data = i420_data;

    // 镜像
    jbyte *i420_mirror_data = NULL;
    if (isMirror) {
        i420_mirror_data = (jbyte *) malloc(sizeof(jbyte) * width * height * 3 / 2);
        MirrorI420(tmp_dst_i420_data, width, height, i420_mirror_data);
        tmp_dst_i420_data = i420_mirror_data;
    }

    // 缩放
    jbyte *i420_scale_data = NULL;
    if (width != dst_width || height != dst_height) {
        i420_scale_data = (jbyte *) malloc(sizeof(jbyte) * width * height * 3 / 2);
        scaleI420(tmp_dst_i420_data, width, height, i420_scale_data, dst_width, dst_height, mode);
        tmp_dst_i420_data = i420_scale_data;
        width = dst_width;
        height = dst_height;
    }

    // 旋转
    jbyte *i420_rotate_data = NULL;
    if (degree == libyuv::kRotate90 || degree == libyuv::kRotate180 ||
        degree == libyuv::kRotate270) {
        i420_rotate_data = (jbyte *) malloc(sizeof(jbyte) * width * height * 3 / 2);
        rotateI420(tmp_dst_i420_data, width, height, i420_rotate_data, degree);
        tmp_dst_i420_data = i420_rotate_data;
    }

    // 同步数据
    // memcpy(dst_i420_data, tmp_dst_i420_data, sizeof(jbyte) * width * height * 3 / 2);
    jint len = env->GetArrayLength(i420Dst);
    memcpy(dst_i420_data, tmp_dst_i420_data, len);
    tmp_dst_i420_data = NULL;
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);

    // 释放
    if (i420_data != NULL) free(i420_data);
    if (i420_mirror_data != NULL) free(i420_mirror_data);
    if (i420_scale_data != NULL) free(i420_scale_data);
    if (i420_rotate_data != NULL) free(i420_rotate_data);
}


extern "C"
JNIEXPORT void JNICALL
Java_net_rabbitknight_open_yuvutils_NativeLib_yuvCropI420(JNIEnv *env, jclass jcls, jbyteArray src_, jint srcOffset,
                                         jint width,
                                         jint height, jbyteArray dst_, jint dstOffset,
                                         jint dst_width,
                                         jint dst_height,
                                         jint left, jint top) {
    //裁剪的区域大小不对
    if (left + dst_width > width || top + dst_height > height) {
        return;
    }
    //left和top必须为偶数，否则显示会有问题
    if (left % 2 != 0 || top % 2 != 0) {
        return;
    }
    jint src_length = env->GetArrayLength(src_);
    jbyte *src_i420_data = env->GetByteArrayElements(src_, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(dst_, NULL);
    CropI420(src_i420_data + srcOffset, src_length, width, height, dst_i420_data + dstOffset,
             dst_width,
             dst_height, left,
             top);
    env->ReleaseByteArrayElements(dst_, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_net_rabbitknight_open_yuvutils_NativeLib_yuvMirrorI420(JNIEnv *env, jclass jcls, jbyteArray i420Src,
                                           jint width, jint height, jbyteArray i420Dst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    // i420数据镜像
    MirrorI420(src_i420_data, width, height, dst_i420_data);
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_net_rabbitknight_open_yuvutils_NativeLib_yuvScaleI420(JNIEnv *env, jclass jcls, jbyteArray i420Src,
                                          jint width, jint height, jbyteArray i420Dst,
                                          jint dstWidth, jint dstHeight, jint mode) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    // i420数据缩放
    scaleI420(src_i420_data, width, height, dst_i420_data, dstWidth, dstHeight, mode);
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_net_rabbitknight_open_yuvutils_NativeLib_yuvRotateI420(JNIEnv *env, jclass jcls, jbyteArray i420Src,
                                           jint width, jint height, jbyteArray i420Dst,
                                           jint degree) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    // i420数据旋转
    rotateI420(src_i420_data, width, height, dst_i420_data, degree);
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_net_rabbitknight_open_yuvutils_NativeLib_yuvNV21ToI420(JNIEnv *env, jclass jcls, jbyteArray nv21Src,
                                           jint width, jint height, jbyteArray i420Dst) {
    jbyte *src_nv21_data = env->GetByteArrayElements(nv21Src, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    // nv21转化为i420
    NV21ToI420(src_nv21_data, width, height, dst_i420_data);
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420ToNV21(JNIEnv *env, jclass jcls, jbyteArray i420Src,
                                           jint width, jint height, jbyteArray nv21Dst) {

    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_nv21_data = env->GetByteArrayElements(nv21Dst, NULL);
    I420ToNv21(src_i420_data, width, height, dst_nv21_data);
    env->ReleaseByteArrayElements(nv21Dst, dst_nv21_data, 0);
}

extern "C"
JNIEXPORT void JNICALL
Java_net_rabbitknight_open_yuvutils_NativeLib_yuvNV21ToI420AndRotate(JNIEnv *env, jclass jcls, jbyteArray nv21Src,
                                                    jint width, jint height, jbyteArray i420Dst,
                                                    jint degree) {
    jbyte *src_nv21_data = env->GetByteArrayElements(nv21Src, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    switch (degree) {
        case libyuv::kRotate0:
            NV21ToI420(src_nv21_data, width, height, dst_i420_data);
            break;
        case libyuv::kRotate90:
            NV21ToI420AndRotateClockwise(src_nv21_data, width, height, dst_i420_data);
            break;
        case libyuv::kRotate180:
            NV21ToI420AndRotate180(src_nv21_data, width, height, dst_i420_data);
            break;
        case libyuv::kRotate270:
            NV21ToI420AndRotateAntiClockwise(src_nv21_data, width, height, dst_i420_data);
            break;
    }
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420ToRGB24
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height,
         jbyteArray rgb24Dst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_rgb24_data = env->GetByteArrayElements(rgb24Dst, NULL);
    I420ToRGB24(src_i420_data, width, height, dst_rgb24_data);
    env->ReleaseByteArrayElements(rgb24Dst, dst_rgb24_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420ToARGB
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height, jint dst_stride,
         jbyteArray argbDst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_argb_data = env->GetByteArrayElements(argbDst, NULL);
    I420ToARGB(src_i420_data, width, height, dst_stride, dst_argb_data);
    env->ReleaseByteArrayElements(argbDst, dst_argb_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420ToRGBAMac
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height, jint dst_stride,
         jbyteArray rgbaMacDst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_rgba_mac_data = env->GetByteArrayElements(rgbaMacDst, NULL);
    I420ToRGBAMac(src_i420_data, width, height, dst_stride, dst_rgba_mac_data);
    env->ReleaseByteArrayElements(rgbaMacDst, dst_rgba_mac_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420ToARGB4444
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height, jint dst_stride,
         jbyteArray argb4444Dst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_argb4444_data = env->GetByteArrayElements(argb4444Dst, NULL);
    I420ToARGB4444(src_i420_data, width, height, dst_stride, dst_argb4444_data);
    env->ReleaseByteArrayElements(argb4444Dst, dst_argb4444_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420ToRGB565
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height,
         jbyteArray rgb565Dst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_rgb565_data = env->GetByteArrayElements(rgb565Dst, NULL);
    I420ToRGB565(src_i420_data, width, height, dst_rgb565_data);
    env->ReleaseByteArrayElements(rgb565Dst, dst_rgb565_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420ToRGB565Android
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height,
         jbyteArray rgb565Dst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_rgb565_data = env->GetByteArrayElements(rgb565Dst, NULL);
    I420ToRGB565Android(src_i420_data, width, height, dst_rgb565_data);
    env->ReleaseByteArrayElements(rgb565Dst, dst_rgb565_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420ToARGB1555
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height, jint dst_stride,
         jbyteArray argb1555Dst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_argb1555_data = env->GetByteArrayElements(argb1555Dst, NULL);
    I420ToARGB1555(src_i420_data, width, height, dst_stride, dst_argb1555_data);
    env->ReleaseByteArrayElements(argb1555Dst, dst_argb1555_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420ToYUY2
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height, jint dst_stride,
         jbyteArray yuy2Dst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_yuy2_data = env->GetByteArrayElements(yuy2Dst, NULL);
    I420ToYUY2(src_i420_data, width, height, dst_stride, dst_yuy2_data);
    env->ReleaseByteArrayElements(yuy2Dst, dst_yuy2_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420ToUYVY
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height, jint dst_stride,
         jbyteArray uyvyDst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_uyvy_data = env->GetByteArrayElements(uyvyDst, NULL);
    I420ToUYVY(src_i420_data, width, height, dst_stride, dst_uyvy_data);
    env->ReleaseByteArrayElements(uyvyDst, dst_uyvy_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420ToYV12
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height, jint dst_stride,
         jbyteArray yv12Dst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_yv12_data = env->GetByteArrayElements(yv12Dst, NULL);
    I420ToYV12(src_i420_data, width, height, dst_stride, dst_yv12_data);
    env->ReleaseByteArrayElements(yv12Dst, dst_yv12_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvYV12ToI420
        (JNIEnv *env, jclass jcls, jbyteArray yv12Src, jint width, jint height,
         jbyteArray i420Dst) {
    jbyte *src_yv12_data = env->GetByteArrayElements(yv12Src, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    YV12ToI420(src_yv12_data, width, height, dst_i420_data);
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvNV12ToI420
        (JNIEnv *env, jclass jcls, jbyteArray nv12Src, jint width, jint height,
         jbyteArray i420Dst) {
    jbyte *src_nv12_data = env->GetByteArrayElements(nv12Src, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    NV12ToI420(src_nv12_data, width, height, dst_i420_data);
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420ToNv12
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height,
         jbyteArray nv12Dst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_nv12_data = env->GetByteArrayElements(nv12Dst, NULL);
    I420ToNv12(src_i420_data, width, height, dst_nv12_data);
    env->ReleaseByteArrayElements(nv12Dst, dst_nv12_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvNV12ToI420AndRotate
        (JNIEnv *env, jclass jcls, jbyteArray nv12Src, jint width, jint height, jbyteArray i420Dst,
         jint degree) {
    jbyte *src_nv12_data = env->GetByteArrayElements(nv12Src, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    switch (degree) {
        case libyuv::kRotate0:
            NV12ToI420(src_nv12_data, width, height, dst_i420_data);
            break;
        case libyuv::kRotate90:
            NV12ToI420AndRotateClockwise(src_nv12_data, width, height, dst_i420_data);
            break;
        case libyuv::kRotate180:
            NV12ToI420AndRotate180(src_nv12_data, width, height, dst_i420_data);
            break;
        case libyuv::kRotate270:
            NV12ToI420AndRotateAntiClockwise(src_nv12_data, width, height, dst_i420_data);
            break;
    }
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvNV12ToRGB565
        (JNIEnv *env, jclass jcls, jbyteArray nv12Src, jint width, jint height,
         jbyteArray rgb565Dst) {
    jbyte *src_nv12_data = env->GetByteArrayElements(nv12Src, NULL);
    jbyte *dst_rgb565_data = env->GetByteArrayElements(rgb565Dst, NULL);
    NV12ToRGB565(src_nv12_data, width, height, dst_rgb565_data);
    env->ReleaseByteArrayElements(rgb565Dst, dst_rgb565_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420ToRGBAIPhone
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height, jint dst_stride,
         jbyteArray rgbaDst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_rgba_data = env->GetByteArrayElements(rgbaDst, NULL);
    I420ToRGBAIPhone(src_i420_data, width, height, dst_stride, dst_rgba_data);
    env->ReleaseByteArrayElements(rgbaDst, dst_rgba_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420Copy
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height, jint dst_stride,
         jbyteArray i420Dst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    I420Copy(src_i420_data, width, height, dst_stride, dst_i420_data);
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvUYVYToI420
        (JNIEnv *env, jclass jcls, jbyteArray uyvySrc, jint width, jint height,
         jbyteArray i420Dst) {
    jbyte *src_uyvy_data = env->GetByteArrayElements(uyvySrc, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    UYVYToI420(src_uyvy_data, width, height, dst_i420_data);
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvYUY2ToI420
        (JNIEnv *env, jclass jcls, jbyteArray yuy2Src, jint width, jint height,
         jbyteArray i420Dst) {
    jbyte *src_yuy2_data = env->GetByteArrayElements(yuy2Src, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    YUY2ToI420(src_yuy2_data, width, height, dst_i420_data);
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvRGB24ToARGB
        (JNIEnv *env, jclass jcls, jbyteArray rgb24Src, jint width, jint height, jint dst_stride,
         jbyteArray argbDst) {
    jbyte *src_rgb24_data = env->GetByteArrayElements(rgb24Src, NULL);
    jbyte *dst_argb_data = env->GetByteArrayElements(argbDst, NULL);
    RGB24ToARGB(src_rgb24_data, width, height, dst_stride, dst_argb_data);
    env->ReleaseByteArrayElements(argbDst, dst_argb_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvRGB24ToI420
        (JNIEnv *env, jclass jcls, jbyteArray rgb24Src, jint width, jint height,
         jbyteArray i420Dst) {
    jbyte *src_rgb24_data = env->GetByteArrayElements(rgb24Src, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    RGB24ToI420(src_rgb24_data, width, height, dst_i420_data);
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420ToARGBMac
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height, jint dst_stride,
         jbyteArray argbMacDst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_argb_mac_data = env->GetByteArrayElements(argbMacDst, NULL);
    I420ToARGBMac(src_i420_data, width, height, dst_stride, dst_argb_mac_data);
    env->ReleaseByteArrayElements(argbMacDst, dst_argb_mac_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvARGBMacToI420
        (JNIEnv *env, jclass jcls, jbyteArray argbMacSrc, jint width, jint height,
         jbyteArray i420Dst) {
    jbyte *src_argb_mac_data = env->GetByteArrayElements(argbMacSrc, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    ARGBMacToI420(src_argb_mac_data, width, height, dst_i420_data);
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvMirrorI420LeftRight
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height,
         jbyteArray i420Dst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    MirrorI420LeftRight(src_i420_data, width, height, dst_i420_data);
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvMirrorI420UpDown
        (JNIEnv *env, jclass jcls, jbyteArray i420Src, jint width, jint height,
         jbyteArray i420Dst) {
    jbyte *src_i420_data = env->GetByteArrayElements(i420Src, NULL);
    jbyte *dst_i420_data = env->GetByteArrayElements(i420Dst, NULL);
    MirrorI420UpDown(src_i420_data, width, height, dst_i420_data);
    env->ReleaseByteArrayElements(i420Dst, dst_i420_data, 0);
}

extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvConvertFromI420
        (JNIEnv *env, jclass jcls, jbyteArray y, jint offsetY, jint strideY,
         jbyteArray u, jint offsetU, jint strideU,
         jbyteArray v, jint offsetV, jint strideV, jbyteArray dst, jint offsetDst, jint strideDst,
         jint width, jint height, jint format) {
    jbyte *src_y = env->GetByteArrayElements(y, NULL);
    jbyte *src_u = env->GetByteArrayElements(u, NULL);
    jbyte *src_v = env->GetByteArrayElements(v, NULL);
    jbyte *dst_dst = env->GetByteArrayElements(dst, NULL);

    libyuv::ConvertFromI420((uint8 *) src_y + offsetY, strideY, (uint8 *) src_u + offsetU, strideU,
                            (uint8 *) src_v + offsetV, strideV,
                            (uint8 *) dst_dst + offsetDst, strideDst, width, height,
                            static_cast<uint32>(format));

    env->ReleaseByteArrayElements(y, src_y, 0);
    env->ReleaseByteArrayElements(u, src_u, 0);
    env->ReleaseByteArrayElements(v, src_v, 0);
    env->ReleaseByteArrayElements(dst, dst_dst, 0);
}


extern "C"
JNIEXPORT void JNICALL Java_net_rabbitknight_open_yuvutils_NativeLib_yuvI420Blend
        (JNIEnv *env, jclass jcls, jbyteArray image1, jint offset1,
         jbyteArray image2, jint offset2,
         jbyteArray jalpha,
         jbyteArray out, jint offsetOut,
         jint with, jint height) {
    uint8_t *src1 = (uint8_t *) env->GetByteArrayElements(image1, NULL);
    uint8_t *src2 = (uint8_t *) env->GetByteArrayElements(image2, NULL);
    uint8_t *dst = (uint8_t *) env->GetByteArrayElements(out, NULL);
    uint8_t *alpha = (uint8_t *) env->GetByteArrayElements(jalpha, NULL);

    int uOffset = with * height;
    int vOffset = (int) (with * height * 1.25);

    libyuv::I420Blend(src1 + offset1, with,
                      src1 + uOffset + offset1, with / 2,
                      src1 + vOffset + offset1, with / 2,
                      src2 + offset2, with,
                      src2 + uOffset + offset2, with / 2,
                      src2 + vOffset + offset2, with / 2,
                      alpha, with,
                      dst + offsetOut, with,
                      dst + uOffset + offsetOut, with / 2,
                      dst + vOffset + offsetOut, with / 2,
                      with, height
    );

    env->ReleaseByteArrayElements(image1, (jbyte *) src1, 0);
    env->ReleaseByteArrayElements(image2, (jbyte *) src2, 0);
    env->ReleaseByteArrayElements(out, (jbyte *) dst, 0);
    env->ReleaseByteArrayElements(jalpha, (jbyte *) alpha, 0);
}

extern "C"
JNIEXPORT jint JNICALL
Java_net_rabbitknight_open_yuvutils_NativeLib_yuvAndroid420ToI420___3BII_3BII_3BIII_3BII_3BII_3BIIII(
        JNIEnv *env, jclass jcls,
        jbyteArray srcY, jint srcOffsetY, jint strideSrcY,
        jbyteArray srcU, jint srcOffsetU, jint strideSrcU,
        jbyteArray srcV, jint srcOffsetV, jint strideSrcV,
        jint pixelStrideUV,
        jbyteArray dstY, jint dstOffsetY, jint strideDstY,
        jbyteArray dstU, jint dstOffsetU, jint strideDstU,
        jbyteArray dstV, jint dstOffsetV, jint strideDstV,
        jint width, jint height
) {
    uint8_t *src_y = (uint8_t *) env->GetByteArrayElements(srcY, NULL);
    uint8_t *src_u = (uint8_t *) env->GetByteArrayElements(srcU, NULL);
    uint8_t *src_v = (uint8_t *) env->GetByteArrayElements(srcV, NULL);

    uint8_t *dst_y = (uint8_t *) env->GetByteArrayElements(dstY, NULL);
    uint8_t *dst_u = (uint8_t *) env->GetByteArrayElements(dstU, NULL);
    uint8_t *dst_v = (uint8_t *) env->GetByteArrayElements(dstV, NULL);


    int rst = libyuv::Android420ToI420(src_y + srcOffsetY, strideSrcY, src_u + srcOffsetU,
                                       strideSrcU,
                                       src_v + srcOffsetV, strideSrcV,
                                       pixelStrideUV,
                                       dst_y + dstOffsetY, strideDstY, dst_u + dstOffsetU,
                                       strideDstU,
                                       dst_v + dstOffsetV, strideDstV,
                                       width, height);

    env->ReleaseByteArrayElements(srcY, (jbyte *) src_y, 0);
    env->ReleaseByteArrayElements(srcU, (jbyte *) src_u, 0);
    env->ReleaseByteArrayElements(srcV, (jbyte *) src_v, 0);

    env->ReleaseByteArrayElements(dstY, (jbyte *) dst_y, 0);
    env->ReleaseByteArrayElements(dstU, (jbyte *) dst_u, 0);
    env->ReleaseByteArrayElements(dstV, (jbyte *) dst_v, 0);
    return rst;

}

extern "C"
JNIEXPORT jint JNICALL
Java_net_rabbitknight_open_yuvutils_NativeLib_yuvAndroid420ToI420__Ljava_nio_ByteBuffer_2ILjava_nio_ByteBuffer_2ILjava_nio_ByteBuffer_2II_3BII_3BII_3BIIII(
        JNIEnv *env, jclass jcls,
        jobject yuvPlanesY, jint strideSrcY,
        jobject yuvPlanesU, jint strideSrcU,
        jobject yuvPlanesV, jint strideSrcV,
        jint pixelStrideUV,
        jbyteArray dstY, jint dstOffsetY, jint strideDstY,
        jbyteArray dstU, jint dstOffsetU, jint strideDstU,
        jbyteArray dstV, jint dstOffsetV, jint strideDstV,
        jint width, jint height
) {
    uint8_t *src_y = (uint8_t *) env->GetDirectBufferAddress(yuvPlanesY);
    uint8_t *src_u = (uint8_t *) env->GetDirectBufferAddress(yuvPlanesU);
    uint8_t *src_v = (uint8_t *) env->GetDirectBufferAddress(yuvPlanesV);

    uint8_t *dst_y = (uint8_t *) env->GetByteArrayElements(dstY, NULL);
    uint8_t *dst_u = (uint8_t *) env->GetByteArrayElements(dstU, NULL);
    uint8_t *dst_v = (uint8_t *) env->GetByteArrayElements(dstV, NULL);


    int rst = libyuv::Android420ToI420(src_y, strideSrcY,
                                       src_u, strideSrcU,
                                       src_v, strideSrcV,
                                       pixelStrideUV,
                                       dst_y + dstOffsetY, strideDstY,
                                       dst_u + dstOffsetU, strideDstU,
                                       dst_v + dstOffsetV, strideDstV,
                                       width, height);

    env->ReleaseByteArrayElements(dstY, (jbyte *) dst_y, 0);
    env->ReleaseByteArrayElements(dstU, (jbyte *) dst_u, 0);
    env->ReleaseByteArrayElements(dstV, (jbyte *) dst_v, 0);
    return rst;
}