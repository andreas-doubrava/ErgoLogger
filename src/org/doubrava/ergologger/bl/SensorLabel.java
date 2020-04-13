package org.doubrava.ergologger.bl;

import java.util.HashMap;

public class SensorLabel {

    private volatile static SensorLabel uniqueSensorLabel;

    private SensorLabel() {}

    public static SensorLabel getInstance() {
        if (SensorLabel.uniqueSensorLabel == null) {
            synchronized (SensorLabel.class) {
                if (SensorLabel.uniqueSensorLabel == null) {
                    SensorLabel.uniqueSensorLabel = new SensorLabel();
                }
            }
        }
        return SensorLabel.uniqueSensorLabel;
    }

    public HashMap<SensorLabelItem, String> getMap(SensorType sensorType) {
        HashMap<SensorLabelItem, String> map = new HashMap<SensorLabelItem, String>();
        switch (sensorType) {
            case HRF:
                map.put(SensorLabelItem.NAME, "Heart Rate");
                map.put(SensorLabelItem.UNIT, "bpm");
                break;
            case RPM:
                map.put(SensorLabelItem.NAME, "Cadence");
                map.put(SensorLabelItem.UNIT, "rpm");
                break;
            case POWER:
                map.put(SensorLabelItem.NAME, "Power");
                map.put(SensorLabelItem.UNIT, "Watt");
                break;
            case SPEED:
                map.put(SensorLabelItem.NAME, "Speed");
                map.put(SensorLabelItem.UNIT, "km/h");
                break;
            case DISTANCE:
                map.put(SensorLabelItem.NAME, "Distance");
                map.put(SensorLabelItem.UNIT, "m");
                break;
            case DURATION:
                map.put(SensorLabelItem.NAME, "Duration");
                map.put(SensorLabelItem.UNIT, "hh:mm:ss");
                break;
            case CALORIES:
                map.put(SensorLabelItem.NAME, "Calories");
                map.put(SensorLabelItem.UNIT, "kCal");
                break;
            default:
                map.put(SensorLabelItem.NAME, "unknown");
                map.put(SensorLabelItem.UNIT, "unknown");
        }
        return map;
    }

}
