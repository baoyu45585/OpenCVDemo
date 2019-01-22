//
// Created by Administrator on 2019/1/16 0016.
//

#ifndef OPENCVDEMO_WLFACE_H
#define OPENCVDEMO_WLFACE_H
#include <string>
#include <jni.h>
#include "CascadeDetectorAdapter.h"

/*
 * 参数结构体
 */
struct DetectorAgregator
{
    cv::Ptr<CascadeDetectorAdapter> mainDetector;
    cv::Ptr<CascadeDetectorAdapter> trackingDetector;

    cv::Ptr<DetectionBasedTracker> tracker;
    DetectorAgregator(cv::Ptr<CascadeDetectorAdapter>& _mainDetector, cv::Ptr<CascadeDetectorAdapter>& _trackingDetector):
            mainDetector(_mainDetector),
            trackingDetector(_trackingDetector)
    {
        CV_Assert(_mainDetector);
        CV_Assert(_trackingDetector);

        cv::DetectionBasedTracker::Parameters DetectorParams;
        tracker = makePtr<DetectionBasedTracker>(mainDetector, trackingDetector, DetectorParams);
    }
};

class WlFace {

public:
    DetectorAgregator* agregator;

    WlFace(std::string stdFileName,int faceSize);
    ~ WlFace();
    void start();
    void stop();
    void setFaceSize(int faceSize);
    void detect(long imageGray, long faces);
    void detect(Mat* mat, long faces);
};


#endif //OPENCVDEMO_WLFACE_H
