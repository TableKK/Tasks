import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;

public class Charts {
    private int accuracy = 1;   //количество точек после запятой для осей
    private ArrayList<Double> xAxis = new ArrayList<Double>();  //массив для отрисовки итогового графика, X ось
    private ArrayList<Double> yAxis = new ArrayList<Double>();  //массив для отрисовки итогового графика, У ось
    private ArrayList<double[]> intervalData = new ArrayList<>(50); //массив, хрянящий входящие значение и значение скорости возрастания графика
                                                                                 //для каждой точки
    public ArrayList<double[]> getIntervalData() {
        return intervalData;
    }

    public void setAccuracy(int accuracy) {
        this.accuracy = accuracy;
    }

    private boolean firstFlag = true;   //флаг для первого добавления интервала

    private void swap(ArrayList<double[]> list, int index) {
        list.add(index + 2, list.get(index));
        list.remove(index);
    }

    private void sort(ArrayList<double[]> list) {

        boolean isSorted = false;
        while (!isSorted) {
            isSorted = true;
            for (int i = 0; i < list.size() - 1; i++) {
                if (list.get(i)[0] > list.get(i + 1)[0]) {
                    isSorted = false;
                    swap(list, i);
                }
            }
        }
    }


    public void addInterval(double a1, double a2, double value) {
        double speed = value / Math.abs((a2 - a1)) / (Math.pow(10,accuracy));
        speed = round(speed, 6);
        boolean wasAdded = false;

        if (firstFlag) {    //добавление первого интервала
            for (double i = a1; i <= a2; i += 1/(Math.pow(10,accuracy))) {
                i = round(i, accuracy);
                if (i >= a2) {
                    speed = 0;
                }
                double[] coordinateXYA = {i, value, speed};
                intervalData.add(coordinateXYA);
            }
            firstFlag = false;
        } else {
            for (double i = a1; i <= a2; i += 1/(Math.pow(10,accuracy))) {
                i = round(i, accuracy);
                if (i == a2) {
                    speed = 0;
                }
                for (int j = 0; j <= intervalData.size() - 1; j++) {
                    if (intervalData.get(j)[0] == i) {
                        double[] arr = {i, intervalData.get(j)[1] + value, intervalData.get(j)[2] + speed};
                        intervalData.set(j, arr);
                        wasAdded = true;
                    }
                }
                if (!wasAdded) {
                    double[] coordinateXYA = {i, value, speed};
                    intervalData.add(coordinateXYA);
                }
                wasAdded = false;
            }
        }
        sort(intervalData);
    }

    public void showInfo() {   //показ всех точек в текстовом виде, для тестирования
        clearEmptyPoint();
        for (double[] show : intervalData) {
            System.out.println(Arrays.toString(show));
        }
    }


    private static double round(double value, int places) {   //Округление
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private void clearEmptyPoint() {
        addInterval(intervalData.get(0)[0], intervalData.get(intervalData.size() - 1)[0], 0);
    }

    public ArrayList<Double> getxAxis() {
        clearEmptyPoint();
        xAxis.clear();
        for (int i = 0; i <= intervalData.size() - 1; i++) {
            xAxis.add(intervalData.get(i)[0]);
        }
        return xAxis;
    }


    public ArrayList<Double> getyAxis() {
        clearEmptyPoint();
        yAxis.clear();
        yAxis.add(0.0);
        double chartNextLevel = 0;
        for (int i = 1; i <= intervalData.size() - 1; i++) {
            chartNextLevel = chartNextLevel + intervalData.get(i - 1)[2];
            chartNextLevel = round(chartNextLevel, 6);
            yAxis.add(chartNextLevel);
        }
        return yAxis;
    }

    public double getValue(double argument) {
        clearEmptyPoint();
        getxAxis();
        getyAxis();
        if (xAxis.contains(argument)) {
            int arg = xAxis.indexOf(argument);
            return yAxis.get(arg);
        } else throw new IllegalArgumentException();
    }

    public double getSumBetween(double argumentStart, double argumentEnd) {
        clearEmptyPoint();
        double sum = 0;
        int numbers = 0;
        for (double i = argumentStart; i <= argumentEnd; i += ((double)1/Math.pow(10,accuracy))) {
            i = round(i, accuracy);
            sum += getValue(i);
            numbers++;
        }
        double result = sum / numbers;
        result = round(result, 6);
        return result * (numbers - 1) /Math.pow(10,accuracy);
    }

    public double getMaxBetween(double argumentStart, double argumentEnd) {
        clearEmptyPoint();
        double max = getValue(argumentStart);
        for (double i = argumentStart; i <= argumentEnd; i += 1/(Math.pow(10,accuracy))) {
            i = round(i, accuracy);
            if (max < getValue(i)) max = getValue(i);
        }
        return max;
    }

    public double getMinBetween(double argumentStart, double argumentEnd) {
        clearEmptyPoint();
        double min = getValue(argumentStart);
        for (double i = argumentStart; i <= argumentEnd; i += 1/(Math.pow(10,accuracy))) {
            i = round(i, accuracy);
            if (min > getValue(i)) min = getValue(i);
        }
        return min;
    }

}
