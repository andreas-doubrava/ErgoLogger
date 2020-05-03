package org.doubrava.ergologger.bl;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class ExportEngine_TCX implements FileExportEngine {

    private static final String FILEFORMAT_EXTENSION = "tcx";
    private static final String FILEFORMAT_NAME = "Training Center XML";

    @Override
    public String getFileFormatExtension() {
        return ExportEngine_TCX.FILEFORMAT_EXTENSION;
    }

    @Override
    public String getFileFormatName() {
        return ExportEngine_TCX.FILEFORMAT_NAME;
    }


    @Override
    public int export(DataAdapter dataAdapter, DataSet dataSet, File file) {
        if (dataSet.hasData()) {

            DateTimeFormatter dtf = DateTimeFormatter
                    .ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .withLocale(Locale.US)
                    .withZone(ZoneId.systemDefault());

            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            nf.setGroupingUsed(false);

            try {

                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
                Document document = documentBuilder.newDocument();

                // -----------------------------------------------------------------------------------------------------
                // root element
                Element root = document.createElement("TrainingCenterDatabase");

                root.setAttribute("xmlns", "http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2");
                root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                root.setAttribute("xsi:schemaLocation", "http://www.garmin.com/xmlschemas/TrainingCenterDatabase/v2 http://www.garmin.com/xmlschemas/TrainingCenterDatabasev2.xsd");
                root.setAttribute("xmlns:ns2", "http://www.garmin.com/xmlschemas/UserProfile/v2");
                root.setAttribute("xmlns:ns3", "http://www.garmin.com/xmlschemas/ActivityExtension/v2");
                root.setAttribute("xmlns:ns4", "http://www.garmin.com/xmlschemas/ProfileExtension/v1");
                root.setAttribute("xmlns:ns5", "http://www.garmin.com/xmlschemas/ActivityGoals/v1");

                document.appendChild(root);

                Element activities = document.createElement("Activities");
                root.appendChild(activities);

                Element activity = document.createElement("Activity");
                activity.setAttribute("Sport", dataSet.getActivityTypeName());
                activities.appendChild(activity);

                Element id = document.createElement("Id");
                id.appendChild(document.createTextNode(dtf.format(dataSet.getLastTimestamp())));
                activity.appendChild(id);

                Element lap = document.createElement("Lap");
                lap.setAttribute("StartTime",dtf.format(dataSet.getFirstTimestamp()));
                activity.appendChild(lap);

                nf.setMaximumFractionDigits(1);
                nf.setMinimumFractionDigits(1);

                Element totalTimeSeconds = document.createElement("TotalTimeSeconds");
                totalTimeSeconds.appendChild(document.createTextNode(nf.format(dataSet.getDuration(true).getSeconds())));
                lap.appendChild(totalTimeSeconds);

                Element distanceMeters = document.createElement("DistanceMeters");
                distanceMeters.appendChild(document.createTextNode(nf.format(dataSet.getLastValue(SensorType.DISTANCE))));
                lap.appendChild(distanceMeters);

                // Hint: TCX requires speed in [m/s].
                // Since speed sensor tracks [km/h], the value has to be divided by 3.6
                Element maximumSpeed = document.createElement("MaximumSpeed");
                maximumSpeed.appendChild(document.createTextNode(nf.format(dataSet.getMaxValue(SensorType.SPEED) / 3.6)));
                lap.appendChild(maximumSpeed);

                nf.setMaximumFractionDigits(0);
                nf.setMinimumFractionDigits(0);

                Element calories = document.createElement("Calories");
                calories.appendChild(document.createTextNode(nf.format(dataSet.getLastValue(SensorType.CALORIES))));
                lap.appendChild(calories);

                Element averageHeartRateBpm = document.createElement("AverageHeartRateBpm");
                Element averageHeartRateBpm_Value = document.createElement("Value");
                averageHeartRateBpm_Value.appendChild(document.createTextNode(nf.format(dataSet.getAverageValue(SensorType.HRF))));
                averageHeartRateBpm.appendChild(averageHeartRateBpm_Value);
                lap.appendChild(averageHeartRateBpm);

                Element maximumHeartRateBpm = document.createElement("MaximumHeartRateBpm");
                Element maximumHeartRateBpm_Value = document.createElement("Value");
                maximumHeartRateBpm_Value.appendChild(document.createTextNode(nf.format(dataSet.getMaxValue(SensorType.HRF))));
                maximumHeartRateBpm.appendChild(maximumHeartRateBpm_Value);
                lap.appendChild(maximumHeartRateBpm);

                Element intensity = document.createElement("Intensity");
                intensity.appendChild(document.createTextNode("Active"));
                lap.appendChild(intensity);

                Element cadence = document.createElement("Cadence");
                cadence.appendChild(document.createTextNode(nf.format(dataSet.getAverageValue(SensorType.RPM))));
                lap.appendChild(cadence);

                Element triggerMethod = document.createElement("TriggerMethod");
                triggerMethod.appendChild(document.createTextNode("Manual"));
                lap.appendChild(triggerMethod);

                Element track = document.createElement("Track");
                lap.appendChild(track);

                // +++ Add Items (Trackpoints) +++

                for (int i = 0; i < dataSet.getItemCount(); i++) {
                    Element trackpoint = document.createElement("Trackpoint");
                    track.appendChild(trackpoint);

                    Element tp_time = document.createElement("Time");
                    tp_time.appendChild(document.createTextNode(dtf.format(dataSet.getItem(i).getTimestamp())));
                    trackpoint.appendChild(tp_time);

                    nf.setMaximumFractionDigits(1);
                    nf.setMinimumFractionDigits(1);

                    Element tp_distanceMeters = document.createElement("DistanceMeters");
                    tp_distanceMeters.appendChild(document.createTextNode(nf.format(dataSet.getItem(i).getValue(SensorType.DISTANCE))));
                    trackpoint.appendChild(tp_distanceMeters);

                    nf.setMaximumFractionDigits(0);
                    nf.setMinimumFractionDigits(0);

                    Element tp_heartRateBpm = document.createElement("HeartRateBpm");
                    Element tp_heartRateBpm_value = document.createElement("Value");
                    tp_heartRateBpm_value.appendChild(document.createTextNode(nf.format(dataSet.getItem(i).getValue(SensorType.HRF))));
                    tp_heartRateBpm.appendChild(tp_heartRateBpm_value);
                    trackpoint.appendChild(tp_heartRateBpm);

                    Element tp_cadence = document.createElement("Cadence");
                    tp_cadence.appendChild(document.createTextNode(nf.format(dataSet.getItem(i).getValue(SensorType.RPM))));
                    trackpoint.appendChild(tp_cadence);

                    Element tp_extensions = document.createElement("Extensions");
                    Element tp_ns3_tpx = document.createElement("ns3:TPX");

                    nf.setMaximumFractionDigits(2);
                    nf.setMinimumFractionDigits(2);

                    Element tp_ns3_tpx_speed = document.createElement("ns3:Speed");
                    tp_ns3_tpx_speed.appendChild(document.createTextNode(nf.format(dataSet.getItem(i).getValue(SensorType.SPEED) / 3.6)));

                    nf.setMaximumFractionDigits(0);
                    nf.setMinimumFractionDigits(0);

                    Element tp_ns3_tpx_watts = document.createElement("ns3:Watts");
                    tp_ns3_tpx_watts.appendChild(document.createTextNode(nf.format(dataSet.getItem(i).getValue(SensorType.POWER))));

                    tp_ns3_tpx.appendChild(tp_ns3_tpx_speed);
                    tp_ns3_tpx.appendChild(tp_ns3_tpx_watts);
                    tp_extensions.appendChild(tp_ns3_tpx);
                    trackpoint.appendChild(tp_extensions);
                }

                // +++ Add Extensions +++

                Element extensions = document.createElement("Extensions");
                Element ns3_LX = document.createElement("ns3:LX");

                nf.setMaximumFractionDigits(2);
                nf.setMinimumFractionDigits(2);

                Element ns3_AvgSpeed = document.createElement("ns3:AvgSpeed");
                ns3_AvgSpeed.appendChild(document.createTextNode(nf.format(dataSet.getAverageValue(SensorType.SPEED) / 3.6)));

                nf.setMaximumFractionDigits(0);
                nf.setMinimumFractionDigits(0);

                Element ns3_MaxBikeCadence = document.createElement("ns3:MaxBikeCadence");
                ns3_MaxBikeCadence.appendChild(document.createTextNode(nf.format(dataSet.getMaxValue(SensorType.RPM))));

                Element ns3_AvgWatt = document.createElement("ns3:AvgWatts");
                ns3_AvgWatt.appendChild(document.createTextNode(nf.format(dataSet.getAverageValue(SensorType.POWER))));

                Element ns3_MaxWatts = document.createElement("ns3:MaxWatts");
                ns3_MaxWatts.appendChild(document.createTextNode(nf.format(dataSet.getMaxValue(SensorType.POWER))));

                ns3_LX.appendChild(ns3_AvgSpeed);
                ns3_LX.appendChild(ns3_MaxBikeCadence);
                ns3_LX.appendChild(ns3_AvgWatt);
                ns3_LX.appendChild(ns3_MaxWatts);

                extensions.appendChild(ns3_LX);
                lap.appendChild(extensions);

                // -----------------------------------------------------------------------------------------------------
                // create the xml file
                // transform the DOM Object to an XML File
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                DOMSource domSource = new DOMSource(document);
                StreamResult streamResult = new StreamResult(file);

                // If you use
                // StreamResult result = new StreamResult(System.out);
                // the output will be pushed to the standard output ...
                // You can use that for debugging

                transformer.transform(domSource, streamResult);

            } catch (ParserConfigurationException | TransformerException e) {
                e.printStackTrace();
                return -1;
            }
        }
        return dataSet.getItemCount();
    }
}
