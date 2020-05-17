package org.doubrava.ergologger.bl;

import com.fazecast.jSerialComm.SerialPort;

import java.math.BigInteger;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class DataAdapter_DaumErgoBike8008TRS4 extends DataAdapter {

    private SerialPort serialPort;
    private static int SERIAL_DELAY_FOR_RESPONSE = 50;
    private byte cockpitAddress = 0;

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
                portName = SerialPort.getCommPorts()[0].getSystemPortName();
                System.out.println("  No serial port defined in properties file.");
                System.out.println("  Selected [" + portName + "] and saved this in properties file.");
                ApplicationProperties.getInstance().setProperty(
                        ApplicationProperty.DATA_SERIAL_PORT,
                        SerialPort.getCommPorts()[0].getSystemPortName());
            } catch (Exception e) {
                System.out.println("  Unable to get any serial port.");
            }
        }

        for (SerialPort port : SerialPort.getCommPorts()) {
            if (port.getSystemPortName().equals(portName)) {
                System.out.println("  [x] " + port.getSystemPortName());
            } else {
                System.out.println("  [ ] " + port.getSystemPortName());
            }
        }


        try {
            this.serialPort = SerialPort.getCommPort(portName);
            this.serialPort.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, 0, 0);
            this.serialPort.openPort();
            this.isConnected = this.serialPort.isOpen();
            if (!this.isConnected) {
                System.out.println("  Serial port not opened.");
            }
        } catch (Exception e) {
            System.out.println("  Unable to open serial port.");
            this.isConnected = false;
        }
    }

    @Override
    public boolean testConnection() {

        // Load some data from DAUM: program version, date/time, ...

        /* Hint Linux: Ensure to grant permissions
        sudo usermod -a -G uucp username
        sudo usermod -a -G dialout username
        sudo usermod -a -G lock username
        sudo usermod -a -G tty username
         */

        /* Reminder: Java's byte is always signed!
        byte b;
        b = -92;
        System.out.println("b = " + b);
        System.out.println("(b < 0 ? 256 + b : b)    " + (b < 0 ? 256 + b : b));
        System.out.println("Byte.toUnsignedInt(b)    " + Byte.toUnsignedInt(b));
        System.out.println("(b & 0xff)               " + (b & 0xff));
        System.out.println("((b << 24) >>> 24)       " + ((b << 24) >>> 24));

         */

        boolean success = true;

        try {
            byte [] command;

            // *** Get address ***
            command = new byte[1];
            command[0] = 0x11;
            this.serialPort.writeBytes(command, command.length);
            Thread.sleep(DataAdapter_DaumErgoBike8008TRS4.SERIAL_DELAY_FOR_RESPONSE);
            if (this.serialPort.bytesAvailable() > 0) {
                byte[] readBuffer = new byte[this.serialPort.bytesAvailable()];
                int numRead = this.serialPort.readBytes(readBuffer, readBuffer.length);

                if (numRead == 2) {
                    this.cockpitAddress = readBuffer[1];
                    System.out.println("Cockpit Address: " + this.cockpitAddress);
                } else {
                    success = false;
                    System.out.println("Unexpected returned byte count.");
                }
            } else {
                success = false;
                System.out.println("No bytes available.");
            }

            // *** Get version ***
            command = new byte[2];
            command[0] = 0x73;
            command[1] = this.cockpitAddress;
            this.serialPort.writeBytes(command, command.length);
            Thread.sleep(DataAdapter_DaumErgoBike8008TRS4.SERIAL_DELAY_FOR_RESPONSE);
            if (this.serialPort.bytesAvailable() > 0) {
                byte[] readBuffer = new byte[this.serialPort.bytesAvailable()];
                int numRead = this.serialPort.readBytes(readBuffer, readBuffer.length);

                if (numRead == 11) {
                    /*
                    System.out.println(numRead + " bytes available.");
                    for (int i = 0; i < readBuffer.length; i++) {
                        System.out.println("  Byte[" + i + "] = " + readBuffer[i] +
                                "    Int: " + Byte.toUnsignedInt(readBuffer[i]) +
                                "    Hex: " + Integer.toHexString(Byte.toUnsignedInt(readBuffer[i])) +
                                "    Char: " + String.valueOf(Character.toChars(Byte.toUnsignedInt(readBuffer[i])))
                        );
                    }
                    */

                    int offset = 32;

                    System.out.println("Cockpit Serial: " +
                            String.valueOf(Character.toChars(Byte.toUnsignedInt(readBuffer[2]) + offset)) +
                            String.valueOf(Character.toChars(Byte.toUnsignedInt(readBuffer[3]) + offset)) +
                            String.valueOf(Character.toChars(Byte.toUnsignedInt(readBuffer[4]) + offset)) +
                            String.valueOf(Character.toChars(Byte.toUnsignedInt(readBuffer[5]) + offset)) +
                            String.valueOf(Character.toChars(Byte.toUnsignedInt(readBuffer[6]) + offset)) +
                            String.valueOf(Character.toChars(Byte.toUnsignedInt(readBuffer[7]) + offset)) +
                            String.valueOf(Character.toChars(Byte.toUnsignedInt(readBuffer[8]) + offset)) +
                            String.valueOf(Character.toChars(Byte.toUnsignedInt(readBuffer[9]) + offset)));

                    offset = -124;
                    switch (Byte.toUnsignedInt(readBuffer[10]) + offset) {
                        case 10:
                            System.out.println("Cockpit Type: Cardio");
                            break;
                        case 20:
                            System.out.println("Cockpit Type: Fitness");
                            break;
                        case 30:
                            System.out.println("Cockpit Type: Vita De Luxe");
                            break;
                        case 40:
                            System.out.println("Cockpit Type: 8008");
                            break;
                        case 50:
                            System.out.println("Cockpit Type: 8080");
                            break;
                        case 60:
                            System.out.println("Cockpit Type: Therapie");
                            break;
                        default:
                            System.out.println("Unknown cockpit type.");
                            break;
                    }

                } else {
                    success = false;
                    System.out.println("Unexpected returned byte count.");
                }
            } else {
                success = false;
                System.out.println("No bytes available.");
            }

            // *** Get cockpit date ***
            command = new byte[2];
            command[0] = 0x65;
            command[1] = this.cockpitAddress;
            this.serialPort.writeBytes(command, command.length);
            Thread.sleep(DataAdapter_DaumErgoBike8008TRS4.SERIAL_DELAY_FOR_RESPONSE);
            if (this.serialPort.bytesAvailable() > 0) {
                byte[] readBuffer = new byte[this.serialPort.bytesAvailable()];
                int numRead = this.serialPort.readBytes(readBuffer, readBuffer.length);

                if (numRead == 5) {
                    System.out.println("Cockpit Date: " +
                            Byte.toUnsignedInt(readBuffer[2]) + "." +
                            Byte.toUnsignedInt(readBuffer[3]) + "." +
                            (Byte.toUnsignedInt(readBuffer[4]) + 2000)
                    );
                } else {
                    success = false;
                    System.out.println("Unexpected returned byte count.");
                }
            } else {
                success = false;
                System.out.println("No bytes available.");
            }

            // *** Get cockpit time ***
            command = new byte[2];
            command[0] = 0x63;
            command[1] = this.cockpitAddress;
            this.serialPort.writeBytes(command, command.length);
            Thread.sleep(DataAdapter_DaumErgoBike8008TRS4.SERIAL_DELAY_FOR_RESPONSE);
            if (this.serialPort.bytesAvailable() > 0) {
                byte[] readBuffer = new byte[this.serialPort.bytesAvailable()];
                int numRead = this.serialPort.readBytes(readBuffer, readBuffer.length);

                if (numRead == 5) {
                    System.out.println("Cockpit Time: " +
                            Byte.toUnsignedInt(readBuffer[4]) + ":" +
                            Byte.toUnsignedInt(readBuffer[3]) + ":" +
                            Byte.toUnsignedInt(readBuffer[2])
                    );
                } else {
                    success = false;
                    System.out.println("Unexpected returned byte count.");
                }
            } else {
                success = false;
                System.out.println("No bytes available.");
            }

            // *** Get current program ***
            command = new byte[2];
            command[0] = 0x66;
            command[1] = this.cockpitAddress;
            this.serialPort.writeBytes(command, command.length);
            Thread.sleep(DataAdapter_DaumErgoBike8008TRS4.SERIAL_DELAY_FOR_RESPONSE);
            if (this.serialPort.bytesAvailable() > 0) {
                byte[] readBuffer = new byte[this.serialPort.bytesAvailable()];
                int numRead = this.serialPort.readBytes(readBuffer, readBuffer.length);

                if (numRead == 3) {
                    System.out.println("Current Program: " + Byte.toUnsignedInt(readBuffer[2]));
                } else {
                    success = false;
                    System.out.println("Unexpected returned byte count.");
                }
            } else {
                success = false;
                System.out.println("No bytes available.");
            }

            // *** Get current person ***
            command = new byte[2];
            command[0] = 0x67;
            command[1] = this.cockpitAddress;
            this.serialPort.writeBytes(command, command.length);
            Thread.sleep(DataAdapter_DaumErgoBike8008TRS4.SERIAL_DELAY_FOR_RESPONSE);
            if (this.serialPort.bytesAvailable() > 0) {
                byte[] readBuffer = new byte[this.serialPort.bytesAvailable()];
                int numRead = this.serialPort.readBytes(readBuffer, readBuffer.length);

                if (numRead == 3) {
                    System.out.println("Current Person: " + Byte.toUnsignedInt(readBuffer[2]));
                } else {
                    success = false;
                    System.out.println("Unexpected returned byte count.");
                }
            } else {
                success = false;
                System.out.println("No bytes available.");
            }

        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }

        if (!success) {
            System.out.println("Hint: If no bytes are available, check, if you are in an program.");
            System.out.println("      For example, if you are in a menu, data is not provided by the device.");
            System.out.println("      Pressing the Menu-Button on the device and enter a program may help you.");
            this.closeConnection();
        }

        return success;
    }

    @Override
    public void closeConnection() {
        System.out.println("Serial port [" + this.serialPort.getSystemPortName() + "] closing...");
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

        boolean success = true;

        try {

            byte [] command = new byte[2];
            command[0] = 0x40;
            command[1] = this.cockpitAddress;
            this.serialPort.writeBytes(command, command.length);
            Thread.sleep(DataAdapter_DaumErgoBike8008TRS4.SERIAL_DELAY_FOR_RESPONSE);
            if (this.serialPort.bytesAvailable() > 0) {
                byte[] readBuffer = new byte[this.serialPort.bytesAvailable()];
                int numRead = this.serialPort.readBytes(readBuffer, readBuffer.length);

                if (numRead == 19) {
                    this.currentTimestamp = Instant.now();
                    this.currentValueMap.clear();
                    // Return: HRF valid range [0..199]
                    // No convertation necessary
                    this.currentValueMap.put(SensorType.HRF, (double)Byte.toUnsignedInt(readBuffer[14]));

                    // Return: Power divided by 5.
                    // Valid range [5..80] (=25..400) or [10..160] (=50..800) depending on device
                    // Convert in Watt multiplication with 5
                    this.currentValueMap.put(SensorType.POWER, (double)(Byte.toUnsignedInt(readBuffer[5]) * 5));

                    // Return: RPM valid range [0..199]
                    // No convertation necessary
                    this.currentValueMap.put(SensorType.RPM, (double)Byte.toUnsignedInt(readBuffer[6]));

                    // Return speed in kilometer per hour (before comma); max. 99
                    // No convertation necessary
                    this.currentValueMap.put(SensorType.SPEED, (double)Byte.toUnsignedInt(readBuffer[7]));

                    // Return duration (cycling) in seconds =>Byte 10..11
                    // Byte 10: Counter 0 .. 255 >Starts at 0, when end reached
                    // Byte 11: Counter each 255. block of byte 10
                    // No convertation necessary
                    this.currentValueMap.put(SensorType.DURATION, (double)(
                            Byte.toUnsignedInt(readBuffer[10]) +
                            Byte.toUnsignedInt(readBuffer[11]) * 255));

                    // Return distance in 100 m =>Byte 8..9
                    // Follows sabe logic like [duration]
                    // Convert in meteres.
                    this.currentValueMap.put(SensorType.DISTANCE, (double)(
                            Byte.toUnsignedInt(readBuffer[8]) * 100 +
                            Byte.toUnsignedInt(readBuffer[9]) * 100 * 255));

                    // Return calories in 100 Joule =>Byte 12..13
                    // Convert in Calories ( 1 Joule / 4.184 Calories )
                    this.currentValueMap.put(SensorType.CALORIES, (double)((
                            Byte.toUnsignedInt(readBuffer[12]) * 100 +
                            Byte.toUnsignedInt(readBuffer[13]) * 100 * 255))
                            / 4.184);

                } else {
                    success = false;
                    System.out.println("Unexpected returned byte count.");
                }
            } else {
                success = false;
                System.out.println("No bytes available.");
            }

        } catch (Exception e) {
            success = false;
            e.printStackTrace();
        }

    }
}
