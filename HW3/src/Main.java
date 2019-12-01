import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.plot.*;

import java.awt.*;
import java.util.ArrayList;
import javax.swing.JFrame;

import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RefineryUtilities;

public class Main extends JFrame {
    public Main(String applicationTitle, String chartTitle) {
        super(applicationTitle);

        JFreeChart xylineChart = ChartFactory.createXYLineChart(
                chartTitle,
                "Х",
                "Value",
                createDataset(),
                PlotOrientation.VERTICAL,
                true, true, false);

        ChartPanel chartPanel = new ChartPanel(xylineChart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 367));
        final XYPlot plot = xylineChart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesStroke(0, new BasicStroke(4.0f));
        plot.setRenderer(renderer);
        setContentPane(chartPanel);
    }

    public XYDataset createDataset() {
        Chart test = new Chart();
        test.setAccuracy(6);  //может быть любым, ограничение - точность double


        test.addInterval(0, 4, 4);
        test.addInterval(0,8,8);
        test.addInterval(3, 5, 4);
        test.addInterval(8,10,-10);
        test.addInterval(-1,11,-20);



        System.out.println("Значение  = "+test.getValue(3));
        System.out.println("Площадь = " + test.getSumBetween(0, 2));
        System.out.println("Minimum = "+test.getMinBetween(2,4));
        System.out.println("Maximum = "+test.getMaxBetween(5,6));
        //test.extension();
        test.showInfo();

        ArrayList<Double> xList = test.getxAxis();
        ArrayList<Double> yList = test.getyAxis();
        final XYSeries finaalData = new XYSeries("RedChart");

        for (int i = 0; i <= xList.size() - 1; i++) {
        finaalData.add(xList.get(i), yList.get(i));

        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(finaalData);
        return dataset;
    }

    public static void main(String[] args) {
        Main chart = new Main("Тестовое задание",
                "Итоговый график");
        chart.pack();
        RefineryUtilities.centerFrameOnScreen(chart);
        chart.setVisible(true);
    }

}

