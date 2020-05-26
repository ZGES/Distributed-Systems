package ice;

import com.zeroc.Ice.Communicator;
import com.zeroc.Ice.ObjectAdapter;
import com.zeroc.Ice.Util;
import ice.servants.CameraControlLocator;
import ice.servants.FridgeControlLocator;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Server {

    public void run(String... args) {
        try (Communicator communicator = Util.initialize(args)) {
            ObjectAdapter objectAdapter = communicator.createObjectAdapter("SmartHomeAdapter");
            System.out.println("[INFO] Server name: " + objectAdapter.getName());
            System.out.println("[INFO] Server configuration: " + Arrays.toString(objectAdapter.getEndpoints()));

            objectAdapter.addServantLocator(
                    new FridgeControlLocator(objectAdapter, Stream.of("fridge1", "fridge2")
                            .peek(sensor -> System.out.println("[FRIDGE] Added servant for: " + sensor))
                            .collect(Collectors.toList())),
                    "fridge");

            objectAdapter.addServantLocator(
                    new CameraControlLocator(objectAdapter, Stream.of("cam-garden", "cam-garage")
                            .peek(sensor -> System.out.println("[CAMERA SENSOR] Added servant for: " + sensor))
                            .collect(Collectors.toList())),
                    "camSensor");

            objectAdapter.activate();
            System.out.println("[INFO] Entering main loop");
            communicator.waitForShutdown();
        }
    }
}
