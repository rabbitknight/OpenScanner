package net.rabbitknight.open.yuvutils;

import android.media.Image;

import java.nio.ByteBuffer;

public class NativeLib {

    // Used to load the 'yuvutils' library on application startup.
    static {
        System.loadLibrary("yuvutils");
    }

    /**
     * YUV数据的基本的处理（nv21-->i420-->mirror-->scale-->rotate）
     *
     * @param nv21Src    原始数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst_width  缩放的宽
     * @param i420Dst    目标数据
     * @param dst_height 缩放的高
     * @param mode       压缩模式。这里为0，1，2，3 速度由快到慢，质量由低到高，一般用0就好了，因为0的速度最快
     * @param degree     旋转的角度，90，180和270三种。切记，如果角度是90或270，则最终i420Dst数据的宽高会调换。
     * @param isMirror   是否镜像，一般只有270的时候才需要镜像
     */
    public static native void yuvCompress(byte[] nv21Src, int width, int height, byte[] i420Dst, int dst_width, int dst_height, int mode, int degree, boolean isMirror);

    /**
     * yuv数据的裁剪操作
     *
     * @param i420Src    原始数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param i420Dst    输出数据
     * @param dst_width  输出的宽
     * @param dst_height 输出的高
     * @param left       裁剪的x的开始位置，必须为偶数，否则显示会有问题
     * @param top        裁剪的y的开始位置，必须为偶数，否则显示会有问题
     **/
    public static native void yuvCropI420(byte[] i420Src, int srcOffset, int width, int height, byte[] i420Dst, int dstOffset, int dst_width, int dst_height, int left, int top);

    /**
     * yuv数据的镜像操作
     *
     * @param i420Src i420原始数据
     * @param width
     * @param height
     * @param i420Dst i420目标数据
     */
    @Deprecated
    public static native void yuvMirrorI420(byte[] i420Src, int width, int height, byte[] i420Dst);

    /**
     * yuv数据的缩放操作
     *
     * @param i420Src   i420原始数据
     * @param width     原始宽度
     * @param height    原始高度
     * @param i420Dst   i420目标数据
     * @param dstWidth  目标宽度
     * @param dstHeight 目标高度
     * @param mode      压缩模式 ，0~3，质量由低到高，一般传入0
     */
    public static native void yuvScaleI420(byte[] i420Src, int width, int height, byte[] i420Dst, int dstWidth, int dstHeight, int mode);

    /**
     * yuv数据的旋转操作
     *
     * @param i420Src i420原始数据
     * @param width
     * @param height
     * @param i420Dst i420目标数据
     * @param degree  旋转角度
     */
    public static native void yuvRotateI420(byte[] i420Src, int width, int height, byte[] i420Dst, int degree);

    /**
     * 将NV21转化为I420
     *
     * @param nv21Src 原始I420数据
     * @param width   原始的宽
     * @param width   原始的高
     * @param i420Dst 转化后的NV21数据
     */
    public static native void yuvNV21ToI420(byte[] nv21Src, int width, int height, byte[] i420Dst);

    /**
     * 将I420转化为NV21
     *
     * @param i420Src 原始I420数据
     * @param width   原始的宽
     * @param width   原始的高
     * @param nv21Dst 转化后的NV21数据
     **/
    public static native void yuvI420ToNV21(byte[] i420Src, int width, int height, byte[] nv21Dst);

    /**
     * 将NV21转化为I420，同时可旋转
     *
     * @param nv21Src 原始NV21数据
     * @param width   原始的宽
     * @param height  原始的高
     * @param i420Dst 转化后的I420数据
     * @param degree  旋转角度：0、90、180、270
     */
    public static native void yuvNV21ToI420AndRotate(byte[] nv21Src, int width, int height, byte[] i420Dst, int degree);

    /**
     * 将I420转化为RGB24
     *
     * @param i420Src  原始I420数据
     * @param width    原始的宽
     * @param height   原始的高
     * @param rgb24Dst 转化后的RGB24数据
     */
    public static native void yuvI420ToRGB24(byte[] i420Src, int width, int height, byte[] rgb24Dst);

    /**
     * 将I420转化为ARGB
     *
     * @param i420Src    原始I420数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst_stride 跨距，可传入0或width
     * @param argbDst    转化后的ARGB数据
     */
    public static native void yuvI420ToARGB(byte[] i420Src, int width, int height, int dst_stride, byte[] argbDst);

    /**
     * 将I420转化为RGBA
     *
     * @param i420Src    原始I420数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst_stride 跨距，可传入0或width
     * @param rgbaMacDst 转化后的RGBA数据
     */
    public static native void yuvI420ToRGBAMac(byte[] i420Src, int width, int height, int dst_stride, byte[] rgbaMacDst);

    /**
     * 将I420转化为ARGB4444
     *
     * @param i420Src     原始I420数据
     * @param width       原始的宽
     * @param height      原始的高
     * @param dst_stride  跨距，可传入0或width
     * @param argb4444Dst 转化后的ARGB4444数据
     */
    public static native void yuvI420ToARGB4444(byte[] i420Src, int width, int height, int dst_stride, byte[] argb4444Dst);

    /**
     * 将I420转化为RGB565
     *
     * @param i420Src   原始I420数据
     * @param width     原始的宽
     * @param height    原始的高
     * @param rgb565Dst 转化后的RGB565数据
     */
    public static native void yuvI420ToRGB565(byte[] i420Src, int width, int height, byte[] rgb565Dst);

    /**
     * 将I420转化为RGB565
     *
     * @param i420Src   原始I420数据
     * @param width     原始的宽
     * @param height    原始的高
     * @param rgb565Dst 转化后的RGB565数据
     */
    public static native void yuvI420ToRGB565Android(byte[] i420Src, int width, int height, byte[] rgb565Dst);

    /**
     * 将I420转化为ARGB1555
     *
     * @param i420Src     原始I420数据
     * @param width       原始的宽
     * @param height      原始的高
     * @param dst_stride  跨距，可传入0或width
     * @param argb1555Dst 转化后的ARGB1555数据
     */
    public static native void yuvI420ToARGB1555(byte[] i420Src, int width, int height, int dst_stride, byte[] argb1555Dst);

    /**
     * 将I420转化为YUY2
     *
     * @param i420Src    原始I420数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst_stride 跨距，可传入0或width
     * @param yuy2Dst    转化后的YUY2数据
     */
    public static native void yuvI420ToYUY2(byte[] i420Src, int width, int height, int dst_stride, byte[] yuy2Dst);

    /**
     * 将I420转化为UYVY
     *
     * @param i420Src    原始I420数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst_stride 跨距，可传入0或width
     * @param uyvyDst    转化后的UYVY数据
     */
    public static native void yuvI420ToUYVY(byte[] i420Src, int width, int height, int dst_stride, byte[] uyvyDst);

    /**
     * 将I420转化为YV12
     *
     * @param i420Src    原始I420数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst_stride 跨距，可传入0或width
     * @param yv12Dst    转化后的YV12数据
     */
    public static native void yuvI420ToYV12(byte[] i420Src, int width, int height, int dst_stride, byte[] yv12Dst);

    /**
     * 将YV12转化为I420
     *
     * @param yv12Src 原始YV12数据
     * @param width   原始的宽
     * @param height  原始的高
     * @param i420Dst 转化后的I420数据
     */
    public static native void yuvYV12ToI420(byte[] yv12Src, int width, int height, byte[] i420Dst);

    /**
     * 将NV12转化为I420
     *
     * @param nv12Src 原始NV12数据
     * @param width   原始的宽
     * @param height  原始的高
     * @param i420Dst 转化后的I420数据
     */
    public static native void yuvNV12ToI420(byte[] nv12Src, int width, int height, byte[] i420Dst);

    /**
     * 将I420转化为NV12
     *
     * @param i420Src 原始I420数据
     * @param width   原始的宽
     * @param height  原始的高
     * @param nv12Dst 转化后的NV12数据
     */
    public static native void yuvI420ToNv12(byte[] i420Src, int width, int height, byte[] nv12Dst);

    /**
     * 将I420转化为NV12，同时可以旋转
     *
     * @param nv12Src 原始NV12数据
     * @param width   原始的宽
     * @param height  原始的高
     * @param i420Dst 转化后的I420数据
     * @param degree  旋转角度
     */
    public static native void yuvNV12ToI420AndRotate(byte[] nv12Src, int width, int height, byte[] i420Dst, int degree);

    /**
     * 将NV12转化为RGB565
     *
     * @param nv12Src   原始NV12数据
     * @param width     原始的宽
     * @param height    原始的高
     * @param rgb565Dst 转化后的RGB565数据
     */
    public static native void yuvNV12ToRGB565(byte[] nv12Src, int width, int height, byte[] rgb565Dst);

    /**
     * 将I420转化为RGBA
     *
     * @param i420Src    原始I420数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst_stride 跨距，可传入0或width
     * @param rgbaDst    转化后的RGBA数据
     */
    public static native void yuvI420ToRGBAIPhone(byte[] i420Src, int width, int height, int dst_stride, byte[] rgbaDst);

    /**
     * 将I420复制一份
     *
     * @param i420Src    原始I420数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst_stride 跨距，可传入0或width
     * @param i420Dst    转化后的I420数据
     */
    public static native void yuvI420Copy(byte[] i420Src, int width, int height, int dst_stride, byte[] i420Dst);

    /**
     * 将UYVY转化为I420
     *
     * @param uyvySrc 原始UYVY数据
     * @param width   原始的宽
     * @param height  原始的高
     * @param i420Dst 转化后的I420数据
     */
    public static native void yuvUYVYToI420(byte[] uyvySrc, int width, int height, byte[] i420Dst);

    /**
     * 将YUY2转化为I420
     *
     * @param yuy2Src 原始YUY2数据
     * @param width   原始的宽
     * @param height  原始的高
     * @param i420Dst 转化后的I420数据
     */
    public static native void yuvYUY2ToI420(byte[] yuy2Src, int width, int height, byte[] i420Dst);

    /**
     * 将RGB24转化为ARGB
     *
     * @param rgb24Src   原始RGB24数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst_stride 跨距，可传入0或width
     * @param argbDst    转化后的ARGB数据
     */
    public static native void yuvRGB24ToARGB(byte[] rgb24Src, int width, int height, int dst_stride, byte[] argbDst);

    /**
     * c
     *
     * @param rgb24Src 原始RGB24数据
     * @param width    原始的宽
     * @param height   原始的高
     * @param i420Dst  转化后的I420数据
     */
    public static native void yuvRGB24ToI420(byte[] rgb24Src, int width, int height, byte[] i420Dst);

    /**
     * 将I420转化为ARGB
     *
     * @param i420Src    原始I420数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param dst_stride 跨距，可传入0或width
     * @param argbMacDst 转化后的ARGB数据
     */
    public static native void yuvI420ToARGBMac(byte[] i420Src, int width, int height, int dst_stride, byte[] argbMacDst);

    /**
     * 将ARGB转化为I420
     *
     * @param argbMacSrc 原始ARGB数据
     * @param width      原始的宽
     * @param height     原始的高
     * @param i420Dst    转化后的I420数据
     */
    public static native void yuvARGBMacToI420(byte[] argbMacSrc, int width, int height, byte[] i420Dst);

    /**
     * 将I420左右镜像
     *
     * @param i420Src 原始I420数据
     * @param width   原始的宽
     * @param height  原始的高
     * @param i420Dst 转化后的I420数据
     */
    public static native void yuvMirrorI420LeftRight(byte[] i420Src, int width, int height, byte[] i420Dst);

    /**
     * 将I420上下镜像
     *
     * @param i420Src 原始I420数据
     * @param width   原始的宽
     * @param height  原始的高
     * @param i420Dst 转化后的I420数据
     */
    public static native void yuvMirrorI420UpDown(byte[] i420Src, int width, int height, byte[] i420Dst);

    public static native void yuvConvertFromI420(byte[] y, int offsetY, int strideY,
                                                 byte[] u, int offsetU, int strideU,
                                                 byte[] v, int offsetV, int strideV,
                                                 byte[] dst, int dstOffset, int dstStride,
                                                 int width, int height, int format);

    /**
     * YUV I420纹理融合
     *
     * @param image1    图像1
     * @param offset1   像素偏移
     * @param image2    图像2
     * @param offset2   像素偏移
     * @param alpha     alpha通道
     * @param out       输出
     * @param offsetOut 输出偏移
     * @param width     宽度
     * @param height    高度
     */
    public static native void yuvI420Blend(byte[] image1, int offset1, byte[] image2, int offset2, byte[] alpha, byte[] out, int offsetOut, int width, int height);

    /**
     * Android MediaCodec输出Image YUV420_888转成I420
     *
     * @param srcY          Y像素
     * @param srcOffsetY    像素偏移
     * @param srcStrideY    跨距
     * @param srcU          U像素
     * @param srcOffsetU    像素偏移
     * @param srcStrideU    跨距
     * @param srcV          V像素
     * @param srcOffsetV    像素偏移
     * @param srcStrideV    跨距
     * @param pixelStrideUV 2/1
     * @param dstY          Y像素
     * @param dstOffsetY    像素偏移
     * @param dstStrideY    跨距
     * @param dstU          U像素
     * @param dstOffsetU    像素偏移
     * @param dstStrideU    跨距
     * @param dstV          V像素
     * @param dstOffsetV    像素偏移
     * @param stStrideV     跨距
     * @param width         宽度
     * @param height        高低
     */
    public static native int yuvAndroid420ToI420(byte[] srcY, int srcOffsetY, int srcStrideY,
                                                 byte[] srcU, int srcOffsetU, int srcStrideU,
                                                 byte[] srcV, int srcOffsetV, int srcStrideV,
                                                 int pixelStrideUV,
                                                 byte[] dstY, int dstOffsetY, int dstStrideY,
                                                 byte[] dstU, int dstOffsetU, int dstStrideU,
                                                 byte[] dstV, int dstOffsetV, int stStrideV,
                                                 int width, int height);

    /**
     * Android MediaCodec输出Image YUV420_888转成I420
     *
     * @param yuvPlanesY    Y像素
     * @param srcStrideY    跨距
     * @param yuvPlanesU    Y像素
     * @param srcStrideU    跨距
     * @param yuvPlanesV    Y像素
     * @param srcStrideV    跨距
     * @param pixelStrideUV UV跨距
     * @param dstY          输出Y
     * @param dstOffsetY    像素偏移
     * @param dstStrideY    跨距
     * @param dstU          输出U
     * @param dstOffsetU    像素偏移
     * @param dstStrideU    跨距
     * @param dstV          输出V
     * @param dstOffsetV    像素偏移
     * @param stStrideV     跨距
     * @param width         宽度
     * @param height        高度
     */
    public static native int yuvAndroid420ToI420(ByteBuffer yuvPlanesY, int srcStrideY,
                                                 ByteBuffer yuvPlanesU, int srcStrideU,
                                                 ByteBuffer yuvPlanesV, int srcStrideV,
                                                 int pixelStrideUV,
                                                 byte[] dstY, int dstOffsetY, int dstStrideY,
                                                 byte[] dstU, int dstOffsetU, int dstStrideU,
                                                 byte[] dstV, int dstOffsetV, int stStrideV,
                                                 int width, int height);


    /**
     * Image转YUV
     *
     * @param image
     * @param dstY
     * @param dstOffsetY
     * @param dstStrideY
     * @param dstU
     * @param dstOffsetU
     * @param dstStrideU
     * @param dstV
     * @param dstOffsetV
     * @param stStrideV
     * @return
     */
    public static int yuvAndroid420ToI420(Image image, byte[] dstY, int dstOffsetY, int dstStrideY,
                                          byte[] dstU, int dstOffsetU, int dstStrideU,
                                          byte[] dstV, int dstOffsetV, int stStrideV) {
        Image.Plane[] planes = image.getPlanes();
        return yuvAndroid420ToI420(planes[0].getBuffer(),
                planes[0].getRowStride(),
                planes[1].getBuffer(), planes[1].getRowStride(),
                planes[2].getBuffer(), planes[2].getRowStride(),
                planes[1].getPixelStride(),
                dstY, dstOffsetY, dstStrideY,
                dstU, dstOffsetU, dstStrideU,
                dstV, dstOffsetV, stStrideV,
                image.getWidth(), image.getHeight());
    }
}