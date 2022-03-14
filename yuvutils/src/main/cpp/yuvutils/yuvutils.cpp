#include <jni.h>
#include <libyuv/convert.h>
#include "libyuv.h"

extern "C"
JNIEXPORT jint JNICALL
Java_net_rabbitknight_open_yuvutils_YuvUtils_yuvMirrorI420(
        JNIEnv *env, jclass clazz,
        jbyteArray _src_y, jint src_offset_y, jint src_stride_y,
        jbyteArray _src_u, jint src_offset_u, jint src_stride_u,
        jbyteArray _src_v, jint src_offset_v, jint src_stride_v,
        jbyteArray _dst_y, jint dst_offset_y, jint dst_stride_y,
        jbyteArray _dst_u, jint dst_offset_u, jint dst_stride_u,
        jbyteArray _dst_v, jint dst_offset_v, jint dst_stride_v,
        jint width, jint height) {
    auto *src_y = (uint8_t *) env->GetByteArrayElements(_src_y, NULL);
    auto *src_u = (uint8_t *) env->GetByteArrayElements(_src_u, NULL);
    auto *src_v = (uint8_t *) env->GetByteArrayElements(_src_v, NULL);

    auto *dst_y = (uint8_t *) env->GetByteArrayElements(_dst_y, NULL);
    auto *dst_u = (uint8_t *) env->GetByteArrayElements(_dst_u, NULL);
    auto *dst_v = (uint8_t *) env->GetByteArrayElements(_dst_v, NULL);


    int rst = libyuv::I420Mirror(src_y + src_offset_y, src_stride_y,
                                 src_u + src_offset_u, src_stride_u,
                                 src_v + src_offset_v, src_stride_v,
                                 dst_y + dst_offset_y, dst_stride_y,
                                 dst_u + dst_offset_u, dst_stride_u,
                                 dst_v + dst_offset_v, dst_stride_v,
                                 width, height);

    env->ReleaseByteArrayElements(_src_y, (jbyte *) src_y, 0);
    env->ReleaseByteArrayElements(_src_u, (jbyte *) src_u, 0);
    env->ReleaseByteArrayElements(_src_v, (jbyte *) src_v, 0);

    env->ReleaseByteArrayElements(_dst_y, (jbyte *) dst_y, 0);
    env->ReleaseByteArrayElements(_dst_u, (jbyte *) dst_u, 0);
    env->ReleaseByteArrayElements(_dst_v, (jbyte *) dst_v, 0);

    return rst;
}


extern "C"
JNIEXPORT jint JNICALL
Java_net_rabbitknight_open_yuvutils_YuvUtils_yuvScaleI420(
        JNIEnv *env, jclass clazz,
        jbyteArray _src_y, jint src_offset_y, jint src_stride_y,
        jbyteArray _src_u, jint src_offset_u, jint src_stride_u,
        jbyteArray _src_v, jint src_offset_v, jint src_stride_v,
        jint src_width, jint src_height,
        jbyteArray _dst_y, jint dst_offset_y, jint dst_stride_y,
        jbyteArray _dst_u, jint dst_offset_u, jint dst_stride_u,
        jbyteArray _dst_v, jint dst_offset_v, jint dst_stride_v,
        jint dst_width, jint dst_height,
        jint filter_mode) {
    auto *src_y = (uint8_t *) env->GetByteArrayElements(_src_y, NULL);
    auto *src_u = (uint8_t *) env->GetByteArrayElements(_src_u, NULL);
    auto *src_v = (uint8_t *) env->GetByteArrayElements(_src_v, NULL);

    auto *dst_y = (uint8_t *) env->GetByteArrayElements(_dst_y, NULL);
    auto *dst_u = (uint8_t *) env->GetByteArrayElements(_dst_u, NULL);
    auto *dst_v = (uint8_t *) env->GetByteArrayElements(_dst_v, NULL);

    auto filterMode = static_cast<libyuv::FilterMode>(filter_mode);

    int rst = libyuv::I420Scale(src_y + src_offset_y, src_stride_y,
                                src_u + src_offset_u, src_stride_u,
                                src_v + src_offset_v, src_stride_v,
                                src_width, src_height,
                                dst_y + dst_offset_y, dst_stride_y,
                                dst_u + dst_offset_u, dst_stride_u,
                                dst_v + dst_offset_v, dst_stride_v,
                                dst_width, dst_height, filterMode);

    env->ReleaseByteArrayElements(_src_y, (jbyte *) src_y, 0);
    env->ReleaseByteArrayElements(_src_u, (jbyte *) src_u, 0);
    env->ReleaseByteArrayElements(_src_v, (jbyte *) src_v, 0);

    env->ReleaseByteArrayElements(_dst_y, (jbyte *) dst_y, 0);
    env->ReleaseByteArrayElements(_dst_u, (jbyte *) dst_u, 0);
    env->ReleaseByteArrayElements(_dst_v, (jbyte *) dst_v, 0);
    return rst;
}
extern "C"
JNIEXPORT jint JNICALL
Java_net_rabbitknight_open_yuvutils_YuvUtils_yuvRotateI420(
        JNIEnv *env, jclass clazz,
        jbyteArray _src_y, jint src_offset_y, jint src_stride_y,
        jbyteArray _src_u, jint src_offset_u, jint src_stride_u,
        jbyteArray _src_v, jint src_offset_v, jint src_stride_v,
        jint src_width, jint src_height,
        jbyteArray _dst_y, jint dst_offset_y, jint dst_stride_y,
        jbyteArray _dst_u, jint dst_offset_u, jint dst_stride_u,
        jbyteArray _dst_v, jint dst_offset_v, jint dst_stride_v,
        jint rotate_mode) {
    auto *src_y = (uint8_t *) env->GetByteArrayElements(_src_y, NULL);
    auto *src_u = (uint8_t *) env->GetByteArrayElements(_src_u, NULL);
    auto *src_v = (uint8_t *) env->GetByteArrayElements(_src_v, NULL);

    auto *dst_y = (uint8_t *) env->GetByteArrayElements(_dst_y, NULL);
    auto *dst_u = (uint8_t *) env->GetByteArrayElements(_dst_u, NULL);
    auto *dst_v = (uint8_t *) env->GetByteArrayElements(_dst_v, NULL);

    auto rotate = static_cast<libyuv::RotationMode>(rotate_mode);

    int rst = libyuv::I420Rotate(src_y + src_offset_y, src_stride_y,
                                 src_u + src_offset_u, src_stride_u,
                                 src_v + src_offset_v, src_stride_v,
                                 dst_y + dst_offset_y, dst_stride_y,
                                 dst_u + dst_offset_u, dst_stride_u,
                                 dst_v + dst_offset_v, dst_stride_v,
                                 src_width, src_height, rotate);

    env->ReleaseByteArrayElements(_src_y, (jbyte *) src_y, 0);
    env->ReleaseByteArrayElements(_src_u, (jbyte *) src_u, 0);
    env->ReleaseByteArrayElements(_src_v, (jbyte *) src_v, 0);

    env->ReleaseByteArrayElements(_dst_y, (jbyte *) dst_y, 0);
    env->ReleaseByteArrayElements(_dst_u, (jbyte *) dst_u, 0);
    env->ReleaseByteArrayElements(_dst_v, (jbyte *) dst_v, 0);
    return rst;
}

extern "C"
JNIEXPORT jint JNICALL
Java_net_rabbitknight_open_yuvutils_YuvUtils_yuvI420ToNV21(
        JNIEnv *env, jclass clazz,
        jbyteArray _src_y, jint src_offset_y, jint src_stride_y,
        jbyteArray _src_u, jint src_offset_u, jint src_stride_u,
        jbyteArray _src_v, jint src_offset_v, jint src_stride_v,
        jbyteArray _dst_y, jint dst_offset_y, jint dst_stride_y,
        jbyteArray _dst_vu, jint dst_offset_vu, jint dst_stride_vu,
        jint width, jint height) {
    auto *src_y = (uint8_t *) env->GetByteArrayElements(_src_y, NULL);
    auto *src_u = (uint8_t *) env->GetByteArrayElements(_src_u, NULL);
    auto *src_v = (uint8_t *) env->GetByteArrayElements(_src_v, NULL);

    auto *dst_y = (uint8_t *) env->GetByteArrayElements(_dst_y, NULL);
    auto *dst_vu = (uint8_t *) env->GetByteArrayElements(_dst_vu, NULL);

    int rst = libyuv::I420ToNV21(src_y + src_offset_y, src_stride_y,
                                 src_u + src_offset_u, src_stride_u,
                                 src_v + src_offset_v, src_stride_v,
                                 dst_y + dst_offset_y, dst_stride_y,
                                 dst_vu + dst_offset_vu, dst_stride_vu,
                                 width, height);

    env->ReleaseByteArrayElements(_src_y, (jbyte *) src_y, 0);
    env->ReleaseByteArrayElements(_src_u, (jbyte *) src_u, 0);
    env->ReleaseByteArrayElements(_src_v, (jbyte *) src_v, 0);

    env->ReleaseByteArrayElements(_dst_y, (jbyte *) dst_y, 0);
    env->ReleaseByteArrayElements(_dst_vu, (jbyte *) dst_vu, 0);
    return rst;
}
extern "C"
JNIEXPORT jint JNICALL
Java_net_rabbitknight_open_yuvutils_YuvUtils_yuvNV21ToI420(
        JNIEnv *env, jclass clazz,
        jbyteArray _src_y, jint src_offset_y, jint src_stride_y,
        jbyteArray _src_vu, jint src_offset_vu, jint src_stride_vu,
        jbyteArray _dst_y, jint dst_offset_y, jint dst_stride_y,
        jbyteArray _dst_u, jint dst_offset_u, jint dst_stride_u,
        jbyteArray _dst_v, jint dst_offset_v, jint dst_stride_v,
        jint width, jint height) {
    auto *src_y = (uint8_t *) env->GetByteArrayElements(_src_y, NULL);
    auto *src_vu = (uint8_t *) env->GetByteArrayElements(_src_vu, NULL);

    auto *dst_y = (uint8_t *) env->GetByteArrayElements(_dst_y, NULL);
    auto *dst_u = (uint8_t *) env->GetByteArrayElements(_dst_u, NULL);
    auto *dst_v = (uint8_t *) env->GetByteArrayElements(_dst_v, NULL);


    int rst = libyuv::NV21ToI420(src_y + src_offset_y, src_stride_y,
                                 src_vu + src_offset_vu, src_stride_vu,
                                 dst_y + dst_offset_y, dst_stride_y,
                                 dst_u + dst_offset_u, dst_stride_u,
                                 dst_v + dst_offset_v, dst_stride_v,
                                 width, height);

    env->ReleaseByteArrayElements(_src_y, (jbyte *) src_y, 0);
    env->ReleaseByteArrayElements(_src_vu, (jbyte *) src_vu, 0);

    env->ReleaseByteArrayElements(_dst_y, (jbyte *) dst_y, 0);
    env->ReleaseByteArrayElements(_dst_u, (jbyte *) dst_u, 0);
    env->ReleaseByteArrayElements(_dst_v, (jbyte *) dst_v, 0);
    return rst;
}

extern "C"
JNIEXPORT jint JNICALL
Java_net_rabbitknight_open_yuvutils_YuvUtils_yuvAndroid420ToI420___3BII_3BII_3BIII_3BII_3BII_3BIIII(
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
    auto *src_y = (uint8_t *) env->GetByteArrayElements(srcY, NULL);
    auto *src_u = (uint8_t *) env->GetByteArrayElements(srcU, NULL);
    auto *src_v = (uint8_t *) env->GetByteArrayElements(srcV, NULL);

    auto *dst_y = (uint8_t *) env->GetByteArrayElements(dstY, NULL);
    auto *dst_u = (uint8_t *) env->GetByteArrayElements(dstU, NULL);
    auto *dst_v = (uint8_t *) env->GetByteArrayElements(dstV, NULL);


    int rst = libyuv::Android420ToI420(src_y + srcOffsetY, strideSrcY,
                                       src_u + srcOffsetU, strideSrcU,
                                       src_v + srcOffsetV, strideSrcV,
                                       pixelStrideUV,
                                       dst_y + dstOffsetY, strideDstY,
                                       dst_u + dstOffsetU, strideDstU,
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
Java_net_rabbitknight_open_yuvutils_YuvUtils_yuvAndroid420ToI420__Ljava_nio_ByteBuffer_2ILjava_nio_ByteBuffer_2ILjava_nio_ByteBuffer_2II_3BII_3BII_3BIIII(
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
    auto *src_y = (uint8_t *) env->GetDirectBufferAddress(yuvPlanesY);
    auto *src_u = (uint8_t *) env->GetDirectBufferAddress(yuvPlanesU);
    auto *src_v = (uint8_t *) env->GetDirectBufferAddress(yuvPlanesV);

    auto *dst_y = (uint8_t *) env->GetByteArrayElements(dstY, NULL);
    auto *dst_u = (uint8_t *) env->GetByteArrayElements(dstU, NULL);
    auto *dst_v = (uint8_t *) env->GetByteArrayElements(dstV, NULL);


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

extern "C"
JNIEXPORT jint JNICALL
Java_net_rabbitknight_open_yuvutils_YuvUtils_yuvConvertToI420___3BII_3BII_3BII_3BIIIIIIIIII(
        JNIEnv *env, jclass clazz, jbyteArray src, jint offset, jint length, jbyteArray dst_y,
        jint dst_offset_y, jint dst_stride_y, jbyteArray dst_u, jint dst_offset_u,
        jint dst_stride_u, jbyteArray dst_v, jint dst_offset_v, jint dst_stride_v, jint left,
        jint top, jint crop_width, jint crop_height, jint src_width, jint src_height, jint rotate,
        jint format) {
    auto *src_frame = (uint8_t *) env->GetByteArrayElements(src, NULL);

    auto *dstY = (uint8_t *) env->GetByteArrayElements(dst_y, NULL);
    auto *dstU = (uint8_t *) env->GetByteArrayElements(dst_u, NULL);
    auto *dstV = (uint8_t *) env->GetByteArrayElements(dst_v, NULL);

    auto rotationMode = static_cast<libyuv::RotationMode>(rotate);
    auto fourcc = static_cast<libyuv::FourCC>(format);

    int rst = libyuv::ConvertToI420(src_frame + offset, length,
                                    dstY + dst_offset_y, dst_stride_y,
                                    dstU + dst_offset_u, dst_stride_u,
                                    dstV + dst_offset_v, dst_stride_v,
                                    left, top, src_width, src_height, crop_width, crop_height,
                                    rotationMode, format
    );

    env->ReleaseByteArrayElements(dst_y, (jbyte *) dstY, 0);
    env->ReleaseByteArrayElements(dst_u, (jbyte *) dstU, 0);
    env->ReleaseByteArrayElements(dst_v, (jbyte *) dstV, 0);

    env->ReleaseByteArrayElements(src, (jbyte *) src_frame, 0);
    return rst;

}
extern "C"
JNIEXPORT jint JNICALL
Java_net_rabbitknight_open_yuvutils_YuvUtils_yuvConvertToI420__Ljava_nio_ByteBuffer_2I_3BII_3BII_3BIIIIIIIIII(
        JNIEnv *env, jclass clazz, jobject src, jint length, jbyteArray dst_y, jint dst_offset_y,
        jint dst_stride_y, jbyteArray dst_u, jint dst_offset_u, jint dst_stride_u, jbyteArray dst_v,
        jint dst_offset_v, jint dst_stride_v, jint left, jint top, jint crop_width,
        jint crop_height, jint src_width, jint src_height, jint rotate, jint format) {

    auto *src_frame = (uint8_t *) env->GetDirectBufferAddress(src);

    auto *dstY = (uint8_t *) env->GetByteArrayElements(dst_y, NULL);
    auto *dstU = (uint8_t *) env->GetByteArrayElements(dst_u, NULL);
    auto *dstV = (uint8_t *) env->GetByteArrayElements(dst_v, NULL);

    auto rotationMode = static_cast<libyuv::RotationMode>(rotate);
    auto fourcc = static_cast<libyuv::FourCC>(format);

    int rst = libyuv::ConvertToI420(src_frame, length,
                                    dstY + dst_offset_y, dst_stride_y,
                                    dstU + dst_offset_u, dst_stride_u,
                                    dstV + dst_offset_v, dst_stride_v,
                                    left, top, src_width, src_height, crop_width, crop_height,
                                    rotationMode, format
    );

    env->ReleaseByteArrayElements(dst_y, (jbyte *) dstY, 0);
    env->ReleaseByteArrayElements(dst_u, (jbyte *) dstU, 0);
    env->ReleaseByteArrayElements(dst_v, (jbyte *) dstV, 0);
    return rst;
}
