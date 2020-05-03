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

    public String getActivityTypeName() {
        switch (this.activityType) {
            case BIKING: return "Biking";
            case RUNNING: return "Running";
            case OTHER: return "Other";
            default: return "Unknown";
        }
    }
    public boolean hasData() {
        return this.dataItems.size() > 0;
    }
    public int getItemCount() {
        return this.dataItems.size();
    }
    public DataItem getItem(int index) { return this.dataItems.get(index); }

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

    public double getAverageValue(SensorType sensorType, int startIndex, int endIndex) {
        double sumValue = 0;
        for (int i = Integer.max(0, startIndex - 1); i < Integer.min(this.dataItems.size(), endIndex); i++) {
            sumValue += this.dataItems.get(i).getValue(sensorType);
        }
        return sumValue / (Integer.min(this.dataItems.size(), endIndex) - Integer.max(0, startIndex - 1));
    }

    public ArrayList<Double> getDataItemValues(SensorType sensorType, int lastItemCount) {
        ArrayList<Double> lst = new ArrayList<Double>();
        if (this.dataItems.size() > lastItemCount) {
            for (int i = this.dataItems.size() - lastItemCount - 1; i < this.dataItems.size(); i++) {
                lst.add(this.dataItems.get(i).getValue(sensorType));
            }
        } else {
            for (int i = 0; i < this.dataItems.size(); i++) {
                lst.add(this.dataItems.get(i).getValue(sensorType));
            }
        }
        return lst;
    }

    public ArrayList<Double> getDataItemAverages(SensorType sensorType, int lastItemCount) {
        ArrayList<Double> lst = new ArrayList<Double>();
        if (this.dataItems.size() > lastItemCount) {
            for (int i = this.dataItems.size() - lastItemCount - 1; i < this.dataItems.size(); i++) {
                lst.add(this.getAverageValue(sensorType, 1, i));
            }
        } else {
            for (int i = 0; i < this.dataItems.size(); i++) {
                lst.add(this.getAverageValue(sensorType, 1, i));
            }
        }
        return lst;
    }

}
