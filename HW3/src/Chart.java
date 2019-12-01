import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Chart {
    public ArrayList<Double> getxAxis() {
        return xAxis;
    }

    public ArrayList<Double> getyAxis() {
        return yAxis;
    }

    private boolean firstAdd = false;  //флаг на первое добавление
    private double chartLevel = 0;  //содержит значение крайней правой точки графика
    private ArrayList<Double> xAxis = new ArrayList<>(50);
    private ArrayList<Double> yAxis = new ArrayList<>(50);
    private int accuracy = 6;

    public void setAccuracy(int accuracy) { //точность скругления, если нужа точность больше 6 знаков
        this.accuracy = accuracy;
    }

    public void addInterval(double a1, double a2, double value) {

        if (!firstAdd) {
            firstAdd = true;
            xAxis.add(a1);
            yAxis.add(0.0);
            xAxis.add(a2);
            yAxis.add(value);
            chartLevel += value;
        } else {
            addPoints(a1, a2);
            setInterval(a1, a2, value);
            chartLevel += value;
        }
    }


    public double getValue(double argument) {     //всего 4 варианта, лежит слева или справа от интервала, внутри интервала попадая или не поадая на имеющуюся точку
        double result;

        if (argument <= xAxis.get(0)) {
            result = 0;
        } else {
            if (argument >= xAxis.get(xAxis.size() - 1)) {
                result = chartLevel;
            } else {
                if (xAxis.contains(argument)) {
                    result = yAxis.get(xAxis.indexOf(argument));
                } else {
                    int index = -Collections.binarySearch(xAxis, argument) - 1;
                    double speedBetweenPoint = round(Math.abs(yAxis.get(index - 1) - yAxis.get(index)) /
                            Math.abs(xAxis.get(index - 1) - xAxis.get(index)));
                    //System.out.println("speed = "+speedBetweenPoint);
                    result = round((Math.abs(argument - xAxis.get(index - 1)) * speedBetweenPoint) + yAxis.get(index - 1));
                    //System.out.println("result = "+result);
                }
            }
        }
        return result;
    }

    private double round(double value) {   //Округление
        if (accuracy < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(accuracy, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public void showInfo() { //вывод точек в текстовом виде
        for (int i = 0; i <= xAxis.size() - 1; i++) {
            System.out.println("X = " + xAxis.get(i) + " Y = " + yAxis.get(i));
        }
    }

    private void setInterval(double argument1, double argument2, double value) {
        double speed = round(value / Math.abs(argument2 - argument1));
        double sizeOfInterval;
        double increase = 0;
        double result;
        for (int i = xAxis.indexOf(argument1); i < xAxis.indexOf(argument2); i++) {
            sizeOfInterval = round(Math.abs(xAxis.get(i + 1) - xAxis.get(i)));
            increase = increase + round(sizeOfInterval * speed);
            result = round(yAxis.get(i + 1) + increase);
            yAxis.set(i + 1, result);
        }
        for (int i = xAxis.indexOf(argument2); i < xAxis.size() - 1; i++) {
            yAxis.set(i + 1, round(yAxis.get(i + 1) + value));
        }

    }

    public double getMinBetween(double argument1, double argument2) {
        addPoints(argument1, argument2);
        double minimum = yAxis.get(xAxis.indexOf(argument1));
        for (int i = xAxis.indexOf(argument1) + 1; i <= xAxis.indexOf(argument2); i++) {
            if (minimum > yAxis.get(i)) {
                minimum = yAxis.get(i);
            }
        }
        return minimum;
    }


    public double getMaxBetween(double argument1, double argument2) {
        addPoints(argument1, argument2);
        double maximum = yAxis.get(xAxis.indexOf(argument1));
        for (int i = xAxis.indexOf(argument1) + 1; i <= xAxis.indexOf(argument2); i++) {
            if (maximum < yAxis.get(i)) {
                maximum = yAxis.get(i);
            }
        }
        return maximum;
    }


    private void addPoints(double argument1, double argument2) {
        if (Collections.binarySearch(xAxis, argument1) < 0) {
            int index = -Collections.binarySearch(xAxis, argument1) - 1;
            yAxis.add(index, getValue(argument1));
            xAxis.add(index, argument1);
        }
        if (Collections.binarySearch(xAxis, argument2) < 0) {
            int index = -Collections.binarySearch(xAxis, argument2) - 1;
            yAxis.add(index, getValue(argument2));
            xAxis.add(index, argument2);
        }

    }

    public void extension() {  //расширяет график на 100 влево и вправо
        addInterval(round(xAxis.get(0) - 100), round(xAxis.get(xAxis.size() - 1) + 100), 0);
    }

    public double getSumBetween(double a1, double a2) {
        addPoints(a1, a2);
        addZeroPoints();
        double area = 0;
        double sumArea = 0;
        double yInterval = 0;
        double xInterval = 0;

        for (int i = xAxis.indexOf(a1); i < xAxis.indexOf(a2); i++) {  //формула площади п\у трапеции с одной стороной = 0 совпадает с площадью п\у треугольника
            yInterval = round((yAxis.get(i + 1) + yAxis.get(i)) / 2);  //сумма сторон/2
            xInterval = round(Math.abs(xAxis.get(i + 1) - xAxis.get(i)));   //высота
            area = round(yInterval * xInterval);
            sumArea += area;
        }
        return sumArea;
    }

        private void addZeroPoints () {    //добавляем точки пересечения оси Х
            double A;                   //ищем по формуле Х=-С/А, где С=(х1*у2-х2*у1), А=(у1-у2)
            double C;
            double xZero;
            for (int i = 0; i < xAxis.size() - 1; i++) {
                if ((yAxis.get(i) > 0 && yAxis.get(i + 1) < 0) || (yAxis.get(i) < 0 && yAxis.get(i + 1) > 0)) {  //находим перегиб графика через 0
                    C = round((xAxis.get(i) * yAxis.get(i + 1) - xAxis.get(i + 1) * yAxis.get(i)));
                    A = round(yAxis.get(i) - yAxis.get(i + 1));
                    xZero = round(-C / A);
                    xAxis.add(i + 1, xZero);
                    yAxis.add(i + 1, 0.0);

                }

            }
        }
    }
