package com.ms.android.opencvdemo;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.ms.android.opencvdemo.utils.FileUtils;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;



/**
 * 人脸检测
 * <p>
 * Created by jiangdongguo on 2018/1/4.
 */

public class FaceDetectActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final int JAVA_DETECTOR = 0;
    private static final int NATIVE_DETECTOR = 1;
    private static final String TAG = "FaceDetectActivity";

    CameraBridgeViewBase mCameraView;

    private Mat mGray;
    private Mat mRgba;
    private int mDetectorType = NATIVE_DETECTOR;
    private int mAbsoluteFaceSize = 0;
    private float mRelativeFaceSize = 0.2f;
    private NDKUtils mNativeDetector;

    private static final Scalar FACE_RECT_COLOR = new Scalar(0, 255, 0, 255);



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_facedetect);
        // 绑定View
        mCameraView=findViewById(R.id.cameraView_face);
        mCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        // 注册Camera渲染事件监听器
        mCameraView.setCvCameraViewListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 静态初始化OpenCV
        Log.d(TAG, "成功加载OpenCV本地库");
        String mCascadeFile= FileUtils.getFaceFile(this);
        mNativeDetector = new NDKUtils(mCascadeFile, 0);
        // 开启渲染Camera
        mCameraView.enableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 停止渲染Camera
        if (mCameraView != null) {
            mCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止渲染Camera
        if (mCameraView != null) {
            mCameraView.disableView();
        }
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        // 灰度图像
        mGray = new Mat();
        // R、G、B彩色图像
        mRgba = new Mat();
    }

    @Override
    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        // 设置脸部大小
        if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }
        // 获取检测到的脸部数据
        MatOfRect faces = new MatOfRect();
        if (mNativeDetector != null) {
            mNativeDetector.detect(mGray, faces);
        }
        Rect[] facesArray = faces.toArray();

        // 绘制检测框
        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
        }
        return mRgba;
    }
}
