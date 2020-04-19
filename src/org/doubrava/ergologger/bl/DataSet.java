package org.doubrava.ergologger.bl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DataSet implements DataObserver {

    private ActivityType activityType;
    private ArrayList<DataItem> dataItems;

    private Duration sumPause;
    private Instant pauseStart;
    private Instant pauseEnd;

    public static String getFileFormatExtension(FileFormat fileFormat) {
        if (fileFormat == FileFormat.CSV) { return "csv"; }
        else if (fileFormat == FileFormat.TCX) { return "tcx"; }
        else { return "unknown"; }
    }

    public static String getFileFormatName(FileFormat fileFormat) {
        if (fileFormat == FileFormat.CSV) { return "Comma Separated Values (CSV)"; }
        else if (fileFormat == FileFormat.TCX) { return "Training Center XML"; }
        else { return "file format"; }
    }

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

    public boolean hasData() {
        return this.dataItems.size() > 0;
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

    public int saveData(File file) {
        if (this.hasData()) {

            if (file.getName().toLowerCase().endsWith("." + DataSet.getFileFormatExtension(FileFormat.TCX))) {
                System.out.println("tcx not implemented yet...");
                return -1;

            } else if (file.getName().toLowerCase().endsWith("." + DataSet.getFileFormatExtension(FileFormat.CSV))) {

                try {
                    FileWriter fileWriter = new FileWriter(file);
                    String csvDelimeter = ApplicationProperties.getInstance().getProperty(ApplicationProperty.EXPORT_CSV_DELIMETER);
                    String csvNewLine = ApplicationProperties.getInstance().getProperty(ApplicationProperty.EXPORT_CSV_NEWLINE);

                    fileWriter.append("Timestamp");
                    for(Map.Entry<SensorType, Double> entry : this.dataItems.get(0).getValueMap().entrySet()) {
                        fileWriter.append(csvDelimeter);
                        fileWriter.append(SensorLabel.getInstance().getMap(entry.getKey()).get(SensorLabelItem.NAME));
                    }
                    fileWriter.append(csvNewLine);

                    DateTimeFormatter timestampFormatter =
                            DateTimeFormatter.ofPattern(
                                    ApplicationProperties.getInstance().getProperty(ApplicationProperty.FORMAT_TIMESTAMP_PATTERN))
                                    .withZone(ZoneId.systemDefault());

                    NumberFormat nf = NumberFormat.getInstance(new Locale(
                            ApplicationProperties.getInstance().getProperty(ApplicationProperty.FORMAT_LOCALE_LANGUAGE),
                            ApplicationProperties.getInstance().getProperty(ApplicationProperty.FORMAT_LOCALE_COUNTRY)));

                    nf.setMaximumFractionDigits(2);
                    nf.setMinimumFractionDigits(2);

                    for (int i = 0; i < this.dataItems.size(); i++) {
                        fileWriter.append(timestampFormatter.format(this.dataItems.get(i).getTimestamp()));
                        for(Map.Entry<SensorType, Double> entry : this.dataItems.get(i).getValueMap().entrySet()) {
                            fileWriter.append(csvDelimeter);
                            fileWriter.append(nf.format(entry.getValue()));
                        }
                        fileWriter.append(csvNewLine);
                    }
                    fileWriter.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else {
                return -1;
            }
        }
        return this.dataItems.size();
    }

}
