package org.doubrava.ergologger.ui;

import org.doubrava.ergologger.bl.SensorLabel;
import org.doubrava.ergologger.bl.SensorLabelItem;
import org.doubrava.ergologger.bl.SensorType;

import javax.swing.*;
import java.text.NumberFormat;
import java.util.Locale;

public class PanelSingleKPI extends JPanel {
    private JPanel pnlSingleKPI;
    private JLabel lblName;
    private JLabel lblValue;
    private JLabel lblUnit;

    private SensorType sensorType;

    public PanelSingleKPI(SensorType sensorType) {
        this.sensorType = sensorType;
        this.lblName.setText(SensorLabel.getInstance().getMap(sensorType).get(SensorLabelItem.NAME));
        this.lblUnit.setText(SensorLabel.getInstance().getMap(sensorType).get(SensorLabelItem.UNIT));
    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorValue(double value) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("de", "DE"));
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);

        this.lblValue.setText(nf.format(value));
    }
}
