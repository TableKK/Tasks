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

        test.addInterval(1,4,2);
        test.addInterval(4,5,-3);
        test.addInterval(5,8,-1);
        test.addInterval(8,12,7);
        System.out.println(test.getValue(10));
        //test.addInterval(3, 5, 4);
        //test.addInterval(9, 11, 2);
        //test.addInterval(0, 15, 15);
        //test.addInterval(3, 5, 2);
        //test.addInterval(3, 4.5, -1.5);
        //test.addInterval(1, 3, 4);
        //test.addInterval(6, 7, 2);
        //test.addInterval(-1, 3, 2);
        //test.addInterval(-2, 2, 4);
        //test.addInterval(14, 16, 2);
        //test.addInterval(15, 17, 2);
        //test.addInterval(13.5,20,-53.4);
        //test.addInterval(4.4,5.235,-34.664);


        //System.out.println(test.getValue(12));
        //System.out.println("Значение  = "+test.getValue(9.75));
        System.out.println("Площадь = "+ test.getSumBetween(3,10));
        //System.out.println("Minimum = "+test.getMinBetween(5,6));
        //System.out.println("Maximum = "+test.getMaxBetween(5,6));

        test.showInfo();
        //test.extension();
        ArrayList<double[]> list = test.getInitialData();
        final XYSeries firefox = new XYSeries("HardChart");

        for (int i = 0; i <= list.size() - 1; i++) {
            firefox.add(list.get(i)[0],list.get(i)[1]);

        }

        final XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(firefox);
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
