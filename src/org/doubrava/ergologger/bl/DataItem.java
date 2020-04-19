package org.doubrava.ergologger.bl;

import java.time.Instant;
import java.util.HashMap;

public class DataItem {
    private Instant timestamp;
    private HashMap<SensorType, Double> valueMap;

    public DataItem(Instant timestamp, HashMap<SensorType, Double> valueMap) {
        this.timestamp = timestamp;
        this.valueMap = valueMap;
    }

    public Instant getTimestamp() {
        return this.timestamp;
    }

    public double getValue(SensorType sensorType) {
        return this.valueMap.get(sensorType);
    }

    public HashMap<SensorType, Double> getValueMap() { return this.valueMap; }

}
