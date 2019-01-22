package com.ms.android.opencvdemo;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ms.android.opencvdemo.utils.FileUtils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;

import java.io.IOException;


/**
 * @author shenjb@ms.com
 * @since 2017/8/29
 */
public class CameraSufaceActivity extends AppCompatActivity {


    /*** 处理照片*/
    private SurfaceHolder holder = null;
    private Camera camera = null;
    private boolean previewRunning = false; //标识是否正在预览中
    private int mCurCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    /*** 处理照片*/

    SurfaceView surfaceView = null;

    private static final String TAG = "CameraSufaceActivity";
    private Mat mGray;
    private int mAbsoluteFaceSize = 0;
    private float mRelativeFaceSize = 0.2f;
    private NDKUtils mNativeDetector;
    private int width;
    private int height;
   Rect rect=null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suface_camera);
        surfaceView = findViewById(R.id.surfaceView);
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        holder = surfaceView.getHolder();//在surfaceView中获取holder
        holder.addCallback(new MySurfaceViewCallback());
        holder.setFixedSize(metrics.widthPixels, metrics.heightPixels);

    }


    @Override
    protected void onResume() {
        super.onResume();
        initCarm();
        if(camera != null){
            if(!previewRunning){
                camera.startPreview(); //停止预览
                camera.setPreviewCallback(callback);
                previewRunning = true;
            }
        }
    }

    @Override
    public void onPause() {
        if(camera != null){
            if(previewRunning){
                camera.stopPreview(); //停止预览
                camera.setPreviewCallback(null);
                previewRunning = false;
            }
        }
        super.onPause();
    }



    private void initCarm() {
        // 静态初始化OpenCV
        Log.d(TAG, "成功加载OpenCV本地库");
        String mCascadeFile = FileUtils.getFaceFile(this);
        mNativeDetector = new NDKUtils(mCascadeFile, 0);

    }

    private Camera.PreviewCallback callback=  new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            mGray.put(0, 0, data);
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
            Log.e("facesArray==","facesArray.length"+facesArray.length);
            // 绘制检测框
            for (int i = 0; i < facesArray.length; i++) {
                rect=facesArray[i];
                Log.e("facesArray==", "facesArray[i].x" + facesArray[i].tl().x + "facesArray[i].y" + facesArray[i].tl().y);
                Log.e("facesArray==", "facesArray[i].x" + facesArray[i].br().x + "facesArray[i].y" + facesArray[i].br().y);
            }
        }
    };

    private class MySurfaceViewCallback implements SurfaceHolder.Callback{

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                   int height) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (camera ==null) {
                try {
                    camera = Camera.open(mCurCameraId);// 取得第一个摄像头
                } catch (Exception e) {
                    if (BuildConfig.DEBUG) e.printStackTrace();
                    return;
                }
                if (camera == null) {
                    return;
                }
                try {
                    Camera.Parameters param = camera.getParameters();
                    param.setPreviewFormat(ImageFormat.NV21);
                    CamcorderProfile profile ;
                    int numCameras = Camera.getNumberOfCameras();
                    if (numCameras > 1) {
                        profile = (CamcorderProfile
                                .get(Camera.CameraInfo.CAMERA_FACING_FRONT,CamcorderProfile.QUALITY_HIGH));
                    }else{
                        profile = (CamcorderProfile
                                .get(Camera.CameraInfo.CAMERA_FACING_BACK,CamcorderProfile.QUALITY_HIGH));
                    }
                    param.setPreviewSize(profile.videoFrameWidth, profile.videoFrameHeight);
                    param.setPictureSize(profile.videoFrameWidth, profile.videoFrameHeight);
                    camera.setParameters(param);//应用设置参数
                    camera.setPreviewDisplay(holder);
                    width=profile.videoFrameWidth;
                    height= profile.videoFrameHeight;
                    mGray= new Mat(height , width, CvType.CV_8UC1);
                    camera.setPreviewCallback(callback);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                camera.startPreview();//开始视频预览
                previewRunning = true;//表示已经开始预览
            }

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if(camera != null){
                if(previewRunning){
                    camera.setPreviewCallback(null);
                    camera.stopPreview(); //停止预览
                    previewRunning = false;
                    camera.release();
                    camera= null;
                }
            }
        }
    }

}


