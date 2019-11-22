import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.*;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class Main extends JFrame {

    public Main(String title) {
        super(title);


        // Create dataset
        XYDataset dataset = createDataset();

        // Create chart
        JFreeChart chart = ChartFactory.createScatterPlot("GazpromHomeWork", "X", "Y", dataset,
                PlotOrientation.VERTICAL, true, true, false);

        //Changes background color
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(255, 228, 196));

        // Create Panel
        ChartPanel panel = new ChartPanel(chart);
        setContentPane(panel);
    }

    public XYDataset createDataset() {


        XYSeriesCollection dataset = new XYSeriesCollection();
        Charts c1 = new Charts();
        c1.setAccuracy(1);  //количество точек после запятой
                            //при 3 и выше работает крайне медленно, занимает огромное количество памяти
                            //при 2 графики становятся линиями
        c1.addInterval(2, 5, 10);
        c1.addInterval(-2, 4, 20);
        c1.addInterval(-2, -1, 25);
        c1.addInterval(3, 5, -40);
        c1.addInterval(0, 0.5, 50);
        System.out.println("Площадь = " + c1.getSumBetween(4.1, 4.2));
        System.out.println("Значение = " + c1.getValue(4));
        System.out.println("Максимум = " + c1.getMaxBetween(0, 3));
        System.out.println("Минимум = " + c1.getMinBetween(2, 4.3));

        XYSeries series1 = new XYSeries("Result");
        for (int i = 0; i <= c1.getxAxis().size() - 1; i++) {
            series1.add(c1.getxAxis().get(i), c1.getyAxis().get(i));
        }
        dataset.addSeries(series1);

        XYSeries series2 = new XYSeries("Start");
        for (int i = 0; i <= c1.getIntervalData().size() - 1; i++) {
            series2.add(c1.getIntervalData().get(i)[0], c1.getIntervalData().get(i)[1]);
        }
        dataset.addSeries(series2);

        return dataset;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main example = new Main("Scatter Chart Example | BORAJI.COM");
            example.setSize(1280, 780);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }

}
