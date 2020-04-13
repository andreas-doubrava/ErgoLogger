package org.doubrava.ergologger.bl;

import java.time.Instant;
import java.util.HashMap;

public interface DataObserver {
    public void updateData(Instant timestamp, HashMap<SensorType, Double> valueMap);
    public void onStart();
    public void onPause();
    public void onRestart();
    public void onStop();
}
