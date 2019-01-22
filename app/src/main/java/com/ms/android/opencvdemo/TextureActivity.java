package com.ms.android.opencvdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import com.ms.android.opencvdemo.utils.FileUtils;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;


/**
 * @author shenjb@ms.com
 * @since 2017/8/29
 */
public class TextureActivity extends AppCompatActivity {

    TextureView texture = null;

    private static final String TAG = "TextureActivity";
    private Mat mGray;

    private int mAbsoluteFaceSize = 0;
    private float mRelativeFaceSize = 0.2f;
    private NDKUtils mNativeDetector;
    private int width;
    private int height;

    private Camera mCamera = null;
    private int mCurCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture);
        texture = findViewById(R.id.texture_view);
        texture.setOpaque(false);
        // 不需要屏幕自动变黑。
        texture.setKeepScreenOn(true);
        init();

    }



    private void init() {
        texture.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, final int w, final int h) {
                try {
                    if (null == mCamera) {
                        mCamera = Camera.open(mCurCameraId);
                    }
                    Camera.Parameters  param = mCamera.getParameters();
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
                    mCamera.setParameters(param);//应用设置参数
                    width=profile.videoFrameWidth;
                    height= profile.videoFrameHeight;
                    mCamera.setParameters(param);
                    int displayRotation = 0;
                    WindowManager windowManager = (WindowManager)getSystemService(Context.WINDOW_SERVICE);
                    int rotation = windowManager.getDefaultDisplay().getRotation();
                    switch (rotation) {
                        case Surface.ROTATION_0:
                            displayRotation = 0;
                            break;
                        case Surface.ROTATION_90:
                            displayRotation = 90;
                            break;
                        case Surface.ROTATION_180:
                            displayRotation = 180;
                            break;
                        case Surface.ROTATION_270:
                            displayRotation = 270;
                            break;
                    }
                    Camera.CameraInfo info = new Camera.CameraInfo();
                    Camera.getCameraInfo(0, info);
                    int orientation;
                    if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                        orientation = (info.orientation - displayRotation + 360) % 360;
                    } else {
                        orientation = (info.orientation + displayRotation) % 360;
                        orientation = (360 - orientation) % 360;
                    }
                    mCamera.setParameters(param);
                    mCamera.setDisplayOrientation(orientation);
                    mCamera.setPreviewTexture(surfaceTexture);
                    // 灰度图像
                    mGray = new Mat(height, width, CvType.CV_8UC1);
                    mCamera.setPreviewCallback(new Camera.PreviewCallback() {
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
                           Log.e("facesArray==","facesArray.length=="+facesArray.length);
                            // 绘制检测框
                            for (int i = 0; i < facesArray.length; i++) {
                                final Rect rect=facesArray[i];
                                Log.e("facesArray==", "facesArray[i].x" + facesArray[i].tl().x + "facesArray[i].y" + facesArray[i].tl().y);
                                Log.e("facesArray==", "facesArray[i].x" + facesArray[i].br().x + "facesArray[i].y" + facesArray[i].br().y);

                            }
                        }
                    });

                   mCamera.startPreview();//开始视频预览
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
                return true;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 静态初始化OpenCV
        Log.d(TAG, "成功加载OpenCV本地库");
        String mCascadeFile = FileUtils.getFaceFile(this);
        mNativeDetector = new NDKUtils(mCascadeFile, 0);
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    @Override
    protected void onStop() {
        if (mCamera != null) {
            mCamera.stopPreview();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mCamera != null){
            mCamera.stopPreview(); //停止预览
            mCamera.release();
            mCamera= null;
        }
    }





    private Paint paint = new Paint();

    /**
     * 绘制人脸框。
     */
    private void showFrame(Rect rectF) {

        Canvas canvas = texture.lockCanvas();
        Log.e("canvas==", "canvas=="+null);
        if (canvas == null) {
            return;
        }
        Log.e("facesArray==", "facesArray[i].x" + rectF.tl().x + "facesArray[i].y" + rectF.tl().y);
        Log.e("facesArray==", "facesArray[i].x" + rectF.br().x + "facesArray[i].y" + rectF.br().y);
        paint.setColor(Color.YELLOW);
        paint.setStyle(Paint.Style.STROKE);
        if (rectF == null) {
            // 清空canvas
            canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            texture.unlockCanvasAndPost(canvas);
            return;
        }
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        float left = (float) rectF.tl().x;
        float top = (float) rectF.tl().y;
        float right = (float) rectF.br().x;
        float bottom = (float) rectF.tl().y;

        RectF rect = new RectF(left, top, right, bottom);
        // 绘制框
        canvas.drawRect(rect, paint);
        texture.unlockCanvasAndPost(canvas);
    }


/*** 处理照片*/
}


