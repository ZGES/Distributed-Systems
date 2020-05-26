package ice.servants;

import com.zeroc.Ice.Current;
import com.zeroc.Ice.Object;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.ServantLocator;

import java.util.Collection;

public abstract class BasicLocator implements ServantLocator {
    protected Collection<String> devices;
    protected ObjectAdapter objectAdapter;

    public BasicLocator(ObjectAdapter objectAdapter, Collection<String> devices) {
        this.objectAdapter = objectAdapter;
        this.devices = devices;
    }

    protected boolean accepts(String name) {
        return devices.contains(name);
    }

    @Override
    public void finished(Current current, Object object, java.lang.Object o) {
        System.out.println("[SENSOR] Proceeded work for: " + current.id.name);
    }

    @Override
    public void deactivate(String s) {
        System.out.println("[FRIDGE] Deactivated: " + s);
    }
}
