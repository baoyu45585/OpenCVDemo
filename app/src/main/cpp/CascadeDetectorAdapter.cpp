//
// Created by Administrator on 2019/1/16 0016.
//

#include "CascadeDetectorAdapter.h"
#include <opencv/cv.hpp>



CascadeDetectorAdapter::CascadeDetectorAdapter(Ptr<CascadeClassifier> detector):IDetector(), Detector(detector)
{
    CV_Assert(detector);
}
CascadeDetectorAdapter:: ~CascadeDetectorAdapter()
{

}
// 多尺度检测人脸
void CascadeDetectorAdapter::detect(const Mat &Image, std::vector<Rect> &objects)
{
    LOGD("CascadeDetectorAdapter::Detect: scaleFactor=%.2f, minNeighbours=%d, minObjSize=(%dx%d), maxObjSize=(%dx%d)", scaleFactor, minNeighbours, minObjSize.width, minObjSize.height, maxObjSize.width, maxObjSize.height);
    Detector->detectMultiScale(Image, objects, scaleFactor, minNeighbours, 0, minObjSize, maxObjSize);

}


