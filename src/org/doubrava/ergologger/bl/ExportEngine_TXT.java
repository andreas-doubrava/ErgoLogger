package org.doubrava.ergologger.bl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ExportEngine_TXT implements FileExportEngine {

    private static final String FILEFORMAT_EXTENSION = "txt";
    private static final String FILEFORMAT_NAME = "Text (Tabstop-separated)";

    @Override
    public String getFileFormatExtension() {
        return ExportEngine_TXT.FILEFORMAT_EXTENSION;
    }

    @Override
    public String getFileFormatName() {
        return ExportEngine_TXT.FILEFORMAT_NAME;
    }

    @Override
    public int export(DataAdapter dataAdapter, DataSet dataSet, File file) {
        if (dataSet.hasData()) {

            try {
                FileWriter fileWriter = new FileWriter(file);
                String txtDelimeter = ApplicationProperties.getInstance().getProperty(ApplicationProperty.EXPORT_TXT_DELIMETER);
                String txtNewLine = ApplicationProperties.getInstance().getProperty(ApplicationProperty.EXPORT_TXT_NEWLINE);

                fileWriter.append("Timestamp");

                if (dataAdapter.getSensorTypeAvailability().get(SensorType.DURATION)) {
                    fileWriter.append(txtDelimeter);
                    fileWriter.append(SensorLabel.getInstance().getMap(SensorType.DURATION).get(SensorLabelItem.NAME));
                }
                if (dataAdapter.getSensorTypeAvailability().get(SensorType.DISTANCE)) {
                    fileWriter.append(txtDelimeter);
                    fileWriter.append(SensorLabel.getInstance().getMap(SensorType.DISTANCE).get(SensorLabelItem.NAME));
                }
                if (dataAdapter.getSensorTypeAvailability().get(SensorType.SPEED)) {
                    fileWriter.append(txtDelimeter);
                    fileWriter.append(SensorLabel.getInstance().getMap(SensorType.SPEED).get(SensorLabelItem.NAME));
                }
                if (dataAdapter.getSensorTypeAvailability().get(SensorType.HRF)) {
                    fileWriter.append(txtDelimeter);
                    fileWriter.append(SensorLabel.getInstance().getMap(SensorType.HRF).get(SensorLabelItem.NAME));
                }
                if (dataAdapter.getSensorTypeAvailability().get(SensorType.RPM)) {
                    fileWriter.append(txtDelimeter);
                    fileWriter.append(SensorLabel.getInstance().getMap(SensorType.RPM).get(SensorLabelItem.NAME));
                }
                if (dataAdapter.getSensorTypeAvailability().get(SensorType.POWER)) {
                    fileWriter.append(txtDelimeter);
                    fileWriter.append(SensorLabel.getInstance().getMap(SensorType.POWER).get(SensorLabelItem.NAME));
                }
                if (dataAdapter.getSensorTypeAvailability().get(SensorType.CALORIES)) {
                    fileWriter.append(txtDelimeter);
                    fileWriter.append(SensorLabel.getInstance().getMap(SensorType.CALORIES).get(SensorLabelItem.NAME));
                }
                /*
                // Problem: unsorted...
                for(Map.Entry<SensorType, Double> entry : this.dataItems.get(0).getValueMap().entrySet()) {
                    fileWriter.append(txtDelimeter);
                    fileWriter.append(SensorLabel.getInstance().getMap(entry.getKey()).get(SensorLabelItem.NAME));
                }
                */
                fileWriter.append(txtNewLine);

                DateTimeFormatter timestampFormatter =
                        DateTimeFormatter.ofPattern(
                                ApplicationProperties.getInstance().getProperty(ApplicationProperty.FORMAT_TIMESTAMP_PATTERN))
                                .withZone(ZoneId.systemDefault());

                NumberFormat nf = NumberFormat.getInstance(new Locale(
                        ApplicationProperties.getInstance().getProperty(ApplicationProperty.FORMAT_LOCALE_LANGUAGE),
                        ApplicationProperties.getInstance().getProperty(ApplicationProperty.FORMAT_LOCALE_COUNTRY)));

                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);

                for (int i = 0; i < dataSet.getItemCount(); i++) {
                    fileWriter.append(timestampFormatter.format(dataSet.getItem(i).getTimestamp()));

                    if (dataAdapter.getSensorTypeAvailability().get(SensorType.DURATION)) {
                        fileWriter.append(txtDelimeter);
                        fileWriter.append(nf.format(dataSet.getItem(i).getValue(SensorType.DURATION)));
                    }
                    if (dataAdapter.getSensorTypeAvailability().get(SensorType.DISTANCE)) {
                        fileWriter.append(txtDelimeter);
                        fileWriter.append(nf.format(dataSet.getItem(i).getValue(SensorType.DISTANCE)));
                    }
                    if (dataAdapter.getSensorTypeAvailability().get(SensorType.SPEED)) {
                        fileWriter.append(txtDelimeter);
                        fileWriter.append(nf.format(dataSet.getItem(i).getValue(SensorType.SPEED)));
                    }
                    if (dataAdapter.getSensorTypeAvailability().get(SensorType.HRF)) {
                        fileWriter.append(txtDelimeter);
                        fileWriter.append(nf.format(dataSet.getItem(i).getValue(SensorType.HRF)));
                    }
                    if (dataAdapter.getSensorTypeAvailability().get(SensorType.RPM)) {
                        fileWriter.append(txtDelimeter);
                        fileWriter.append(nf.format(dataSet.getItem(i).getValue(SensorType.RPM)));
                    }
                    if (dataAdapter.getSensorTypeAvailability().get(SensorType.POWER)) {
                        fileWriter.append(txtDelimeter);
                        fileWriter.append(nf.format(dataSet.getItem(i).getValue(SensorType.POWER)));
                    }
                    if (dataAdapter.getSensorTypeAvailability().get(SensorType.CALORIES)) {
                        fileWriter.append(txtDelimeter);
                        fileWriter.append(nf.format(dataSet.getItem(i).getValue(SensorType.CALORIES)));
                    }
                    /*
                    // Problem: unsorted
                    for(Map.Entry<SensorType, Double> entry : this.dataItems.get(i).getValueMap().entrySet()) {
                        fileWriter.append(txtDelimeter);
                        fileWriter.append(nf.format(entry.getValue()));
                    }
                    */
                    fileWriter.append(txtNewLine);
                }
                fileWriter.close();

            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }

        }
        return dataSet.getItemCount();
    }
}
