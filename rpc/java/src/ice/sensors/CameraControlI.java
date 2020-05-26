package ice.sensors;

import sr.ice.gen.Camera.CameraControl;
import sr.ice.gen.Camera.CameraStatus;
import com.zeroc.Ice.Current;

public class CameraControlI extends BasicSensor implements CameraControl {

    private CameraStatus status;
    private float angle;
    private float zoom;

    public CameraControlI() {
        this.status = CameraStatus.OFF;
        this.angle = 90;
        this.zoom = 0;
    }

    @Override
    public void turnOn(Current current) {
        if (status == CameraStatus.OFF) {
            status = CameraStatus.ON;
        }
    }

    @Override
    public void turnOff(Current current) {
        if (status == CameraStatus.ON) {
            status = CameraStatus.OFF;
        }
    }

    @Override
    public boolean isOn(Current current) {
        switch (status) {
            case OFF:
                return false;
            case ON:
                return true;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public float zoom(float times, Current current) {
        if (times >= 0 && times <= 1) {
            this.zoom = times;
        }
        return this.zoom;
    }

    @Override
    public float move(float angle, Current current) {
        if (angle >= 0 && angle <= 360) {
            this.angle = angle;
        }
        return this.angle;
    }
}