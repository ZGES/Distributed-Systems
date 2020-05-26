package ice.servants;

import com.zeroc.Ice.Current;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ServantLocator;
import ice.sensors.CameraControlI;

import java.util.Collection;

public class CameraControlLocator extends BasicLocator {
    public CameraControlLocator(ObjectAdapter objectAdapter, Collection<String> devices) {
        super(objectAdapter, devices);
    }

    @Override
    public ServantLocator.LocateResult locate(Current current) {
        if (accepts(current.id.name)) {
            CameraControlI sensor = new CameraControlI();
            objectAdapter.add(sensor, current.id);
            return new ServantLocator.LocateResult(sensor, null);
        } else {
            return new ServantLocator.LocateResult();
        }
    }

}