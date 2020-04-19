package org.doubrava.ergologger.ui;

import org.doubrava.ergologger.bl.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;

public class FormErgoLogger extends JPanel implements ClockObserver, DataObserver, ActionListener {
    private JPanel pnlMain;
    private JCheckBox chkConnected;
    private JPanel pnlFooter;
    private JLabel lblStatusbar;
    private JButton btnStart;
    private JButton btnPause;
    private JButton btnRestart;
    private JButton btnStop;
    private JPanel pnlCommand;
    private JPanel pnlDisplayContainer;
    private PanelSensorDisplay panelSensorDisplay1;
    private PanelSensorDisplay panelSensorDisplay2;
    private PanelSensorDisplay panelSensorDisplay3;
    private PanelSensorDisplay panelSensorDisplay4;
    private JPanel pnlHeader;
    private JLabel lblDuration;
    private JPanel pnlSingleKPIContainer;
    private PanelSingleKPI panelSingleKPI1;
    private PanelSingleKPI panelSingleKPI2;
    private JPanel pnlClock;
    private JLabel lblClockDate;
    private JLabel lblClockTime;
    private JButton btnConnect;
    private JButton btnDisconnect;
    private JButton btnSave;
    private JLabel lblClock;

    private static java.awt.Color DEFAULT_SPD_COLOR = Color.getHSBColor(
            Color.RGBtoHSB(209, 235, 255, null)[0],
            Color.RGBtoHSB(209, 235, 255, null)[1],
            Color.RGBtoHSB(209, 235, 255, null)[2]);

    private static java.awt.Color DEFAULT_HRF_COLOR = Color.getHSBColor(
            Color.RGBtoHSB(238, 200, 200, null)[0],
            Color.RGBtoHSB(238, 200, 200, null)[1],
            Color.RGBtoHSB(238, 200, 200, null)[2]);

    private static java.awt.Color DEFAULT_RPM_COLOR = Color.getHSBColor(
            Color.RGBtoHSB(244, 244, 224, null)[0],
            Color.RGBtoHSB(244, 244, 224, null)[1],
            Color.RGBtoHSB(244, 244, 224, null)[2]);

    private static java.awt.Color DEFAULT_POW_COLOR = Color.getHSBColor(
            Color.RGBtoHSB(252, 216, 245, null)[0],
            Color.RGBtoHSB(252, 216, 245, null)[1],
            Color.RGBtoHSB(252, 216, 245, null)[2]);

    private Clock clock;
    private DataAdapter dataAdapter;
    private DataSet dataSet;

    public JPanel getMainPanel() {return this.pnlMain; }

    public FormErgoLogger() {

        this.clock = new Clock();
        this.clock.registerObserver(this);

        Thread clockThread = new Thread(this.clock);
        clockThread.start();

        this.dataAdapter = new VirtualDataAdapter();
        this.dataSet = new DataSet(ActivityType.BIKING);

        this.dataAdapter.registerObserver(dataSet);
        this.dataAdapter.registerObserver(this);

        this.btnStart.addActionListener(this);
        this.btnPause.addActionListener(this);
        this.btnRestart.addActionListener(this);
        this.btnStop.addActionListener(this);
        this.btnConnect.addActionListener(this);
        this.btnDisconnect.addActionListener(this);
        this.btnSave.addActionListener(this);

        this.btnConnect.setEnabled(true);
        this.btnStart.setEnabled(false);
        this.btnPause.setEnabled(false);
        this.btnRestart.setEnabled(false);
        this.btnStop.setEnabled(false);
        this.btnDisconnect.setEnabled(false);
        this.btnSave.setEnabled(false);

        this.panelSingleKPI1.setSensorValue(0.0);
        this.panelSingleKPI2.setSensorValue(0.0);
        this.panelSensorDisplay1.setSensorValues(0.0, 0.0, null, null);
        this.panelSensorDisplay2.setSensorValues(0.0, 0.0, null, null);
        this.panelSensorDisplay3.setSensorValues(0.0, 0.0, null, null);
        this.panelSensorDisplay4.setSensorValues(0.0, 0.0, null, null);
    }

    private void createUIComponents() {
        this.panelSingleKPI1 = new PanelSingleKPI(SensorType.DISTANCE);
        this.panelSingleKPI2 = new PanelSingleKPI(SensorType.CALORIES);

        this.panelSensorDisplay1 = new PanelSensorDisplay(SensorType.SPEED, FormErgoLogger.DEFAULT_SPD_COLOR);
        this.panelSensorDisplay2 = new PanelSensorDisplay(SensorType.HRF, FormErgoLogger.DEFAULT_HRF_COLOR);
        this.panelSensorDisplay3 = new PanelSensorDisplay(SensorType.RPM, FormErgoLogger.DEFAULT_RPM_COLOR);
        this.panelSensorDisplay4 = new PanelSensorDisplay(SensorType.POWER, FormErgoLogger.DEFAULT_POW_COLOR);
    }

    @Override
    public void updateClock(Instant timestamp) {
        DateTimeFormatter dateFormatter =
                DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                        .withLocale(Locale.GERMANY)
                        .withZone(ZoneId.systemDefault());

        DateTimeFormatter timeFormatter =
                DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
                        .withLocale(Locale.GERMANY)
                        .withZone(ZoneId.systemDefault());

        this.lblClockDate.setText(dateFormatter.format(timestamp));
        this.lblClockTime.setText(timeFormatter.format(timestamp));
    }

    @Override
    public void updateData(Instant timestamp, HashMap<SensorType, Double> valueMap) {
        Duration diff = this.dataSet.getDuration(true);
        LocalTime time = LocalTime.ofNanoOfDay(diff.toNanos());
        DateTimeFormatter timeFormatter =
                DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM)
                        .withLocale(Locale.GERMANY)
                        .withZone(ZoneId.systemDefault());
        this.lblDuration.setText(timeFormatter.format(time));

        this.panelSingleKPI1.setSensorValue(
                this.dataSet.getLastValue(this.panelSingleKPI1.getSensorType()));

        this.panelSingleKPI2.setSensorValue(
                this.dataSet.getLastValue(this.panelSingleKPI2.getSensorType()));

        this.panelSensorDisplay1.setSensorValues(
                this.dataSet.getAverageValue(this.panelSensorDisplay1.getSensorType()),
                this.dataSet.getLastValue(this.panelSensorDisplay1.getSensorType()),
                this.dataSet.getDataItemValues(this.panelSensorDisplay1.getSensorType(), PanelSensorDisplay.DEFAULT_MAX_CHART_ITEMS),
                this.dataSet.getDataItemAverages(this.panelSensorDisplay1.getSensorType(), PanelSensorDisplay.DEFAULT_MAX_CHART_ITEMS));

        this.panelSensorDisplay2.setSensorValues(
                this.dataSet.getAverageValue(this.panelSensorDisplay2.getSensorType()),
                this.dataSet.getLastValue(this.panelSensorDisplay2.getSensorType()),
                this.dataSet.getDataItemValues(this.panelSensorDisplay2.getSensorType(), PanelSensorDisplay.DEFAULT_MAX_CHART_ITEMS),
                this.dataSet.getDataItemAverages(this.panelSensorDisplay2.getSensorType(), PanelSensorDisplay.DEFAULT_MAX_CHART_ITEMS));

        this.panelSensorDisplay3.setSensorValues(
                this.dataSet.getAverageValue(this.panelSensorDisplay3.getSensorType()),
                this.dataSet.getLastValue(this.panelSensorDisplay3.getSensorType()),
                this.dataSet.getDataItemValues(this.panelSensorDisplay3.getSensorType(), PanelSensorDisplay.DEFAULT_MAX_CHART_ITEMS),
                this.dataSet.getDataItemAverages(this.panelSensorDisplay3.getSensorType(), PanelSensorDisplay.DEFAULT_MAX_CHART_ITEMS));

        this.panelSensorDisplay4.setSensorValues(
                this.dataSet.getAverageValue(this.panelSensorDisplay4.getSensorType()),
                this.dataSet.getLastValue(this.panelSensorDisplay4.getSensorType()),
                this.dataSet.getDataItemValues(this.panelSensorDisplay4.getSensorType(), PanelSensorDisplay.DEFAULT_MAX_CHART_ITEMS),
                this.dataSet.getDataItemAverages(this.panelSensorDisplay4.getSensorType(), PanelSensorDisplay.DEFAULT_MAX_CHART_ITEMS));
    }

    @Override
    public void onStart() {}

    @Override
    public void onPause() {}

    @Override
    public void onRestart() {}

    @Override
    public void onStop() {}

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.btnConnect) {
            if (this.dataAdapter.isConnected()) {
                this.lblStatusbar.setText("Connection: You are already connected.");
            } else {
                this.dataAdapter.openConnection();
                if (this.dataAdapter.testConnection()) {
                    this.lblStatusbar.setText("Connection: OK.");

                    this.btnConnect.setEnabled(false);
                    this.btnStart.setEnabled(true);
                    this.btnPause.setEnabled(false);
                    this.btnRestart.setEnabled(false);
                    this.btnStop.setEnabled(false);
                    this.btnDisconnect.setEnabled(true);
                    this.btnSave.setEnabled(false);

                } else {
                    this.lblStatusbar.setText("Connection: Failed!");
                }
            }

        } else if (e.getSource() == this.btnStart) {
            if (this.dataAdapter.isConnected()) {
                if (this.dataAdapter.isRunning()) {
                    this.lblStatusbar.setText("Start: Service is already running.");
                } else {
                    Thread dataAdapterThread = new Thread(this.dataAdapter);
                    dataAdapterThread.start();
                    this.dataAdapter.startService();
                    this.lblStatusbar.setText("Start: OK");

                    this.btnConnect.setEnabled(false);
                    this.btnStart.setEnabled(false);
                    this.btnPause.setEnabled(true);
                    this.btnRestart.setEnabled(false);
                    this.btnStop.setEnabled(true);
                    this.btnDisconnect.setEnabled(false);
                    this.btnSave.setEnabled(false);

                }
            } else {
                this.lblStatusbar.setText("Start: Service not connected. Press [Connect] to open connection.");
            }

        } else if (e.getSource() == this.btnPause) {
            if (this.dataAdapter.isConnected()) {
                if (this.dataAdapter.isRunning()) {
                    if (this.dataAdapter.isActive()) {
                        this.dataAdapter.pauseService();
                        this.lblStatusbar.setText("Pause: OK. Press [Restart] to continue.");

                        this.btnConnect.setEnabled(false);
                        this.btnStart.setEnabled(false);
                        this.btnPause.setEnabled(false);
                        this.btnRestart.setEnabled(true);
                        this.btnStop.setEnabled(false);
                        this.btnDisconnect.setEnabled(false);
                        this.btnSave.setEnabled(false);

                    } else {
                        this.lblStatusbar.setText("Pause: Service is already sleeping. Press [Restart] to continue.");
                    }
                } else {
                    this.lblStatusbar.setText("Pause: Service is not running. Press [Start] to run service.");
                }
            } else {
                this.lblStatusbar.setText("Pause: Service not connected. Press [Connect] to open connection.");
            }

        } else if (e.getSource() == this.btnRestart) {
            if (this.dataAdapter.isConnected()) {
                if (this.dataAdapter.isRunning()) {
                    if (this.dataAdapter.isActive()) {
                        this.lblStatusbar.setText("Restart: Service is already running.");
                    } else {
                        this.dataAdapter.restartService();
                        this.lblStatusbar.setText("Restart: OK");

                        this.btnConnect.setEnabled(false);
                        this.btnStart.setEnabled(false);
                        this.btnPause.setEnabled(true);
                        this.btnRestart.setEnabled(false);
                        this.btnStop.setEnabled(true);
                        this.btnDisconnect.setEnabled(false);
                        this.btnSave.setEnabled(false);

                    }
                } else {
                    this.lblStatusbar.setText("Restart: Service is not running. Press [Start] to run service.");
                }
            } else {
                this.lblStatusbar.setText("Restart: Service not connected. Press [Connect] to open connection.");
            }

        } else if (e.getSource() == this.btnStop) {
            if (this.dataAdapter.isConnected()) {
                if (this.dataAdapter.isRunning()) {
                    this.dataAdapter.stopService();
                    this.lblStatusbar.setText("Stop: OK");

                    this.btnConnect.setEnabled(false);
                    this.btnStart.setEnabled(true);
                    this.btnPause.setEnabled(false);
                    this.btnRestart.setEnabled(false);
                    this.btnStop.setEnabled(false);
                    this.btnDisconnect.setEnabled(true);
                    this.btnSave.setEnabled(this.dataSet.hasData());

                } else {
                    this.lblStatusbar.setText("Stop: Service is already stopped.");
                }
            } else {
                this.lblStatusbar.setText("Stop: Service not connected. Press [Connect] to open connection.");
            }

        } else if (e.getSource() == this.btnDisconnect) {
            if (this.dataAdapter.isConnected()) {
                if (this.dataAdapter.isRunning()) {
                    this.lblStatusbar.setText("Disconnect: Service is still running. Press [Stop] to quit service.");
                } else {
                    this.dataAdapter.closeConnection();
                    this.lblStatusbar.setText("Disconnect: OK.");

                    this.btnConnect.setEnabled(true);
                    this.btnStart.setEnabled(false);
                    this.btnPause.setEnabled(false);
                    this.btnRestart.setEnabled(false);
                    this.btnStop.setEnabled(false);
                    this.btnDisconnect.setEnabled(false);
                    this.btnSave.setEnabled(this.dataSet.hasData());

                }
            } else {
                this.lblStatusbar.setText("Disconnect: Service already disconnected.");
            }
        } else if (e.getSource() == this.btnSave) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Specify a file to save");
            for (FileFormat fileFormat : FileFormat.values()) {
                FileFilter filterTCX = new FileNameExtensionFilter(
                        DataSet.getFileFormatName(fileFormat),
                        DataSet.getFileFormatExtension(fileFormat));

                fileChooser.addChoosableFileFilter(filterTCX);
            }
            fileChooser.setAcceptAllFileFilterUsed(false);
            fileChooser.setCurrentDirectory(new File(ApplicationProperties.getInstance().getProperty(ApplicationProperty.EXPORT_DIRECTORY)));

            int userSelection = fileChooser.showSaveDialog(this);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();

                if (fileChooser.getFileFilter() instanceof FileNameExtensionFilter) {
                    boolean hasValidExt = false;

                    String[] extensions = ((FileNameExtensionFilter)fileChooser.getFileFilter()).getExtensions();
                    String nameLower = fileToSave.getName().toLowerCase();
                    // check if it already has a valid extension
                    for (String ext : extensions) {
                        if (nameLower.endsWith('.' + ext.toLowerCase())) {
                            hasValidExt = true;
                        }
                    }
                    // if not, append the first extension from the selected filter
                    if (!hasValidExt) {
                        fileToSave = new File(fileToSave.toString() + '.' + extensions[0]);
                    }
                }
                ApplicationProperties.getInstance().setProperty(ApplicationProperty.EXPORT_DIRECTORY, fileToSave.getParent());
                ApplicationProperties.getInstance().saveProperties();
                this.dataSet.saveData(fileToSave);
            }
        }
    }
}
