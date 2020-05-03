package org.doubrava.ergologger.bl;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Random;

public class DataAdapter_Virtual extends DataAdapter {

    private Random random;

    @Override
    public String getName() { return "Virtual"; }

    // *** Connection *********************************************************

    @Override
    public void openConnection() {
        this.random = new Random();
        this.isConnected = true;
    }

    @Override
    public boolean testConnection() {
        this.isConnected = true;
        return true;
    }

    @Override
    public void closeConnection() {
        this.random = null;
        this.isConnected = false;
    }

    // *** Request Data *******************************************************

    @Override
    public HashMap<SensorType, Boolean> getSensorTypeAvailability() {
        HashMap<SensorType, Boolean> map = new HashMap<SensorType, Boolean>();
        map.put(SensorType.HRF, true);
        map.put(SensorType.POWER, true);
        map.put(SensorType.RPM, true);
        map.put(SensorType.SPEED, true);
        map.put(SensorType.DURATION, true);
        map.put(SensorType.DISTANCE, true);
        map.put(SensorType.CALORIES, true);
        return map;
    }

    @Override
    protected void doRequest() {
        this.currentTimestamp = Instant.now();

        this.currentValueMap.clear();
        this.currentValueMap.put(SensorType.HRF, 80 + (170 - 80) * random.nextDouble());
        this.currentValueMap.put(SensorType.POWER, 110 + (200 - 110) * random.nextDouble());
        this.currentValueMap.put(SensorType.RPM, 60 + (110 - 60) * random.nextDouble());
        this.currentValueMap.put(SensorType.SPEED, 18 + (45 - 18) * random.nextDouble());
        this.currentValueMap.put(SensorType.DURATION, (double) (Duration.between(this.startTimestamp, this.currentTimestamp).getSeconds()));
        this.currentValueMap.put(SensorType.DISTANCE, (double) (8 * Duration.between(this.startTimestamp, this.currentTimestamp).getSeconds()));
        this.currentValueMap.put(SensorType.CALORIES, (double) (140 / 1000 * Duration.between(this.startTimestamp, this.currentTimestamp).getSeconds()));
    }
}
