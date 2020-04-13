package org.doubrava.ergologger.bl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

public class DataSet implements DataObserver {

    private ActivityType activityType;
    private ArrayList<DataItem> dataItems;

    private Duration sumPause;
    private Instant pauseStart;
    private Instant pauseEnd;

    public DataSet(ActivityType activityType) {
        this.activityType = activityType;
        this.dataItems = new ArrayList<DataItem>();
        this.sumPause = Duration.ZERO;
        this.pauseStart = null;
        this.pauseEnd = null;
    }

    @Override
    public void updateData(Instant timestamp, HashMap<SensorType, Double> valueMap) {
        this.dataItems.add(new DataItem(timestamp, (HashMap<SensorType, Double>) valueMap.clone()));

        System.out.println("  " + timestamp.toString());
        System.out.println("  " + valueMap.toString());
        System.out.println("");
    }

    @Override
    public void onStart() {
        this.dataItems.clear();
        this.sumPause = Duration.ZERO;
        this.pauseStart = null;
        this.pauseEnd = null;
    }

    @Override
    public void onPause() {
        this.pauseStart = Instant.now();
    }

    @Override
    public void onRestart() {
        this.pauseEnd = Instant.now();
        if (this.pauseStart != null) {
            Duration diff = Duration.between(this.pauseStart, this.pauseEnd);
            this.sumPause = this.sumPause.plus(diff);
        }
        this.pauseStart = null;
        this.pauseEnd = null;
    }

    @Override
    public void onStop() {}

    public Duration getDuration(boolean excludePause) {
        Duration diff = Duration.between(this.getFirstTimestamp(), this.getLastTimestamp());
        if (excludePause) {
            diff = diff.minus(this.sumPause);
        }
        return diff;
    }

    public Instant getFirstTimestamp() {
        return this.dataItems.get(0).getTimestamp();
    }

    public Instant getLastTimestamp() {
        return this.dataItems.get(this.dataItems.size() - 1).getTimestamp();
    }

    public double getLastValue(SensorType sensorType) {
        return this.dataItems.get(this.dataItems.size() - 1).getValue(sensorType);
    }

    public double getMaxValue(SensorType sensorType) {
        double lastValue = 0;
        for (int i = 0; i < this.dataItems.size(); i++) {
            if (this.dataItems.get(i).getValue(sensorType) > lastValue) {
                lastValue = this.dataItems.get(i).getValue(sensorType);
            }
        }
        return lastValue;
    }

    public double getAverageValue(SensorType sensorType) {
        double sumValue = 0;
        for (int i = 0; i < this.dataItems.size(); i++) {
            sumValue += this.dataItems.get(i).getValue(sensorType);
        }
        return sumValue / this.dataItems.size();
    }

}
