package org.doubrava.ergologger.bl;

import com.fazecast.jSerialComm.SerialPort;

import java.util.HashMap;

public class DataAdapter_DaumErgoBike8008TRS4 extends DataAdapter {

    private SerialPort serialPort;

    @Override
    public String getName() { return "Daum ErgoBike 8008 TRS4"; }

    // *** Connection *********************************************************

    @Override
    public void openConnection() {
        System.out.println("Serial Port:");

        String portName = ApplicationProperties.getInstance().getProperty(ApplicationProperty.DATA_SERIAL_PORT);
        if (portName != null && !portName.equals("")) {
            System.out.println("  Defined serial port from properties file: " + portName);
        } else {
            try {
                portName = SerialPort.getCommPorts()[0].getPortDescription();
                System.out.println("  No serial port defined in properties file.");
                System.out.println("  Selected [" + portName + "] and saved this in properties file.");
                ApplicationProperties.getInstance().setProperty(
                        ApplicationProperty.DATA_SERIAL_PORT,
                        SerialPort.getCommPorts()[0].getPortDescription());
            } catch (Exception e) {
                System.out.println("  Unable to get any serial port.");
            }
        }

        for (SerialPort port : SerialPort.getCommPorts()) {
            if (port.getPortDescription().equals(portName)) {
                System.out.println("  [x] " + port.getPortDescription());
            } else {
                System.out.println("  [ ] " + port.getPortDescription());
            }
        }


        try {
            this.serialPort = SerialPort.getCommPort(portName);
            this.serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
            this.serialPort.openPort();
            this.isConnected = true;
        } catch (Exception e) {
            System.out.println("  Unable to open serial port.");
            this.isConnected = false;
        }
    }

    @Override
    public boolean testConnection() {

        // Load some data from DAUM: program version, date/time

        this.isConnected = true;
        return true;
    }

    @Override
    public void closeConnection() {
        System.out.println("Serial port [" + this.serialPort.getPortDescription() + "] closing...");
        if (this.serialPort != null) {
            if (this.serialPort.isOpen()) {
                this.serialPort.closePort();
            }
            this.serialPort = null;
        }
        this.isConnected = false;
        System.out.println("Done.");
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
