package org.doubrava.ergologger.ui;

import org.doubrava.ergologger.bl.SensorLabel;
import org.doubrava.ergologger.bl.SensorLabelItem;
import org.doubrava.ergologger.bl.SensorType;
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
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
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
    private JPanel pnlChartGridBag;

    private SensorType sensorType;

    XYSeriesCollection collection;
    private JFreeChart chart;
    private XYLineAndShapeRenderer renderer;
    private ChartPanel chartPanel;

    public PanelSensorDisplay(SensorType sensorType) {

        this.sensorType = sensorType;
        this.lblSensorType.setText(SensorLabel.getInstance().getMap(sensorType).get(SensorLabelItem.NAME));
        this.lblAverageUnit.setText(SensorLabel.getInstance().getMap(sensorType).get(SensorLabelItem.UNIT));
        this.lblActualUnit.setText(SensorLabel.getInstance().getMap(sensorType).get(SensorLabelItem.UNIT));

        XYSeries series = new XYSeries("2016");
        series.add(18, 567);
        series.add(20, 612);
        series.add(25, 800);
        series.add(30, 980);
        series.add(40, 1410);
        series.add(50, 2350);

        this.collection = new XYSeriesCollection();
        collection.addSeries(series);

        this.chart = ChartFactory.createXYLineChart(
                "",
                "",
                "",
                collection,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        this.renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(null);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);
        chart.getLegend().setVisible(false);
        chart.setBackgroundPaint(null);

        this.chartPanel = new ChartPanel(chart);
        /*
        this.chartPanel = new ChartPanel(chart) {

            @Override
            public Dimension getPreferredSize() {
                System.out.println(this.getParent().getSize().toString());
                //return new Dimension(this.getWidth(), this.getHeight());


                return new Dimension((int)this.getParent().getSize().getWidth(), (int)this.getParent().getSize().getHeight());
            }

        };

         */

        chartPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 5, 2));
        chartPanel.setBackground(null);

        this.pnlChartGridBag.add(chartPanel, BorderLayout.CENTER);
        this.pnlChartGridBag.revalidate();
        this.pnlChartGridBag.repaint();

    }

    public SensorType getSensorType() {
        return sensorType;
    }

    public void setSensorValues(double averageValue, double actualValue) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("de", "DE"));
        nf.setMaximumFractionDigits(0);
        nf.setMinimumFractionDigits(0);

        this.lblAverageValue.setText(nf.format(averageValue));
        this.lblActualValue.setText(nf.format(actualValue));
    }

}
