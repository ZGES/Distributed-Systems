package pl.agh.rest.processing;

import org.springframework.web.client.RestTemplate;
import pl.agh.rest.domain.AirQualityIndex;
import pl.agh.rest.domain.Data;
import pl.agh.rest.domain.Sensor;
import pl.agh.rest.domain.Station;

import java.util.ArrayList;

public class DataManager {

    private static RestTemplate restTemplate = new RestTemplate();
    private static String URL = "http://api.gios.gov.pl/pjp-api/rest";
    static String stationURL = "/station/findAll";
    static String sensorURL = "/station/sensors/";
    static String dataURL = "/data/getData/";
    static String indexURL = "/aqindex/getIndex/";

    private ArrayList<Integer> getStationIDs(String cityName){
        Station[] stations = restTemplate.getForObject(URL + stationURL, Station[].class);
        ArrayList<Integer> stationIdList = new ArrayList<>();
        if (stations != null) {
            for (Station station : stations) {
                if(station.city().name().equals(cityName) && station.id().isPresent()) {
                    stationIdList.add(station.id().getAsInt());
                }
            }
        }
        return stationIdList;
    }

    private ArrayList<Sensor[]> getSensors(ArrayList<Integer> stationIdList){
        ArrayList<Sensor[]> sensorList = new ArrayList<>();
        for (Integer id: stationIdList) {
            Sensor[] sensors = restTemplate.getForObject(URL + sensorURL + id.toString(), Sensor[].class);
            sensorList.add(sensors);
        }
        return sensorList;
    }

    private ArrayList<SensorData> collectData(ArrayList<Sensor[]> sensorList){
        ArrayList<SensorData> sensorData = new ArrayList<>();
        for (Sensor[] sensors : sensorList) {
            for (Sensor sensor : sensors) {
                Data data = restTemplate.getForObject(URL + dataURL + sensor.id(), Data.class);
                double max = -1;
                double sum = 0;
                double latest = -1;
                int counter = 0;
                double currVal;
                if (data != null) {
                    if (data.values()[0].value().isPresent())
                        latest = data.values()[0].value().getAsDouble();
                    for (int k = 0; k < data.values().length; k++) {
                        if (data.values()[k].value().isPresent()) {
                            currVal = data.values()[k].value().getAsDouble();
                            if (currVal > max)
                                max = data.values()[k].value().getAsDouble();
                            sum = sum + currVal;
                            counter++;
                        }
                    }
                    double avg;
                    if (counter != 0)
                        avg = sum / counter;
                    else
                        avg = 0;
                    sensorData.add(new SensorData(data.key(), avg, max, latest));
                }
            }
        }
        return sensorData;
    }

    private ArrayList<SensorData> mergeData(ArrayList<SensorData> sensorData){
        sensorData.sort(new SensorDataComparator());
        String currKey = sensorData.get(0).getKey();
        double sum = 0;
        double max = -1;
        double latest = 0;
        int counter = 0;
        ArrayList<SensorData> mergedData = new ArrayList<>();
        for (SensorData data : sensorData) {
            if (data.getKey().equals(currKey)) {
                sum = sum + data.getAvg();
                if(data.getLatest() != -1)
                    latest = latest + data.getLatest();
                if (max < data.getMax())
                    max = data.getMax();
                counter++;
            } else {
                mergedData.add(new SensorData(currKey, sum / counter, max, latest / counter));
                currKey = data.getKey();
                sum = data.getAvg();
                max = data.getMax();
                if(data.getLatest() != -1)
                    latest = data.getLatest();
                else
                    latest = 0;
                counter = 1;
            }
        }
        mergedData.add(new SensorData(currKey, sum/counter, max, latest/counter));
        return mergedData;
    }

    private String getAirQualityIndex(ArrayList<Integer> stationIdList){
        ArrayList<String> indexes = new ArrayList<>();
        for(Integer id : stationIdList){
            AirQualityIndex airIndex = restTemplate.getForObject(URL + indexURL +id.toString(), AirQualityIndex.class);
            if(airIndex != null && airIndex.stIndexLevel() != null)
                indexes.add(airIndex.stIndexLevel().indexLevelName());
        }
        indexes.sort(new IndexComparator());
        String airQualityIndex = "";
        String currIndex = indexes.get(0);
        int amount = 0;
        int maxAmount = 0;
        for (String index : indexes) {
            if (index.equals(currIndex))
                amount++;
            else {
                if (amount > maxAmount) {
                    maxAmount = amount;
                    airQualityIndex = currIndex;
                }
                currIndex = index;
                amount = 1;
            }
        }
        if (amount > maxAmount)
            airQualityIndex = currIndex;

        return airQualityIndex;
    }

    private String stringBuilder(String cityName, String airQualityIndex,  ArrayList<SensorData> mergedData){
        StringBuilder builder = new StringBuilder("<b>Zestawienie zanieczyszczeń dla miasta " + cityName + "</b><br><br>");
        for (SensorData data: mergedData) {
            builder.append("<b>Rodzaj zanieczyszczeń</b>: ").append(data.getKey()).append(" -  <b>wartość średnia:</b> ")
                    .append(data.getAvg()).append(";   <b>wartość maksymalna:</b> ").append(data.getMax()).append(";   <b>ostatnia zmierzona wartość:</b> ")
                    .append(data.getLatest()).append("<br>");
        }
        builder.append("Indeks jakości powietrza: " + "<b>").append(airQualityIndex).append("</b>");
        return builder.toString();
    }


    public String htmlBuilder(String cityName){

        ArrayList<Integer> stationIdList = getStationIDs(cityName);
        ArrayList<Sensor[]> sensorList = getSensors(stationIdList);
        ArrayList<SensorData> sensorData = collectData(sensorList);
        ArrayList<SensorData> mergedData  = mergeData(sensorData);
        String airQualityIndex = getAirQualityIndex(stationIdList);

        return stringBuilder(cityName, airQualityIndex, mergedData);
    }
}
