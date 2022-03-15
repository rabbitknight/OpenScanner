package net.rabbitknight.open.yuvutils;

import java.nio.ByteBuffer;

public class YuvUtils {

    // Used to load the 'yuvutils' library on application startup.
    static {
        System.loadLibrary("yuvutils");
    }


    public static native int mirrorI420(byte[] srcY, int srcOffsetY, int srcStrideY,
                                        byte[] srcU, int srcOffsetU, int srcStrideU,
                                        byte[] srcV, int srcOffsetV, int srcStrideV,
                                        byte[] dstY, int dstOffsetY, int dstStrideY,
                                        byte[] dstU, int dstOffsetU, int dstStrideU,
                                        byte[] dstV, int dstOffsetV, int dstStrideV,
                                        int width, int height);

    public static native int scaleI420(byte[] srcY, int srcOffsetY, int srcStrideY,
                                       byte[] srcU, int srcOffsetU, int srcStrideU,
                                       byte[] srcV, int srcOffsetV, int srcStrideV,
                                       int srcWidth, int srcHeight,
                                       byte[] dstY, int dstOffsetY, int dstStrideY,
                                       byte[] dstU, int dstOffsetU, int dstStrideU,
                                       byte[] dstV, int dstOffsetV, int dstStrideV,
                                       int dstWidth, int dstHeight,
                                       int filterMode
    );

    public static native int rotateI420(byte[] srcY, int srcOffsetY, int srcStrideY,
                                        byte[] srcU, int srcOffsetU, int srcStrideU,
                                        byte[] srcV, int srcOffsetV, int srcStrideV,
                                        int srcWidth, int srcHeight,
                                        byte[] dstY, int dstOffsetY, int dstStrideY,
                                        byte[] dstU, int dstOffsetU, int dstStrideU,
                                        byte[] dstV, int dstOffsetV, int dstStrideV,
                                        int rotateMode
    );

    public static native int convertNV21ToI420(byte[] srcY, int srcOffsetY, int srcStrideY,
                                               byte[] srcVU, int srcOffsetVU, int srcStrideVU,
                                               byte[] dstY, int dstOffsetY, int dstStrideY,
                                               byte[] dstU, int dstOffsetU, int dstStrideU,
                                               byte[] dstV, int dstOffsetV, int dstStrideV,
                                               int width, int height);

    public static native int convertI420ToNV21(byte[] srcY, int srcOffsetY, int srcStrideY,
                                               byte[] srcU, int srcOffsetU, int srcStrideU,
                                               byte[] srcV, int srcOffsetV, int srcStrideV,
                                               byte[] dstY, int dstOffsetY, int dstStrideY,
                                               byte[] dstVU, int dstOffsetVU, int dstStrideVU,
                                               int width, int height);

    /**
     * Android MediaCodec输出Image YUV420_888转成I420
     */
    public static native int convertAndroid420ToI420(byte[] srcY, int srcOffsetY, int srcStrideY,
                                                     byte[] srcU, int srcOffsetU, int srcStrideU,
                                                     byte[] srcV, int srcOffsetV, int srcStrideV,
                                                     int pixelStrideUV,
                                                     byte[] dstY, int dstOffsetY, int dstStrideY,
                                                     byte[] dstU, int dstOffsetU, int dstStrideU,
                                                     byte[] dstV, int dstOffsetV, int stStrideV,
                                                     int width, int height);

    /**
     * Android MediaCodec输出Image YUV420_888转成I420
     */
    public static native int convertAndroid420ToI420(ByteBuffer yuvPlanesY, int srcStrideY,
                                                     ByteBuffer yuvPlanesU, int srcStrideU,
                                                     ByteBuffer yuvPlanesV, int srcStrideV,
                                                     int pixelStrideUV,
                                                     byte[] dstY, int dstOffsetY, int dstStrideY,
                                                     byte[] dstU, int dstOffsetU, int dstStrideU,
                                                     byte[] dstV, int dstOffsetV, int stStrideV,
                                                     int width, int height);

    public native static int convertToI420(ByteBuffer src, int length,
                                           byte[] dstY, int dstOffsetY, int dstStrideY,
                                           byte[] dstU, int dstOffsetU, int dstStrideU,
                                           byte[] dstV, int dstOffsetV, int dstStrideV,
                                           int left, int top, int cropWidth, int cropHeight,
                                           int srcWidth, int srcHeight,
                                           int rotate, int format);

    public native static int convertToI420(byte[] src, int offset, int length,
                                           byte[] dstY, int dstOffsetY, int dstStrideY,
                                           byte[] dstU, int dstOffsetU, int dstStrideU,
                                           byte[] dstV, int dstOffsetV, int dstStrideV,
                                           int left, int top, int cropWidth, int cropHeight,
                                           int srcWidth, int srcHeight,
                                           int rotate, int format);

    public native static int yuvConvertToARGB(byte[] src, int offset, int length,
                                              byte[] dst, int dstOffset, int dstStride,
                                              int left, int top, int cropWidth, int cropHeight,
                                              int srcWidth, int srcHeight,
                                              int rotate, int format);

    public native static int convertI420ToARGB(byte[] srcY, int srcOffsetY, int srcStrideY,
                                               byte[] srcU, int srcOffsetU, int srcStrideU,
                                               byte[] srcV, int srcOffsetV, int srcStrideV,
                                               byte[] dst, int dstOffset, int dstStride,
                                               int width, int height);

    public native static int convertI420ToARGB(ByteBuffer srcY, int srcStrideY,
                                               ByteBuffer srcU, int srcStrideU,
                                               ByteBuffer srcV, int srcStrideV,
                                               byte[] dst, int dstOffset, int dstStride,
                                               int width, int height);

    public native static int convertNV21ToBGRA(byte[] srcY, int srcOffsetY, int srcStrideY,
                                               byte[] srcVU, int srcOffsetVU, int srcStrideVU,
                                               byte[] dst, int dstOffset, int dstStride,
                                               int width, int height);

    public native static int convertBGRAToARGB(byte[] src, int srcOffset, int srcStride,
                                               byte[] dst, int dstOffset, int dstStride,
                                               int width, int height);
}