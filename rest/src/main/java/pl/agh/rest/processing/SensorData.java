package pl.agh.rest.processing;

public class SensorData {

    private double avg;
    private double max;
    private double latest;
    private String key;

    public SensorData(String key, double avg, double max, double latest){
        this.key = key;
        this.avg = avg;
        this.max = max;
        this.latest = latest;
    }

    public double getAvg() {
        return avg;
    }

    public double getLatest() {
        return latest;
    }

    public double getMax() {
        return max;
    }

    public String getKey() {
        return key;
    }
}
