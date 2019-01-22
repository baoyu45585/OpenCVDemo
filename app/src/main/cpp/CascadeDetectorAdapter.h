//
// Created by Administrator on 2019/1/16 0016.
//

#ifndef OPENCVDEMO_CASCADEDETECTORADAPTER_H
#define OPENCVDEMO_CASCADEDETECTORADAPTER_H
#include <opencv/cv.hpp>
#include "AndroidLog.h"


using namespace cv;



class CascadeDetectorAdapter : public DetectionBasedTracker::IDetector{


public:
    CascadeDetectorAdapter();
    Ptr<CascadeClassifier> Detector;

public:
    CascadeDetectorAdapter(Ptr<CascadeClassifier> detector);
    virtual ~CascadeDetectorAdapter();

    void detect(const Mat &Image, std::vector<Rect> &objects);
};


#endif //OPENCVDEMO_CASCADEDETECTORADAPTER_H
