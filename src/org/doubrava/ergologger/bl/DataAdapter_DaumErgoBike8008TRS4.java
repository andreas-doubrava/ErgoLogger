package org.doubrava.ergologger.bl;

import java.util.HashMap;

public class DataAdapter_DaumErgoBike8008TRS4 extends DataAdapter {

    @Override
    public String getName() { return "Daum_ErgoBike_8008_TRS4"; }

    // *** Connection *********************************************************

    @Override
    public void openConnection() {

    }

    @Override
    public boolean testConnection() {
        return false;
    }

    @Override
    public void closeConnection() {

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

    }
}
