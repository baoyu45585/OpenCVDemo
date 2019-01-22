package com.ms.android.opencvdemo;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.objdetect.CascadeClassifier;


/**
 * Created by Administrator on 2017/2/10.
 */

public class NDKUtils {


    private CascadeClassifier mJavaDetector;
    private static final String TAG = "FaceDetectActivity";
    static {
        System.loadLibrary("native-lib");
    }


    // 构造方法：初始化人脸检测引擎
    public NDKUtils(String cascadeName,int minFaceSize) {
        if (cascadeName!=null){
            // 使用模型文件初始化人脸检测引擎
            mJavaDetector = new CascadeClassifier(cascadeName);
            if (mJavaDetector.empty()) {
                Log.e(TAG, "加载cascade classifier失败");
                mJavaDetector = null;
            } else {
                Log.d(TAG, "Loaded cascade classifier from " + cascadeName);
            }
            n_CreateObject(cascadeName,minFaceSize);
        }
    }
    public NDKUtils() {

    }


    public CascadeClassifier getmJavaDetector() {
        return mJavaDetector;
    }


    // 开始人脸检测
    public void start() {

        n_Start();
    }

    // 停止人脸检测
    public void stop() {
        n_Stop();
    }

    // 设置人脸最小尺寸
    public void setMinFaceSize(int size) {

        n_SetFaceSize(size);
    }

    // 检测人脸
    public void detect(Mat imageGray, MatOfRect faces) {

        n_Detect(imageGray.getNativeObjAddr(), faces.getNativeObjAddr());
    }

    // 检测人脸
    public void detect(byte[] buf, int w, int h,MatOfRect faces) {

        n_Detect(buf, w, h,faces.getNativeObjAddr());
    }

    // 释放资源
    public void release() {
        n_DestroyObject();
    }


    public  native int[] n_gray(int[] buf, int w, int h);

    private  native void n_CreateObject(String cascadeName, int minFaceSize);
    private  native void n_DestroyObject();
    private  native void n_Start();
    private  native void n_Stop();
    private  native void n_SetFaceSize( int size);
    private  native void n_Detect(long inputImage, long faces);
    private  native void n_Detect(byte[] buf, int w, int h, long faces);

}
