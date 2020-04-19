package org.doubrava.ergologger.ui;

import org.doubrava.ergologger.bl.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class PanelSensorDisplay extends JPanel {
    private JPanel pnlSensorDisplay;
    private JPanel pnlAverage;
    private JPanel pnlActual;
    private JPanel pnlChartContainer;
    private JLabel lblAverage;
    private JLabel lblAverageValue;
    private JLabel lblAverageUnit;
    private JLabel lblActualValue;
    private JLabel lblActualUnit;
    private JLabel lblActual;
    private JLabel lblSensorType;

    public static int DEFAULT_MAX_CHART_ITEMS = 120;

    private static java.awt.Color DEFAULT_ACT_COLOR = Color.getHSBColor(
            Color.RGBtoHSB(192, 0, 0, null)[0],
            Color.RGBtoHSB(192, 0, 0, null)[1],
            Color.RGBtoHSB(192, 0, 0, null)[2]);

    private static java.awt.Color DEFAULT_AVG_COLOR = Color.getHSBColor(
            Color.RGBtoHSB(0, 112, 192, null)[0],
            Color.RGBtoHSB(0, 112, 192, null)[1],
            Color.RGBtoHSB(0, 112, 192, null)[2]);

    private SensorType sensorType;

    XYSeries chartSeriesAct;
    XYSeries chartSeriesAvg;
    XYSeriesCollection chartCollection;
    private JFreeChart chart;
    private XYLineAndShapeRenderer chartRenderer;
    private ChartPanel chartPanel;

    public PanelSensorDisplay(SensorType sensorType, java.awt.Color bg) {

        this.sensorType = sensorType;
        this.lblSensorType.setText(SensorLabel.getInstance().getMap(sensorType).get(SensorLabelItem.NAME));
        this.lblAverageUnit.setText(SensorLabel.getInstance().getMap(sensorType).get(SensorLabelItem.UNIT));
        this.lblActualUnit.setText(SensorLabel.getInstance().getMap(sensorType).get(SensorLabelItem.UNIT));

        //this.lblActual.setForeground(PanelSensorDisplay.DEFAULT_ACT_COLOR);
        this.lblActualValue.setForeground(PanelSensorDisplay.DEFAULT_ACT_COLOR);
        //this.lblActualUnit.setForeground(PanelSensorDisplay.DEFAULT_ACT_COLOR);

        //this.lblAverage.setForeground(PanelSensorDisplay.DEFAULT_AVG_COLOR);
        this.lblAverageValue.setForeground(PanelSensorDisplay.DEFAULT_AVG_COLOR);
        //this.lblAverageUnit.setForeground(PanelSensorDisplay.DEFAULT_AVG_COLOR);

        this.pnlActual.setBackground(bg);
        this.pnlAverage.setBackground(bg);
        this.pnlChartContainer.setBackground(bg);

        this.chartSeriesAct = new XYSeries("Actual");
        this.chartSeriesAvg = new XYSeries("Average");

        this.chartCollection = new XYSeriesCollection();
        this.chartCollection.addSeries(this.chartSeriesAct);
        this.chartCollection.addSeries(this.chartSeriesAvg);

        this.chart = ChartFactory.createXYLineChart(
                "",
                "",
                "",
                this.chartCollection,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot chartPlot = this.chart.getXYPlot();

        this.chartRenderer = new XYLineAndShapeRenderer();

        this.chartRenderer.setSeriesPaint(0, PanelSensorDisplay.DEFAULT_ACT_COLOR);
        this.chartRenderer.setSeriesStroke(0, new BasicStroke(1.5f));
        this.chartRenderer.setSeriesShapesVisible(0, false);

        this.chartRenderer.setSeriesPaint(1, PanelSensorDisplay.DEFAULT_AVG_COLOR);
        this.chartRenderer.setSeriesStroke(1, new BasicStroke(1.5f));
        this.chartRenderer.setSeriesShapesVisible(1, false);

        chartPlot.setRenderer(this.chartRenderer);
        chartPlot.setBackgroundPaint(null);

        chartPlot.setRangeGridlinesVisible(true);
        chartPlot.setRangeGridlinePaint(Color.BLACK);

        chartPlot.setDomainGridlinesVisible(true);
        chartPlot.setDomainGridlinePaint(Color.BLACK);
        chartPlot.getDomainAxis().setRange(1, PanelSensorDisplay.DEFAULT_MAX_CHART_ITEMS);
        chartPlot.getDomainAxis().setVisible(false);

        this.chart.getLegend().setFrame(BlockBorder.NONE);
        this.chart.getLegend().setVisible(false);
        this.chart.setBackgroundPaint(null);

        this.chartPanel = new ChartPanel(chart);
        this.chartPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        this.chartPanel.setBackground(null);

        this.pnlChartContainer.add(this.chartPanel, BorderLayout.CENTER);

    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorValues(double averageValue, double actualValue,
                                ArrayList<Double> lastActualValues,
                                ArrayList<Double> lastActualAverages) {

        NumberFormat nf = NumberFormat.getInstance(new Locale(
                ApplicationProperties.getInstance().getProperty(ApplicationProperty.FORMAT_LOCALE_LANGUAGE),
                ApplicationProperties.getInstance().getProperty(ApplicationProperty.FORMAT_LOCALE_COUNTRY)));

        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);

        this.lblAverageValue.setText(nf.format(averageValue));
        this.lblActualValue.setText(nf.format(actualValue));

        this.chartSeriesAct.clear();
        if (lastActualValues != null) {
            for (int i = 0; i < lastActualValues.size(); i++) {
                this.chartSeriesAct.add(i, lastActualValues.get(i));
            }
        }

        this.chartSeriesAvg.clear();
        if (lastActualAverages != null) {
            for (int i = 0; i < lastActualAverages.size(); i++) {
                this.chartSeriesAvg.add(i, lastActualAverages.get(i));
            }
        }

    }

}
