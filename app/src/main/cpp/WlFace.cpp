//
// Created by Administrator on 2019/1/16 0016.
//

#include "WlFace.h"
#include "CascadeDetectorAdapter.h"




WlFace::WlFace(std::string stdFileName,int faceSize)
{

    cv::Ptr<CascadeDetectorAdapter> mainDetector = makePtr<CascadeDetectorAdapter>(
            makePtr<CascadeClassifier>(stdFileName));
    cv::Ptr<CascadeDetectorAdapter> trackingDetector = makePtr<CascadeDetectorAdapter>(
            makePtr<CascadeClassifier>(stdFileName));
    agregator = new DetectorAgregator(mainDetector, trackingDetector);
    if (faceSize>0)
    {
        mainDetector->setMinObjectSize(Size(faceSize, faceSize));
    }
}

WlFace::~ WlFace(){
    if(agregator!=NULL)
    {
        agregator->tracker->stop();
        delete agregator;
    }
}

void WlFace::start() {
    if(agregator!=NULL)
    {
        agregator->tracker->run();
    }
}

void WlFace::stop() {
    if(agregator!=NULL)
    {
        agregator->tracker->stop();
    }
}

void WlFace::setFaceSize(int faceSize) {
    if(agregator!=NULL&&faceSize>0)
    {
        agregator->mainDetector->setMinObjectSize(Size(faceSize, faceSize));
    }
}

void WlFace::detect(long imageGray, long faces) {

    if(agregator!=NULL)
    {
        std::vector<Rect> RectFaces;
        agregator->tracker->process(*((Mat*)imageGray));
        agregator->tracker->getObjects(RectFaces);
        *((Mat*)faces) = Mat(RectFaces, true);
    }
}

void WlFace::detect(Mat *mat, long faces) {
    if(agregator!=NULL)
    {
        std::vector<Rect> RectFaces;
        agregator->tracker->process(*mat);
        agregator->tracker->getObjects(RectFaces);
        *((Mat*)faces) = Mat(RectFaces, true);
    }
}
