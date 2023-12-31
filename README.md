# ErgoLogger
Get current data from your ergomenter, visualize it and create a TCX file to import it in Garmin Connect.

# Libraries
Import using Maven in project structure
> com.fazecast:jSerialComm:2.10.4
> org.jfree:jfreechart:1.5.4

https://fazecast.github.io/jSerialComm/
https://www.jfree.org/jfreechart/

# Enable Serial Port
sudo usermod -a -G uucp username
sudo usermod -a -G dialout username
sudo usermod -a -G lock username
sudo usermod -a -G tty username

# Confuguration file
~/.ergologger.properties

data.serial.port=
data.adapter=Virtual