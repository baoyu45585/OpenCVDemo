#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <vector>
#include <opencv2/core.hpp>
#include <opencv2/objdetect.hpp>
#include <opencv2/highgui/highgui.hpp>
#include "WlFace.h"

#include <android/log.h>
#include "CascadeDetectorAdapter.h"


WlFace * face=NULL;

extern "C"
JNIEXPORT jintArray JNICALL
Java_com_ms_android_opencvdemo_NDKUtils_n_1gray(JNIEnv *env, jobject instance, jintArray buf, jint w, jint h) {
    jint *cbuf = env->GetIntArrayElements(buf, NULL);
    if (cbuf == NULL) {
        return 0;
    }
    Mat imgData(h, w, CV_8UC4, (unsigned char *) cbuf);
    uchar* ptr = imgData.ptr(0);
    for(int i = 0; i < w*h; i ++){
        //计算公式：Y(亮度) = 0.299*R + 0.587*G + 0.114*B
        //对于一个int四字节，其彩色值存储方式为：BGRA
        int grayScale = (int)(ptr[4*i+2]*0.299 + ptr[4*i+1]*0.587 + ptr[4*i+0]*0.114);
        ptr[4*i+1] = grayScale;
        ptr[4*i+2] = grayScale;
        ptr[4*i+0] = grayScale;
    }
    int size = w * h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, cbuf);
    env->ReleaseIntArrayElements(buf, cbuf, 0);
    return result;
}


extern "C"
JNIEXPORT void  JNICALL
Java_com_ms_android_opencvdemo_NDKUtils_n_1CreateObject(JNIEnv * env,jobject instance, jstring jFileName,jint faceSize) {

    const char* jnamestr = env->GetStringUTFChars(jFileName, NULL);
    std::string stdFileName(jnamestr);
    jlong result = 0;
    try
    {
        face=new WlFace(stdFileName,faceSize);
    } catch (...)
    {
        LOGD("nCreateObject caught  exception");
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "exception  nCreateObject()");
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ms_android_opencvdemo_NDKUtils_n_1DestroyObject(JNIEnv *env,jobject instance) {
    try
    {
        if(face!=NULL)
        {
            delete(face);
        }
    }
    catch (...)
    {
        LOGD("nativeDestroyObject caught exception");
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception nativeDestroyObject()");
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ms_android_opencvdemo_NDKUtils_n_1Start(JNIEnv *env, jobject instance) {
    try
    {
        if(face!=NULL)
        {
            face->start();
        }
    }
    catch (...)
    {
        LOGD("nativeStart caught exception");
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception nativeStart()");
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ms_android_opencvdemo_NDKUtils_n_1Stop(JNIEnv *env, jobject instance) {
    try
    {
        if(face!=NULL)
        {
            face->stop();
        }
    }
    catch (...)
    {
        LOGD("nativeStop caught exception");
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception nativeStop()");
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_ms_android_opencvdemo_NDKUtils_n_1SetFaceSize(JNIEnv *env,jobject instance,jint faceSize) {
    try
    {
        if(face!=NULL)
        {
            face->setFaceSize(faceSize);
        }
    }
    catch (...)
    {
        LOGD("nativeSetFaceSize caught  exception");
        jclass je = env->FindClass("java/lang/Exception");
        env->ThrowNew(je, "Unknown exception nativeSetFaceSize()");
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_com_ms_android_opencvdemo_NDKUtils_n_1Detect(JNIEnv * jenv,jobject instance, jlong imageGray, jlong faces) {
    try
    {
        if(face!=NULL)
        {
            face->detect(imageGray,faces);
        }
    }
    catch(cv::Exception& e)
    {
        LOGD("Exception: %s", e.what());
        jclass je = jenv->FindClass("org/opencv/core/CvException");
        if(!je)
            je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, e.what());
    }
    catch (...)
    {
        LOGD("nativeDetect caught  exception");
        jclass je = jenv->FindClass("java/lang/Exception");
        jenv->ThrowNew(je, "Unknown exception nativeDetect()");
    }
}
extern "C"
JNIEXPORT void JNICALL
Java_com_ms_android_opencvdemo_NDKUtils_n_1Detect__Lbyte_3_093_2IIJ(JNIEnv *env, jobject instance,
                                                                    jbyteArray buf_, jint w, jint h,
                                                                    jlong faces) {
    jbyte *buf = env->GetByteArrayElements(buf_, NULL);

    if(buf== NULL)
    {
        return;
    }
    Mat outImg = Mat::zeros(h, w, CV_8UC1);


    memcpy(outImg.data, buf,w*h);
    if(face!=NULL)
    {
        face->detect(&outImg,faces);
    }

    env->ReleaseByteArrayElements(buf_, buf, 0);
}