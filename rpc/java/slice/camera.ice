#ifndef CAMER_ICE
#define CAMER_ICE

module Camera {

    enum CameraStatus {
        ON,
        OFF
    };

    interface CameraControl {
        void turnOn();
        void turnOff();
        bool isOn();
        float zoom(float times);
        float move(float angle);
    };

};

#endif