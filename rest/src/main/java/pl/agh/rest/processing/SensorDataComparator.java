package pl.agh.rest.processing;

import java.util.Comparator;

public class SensorDataComparator implements Comparator<SensorData> {

    @Override
    public int compare(SensorData sensorData1, SensorData sensorData2) {
        return sensorData1.getKey().compareTo(sensorData2.getKey());
    }
}
