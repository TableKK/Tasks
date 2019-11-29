import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Chart {
    private boolean firstAdd = false;  //флаг на первое добавление
    private double chartLevel = 0;  //содержит значение крайней правой точки графика
    private ArrayList<double[]> initialData = new ArrayList<>(50);
    private ArrayList<Integer> arrayOfZeroPoint = new ArrayList<>(); //массив, содержащий индексы точек пересечения графиком оси Х

    public ArrayList<double[]> getInitialData() {
        return initialData;
    }

    public void extension() {   //расширяет интервал на 100 влево и 100 вправо
        initialData.add(0, new double[]{round(initialData.get(0)[0] - 100, 6), 0});
        initialData.add(new double[]{round(initialData.get(initialData.size() - 1)[0] + 100, 6), chartLevel});
    }

    public void addInterval(double a1, double a2, double value) {
        if (!firstAdd) {
            double[] array1 = {a1, 0};
            initialData.add(array1);
            double[] array2 = {a2, value};
            initialData.add(array2);
            firstAdd = true;
            chartLevel += value;
        } else {
            double speed = round(value / Math.abs(a2 - a1), 6);
            switch (checkLocationOfNewInterval(a1, a2)) {
                case 1:
                    for (int i = 0; i <= initialData.size() - 1; i++) {
                        initialData.set(i, new double[]{initialData.get(i)[0], initialData.get(i)[1] += value});
                    }
                    chartLevel += value;
                    initialData.add(0, new double[]{a2, value});
                    initialData.add(0, new double[]{a1, 0});
                    break;

                case 2:
                    initialData.add(new double[]{a1, chartLevel});
                    initialData.add(new double[]{a2, value + chartLevel});
                    chartLevel += value;
                    break;

                case 3:
                    double increaseValue1 = 0;
                    initialData.add(0, new double[]{a1, 0});
                    for (int i = 0; i < initialData.size() - 1; i++) {
                        double intervalOfIncrease = round(Math.abs(initialData.get(i + 1)[0] - initialData.get(i)[0]), 6);
                        increaseValue1 += round(intervalOfIncrease * speed, 6);
                        initialData.set(i + 1, new double[]{initialData.get(i + 1)[0], round(initialData.get(i + 1)[1] + increaseValue1, 6)});
                    }
                    initialData.add(new double[]{a2, round(chartLevel + value, 6)});
                    chartLevel += value;
                    break;

                case 4:
                    int index1 = -1, index2 = -1;
                    for (int i = 0; i <= initialData.size() - 1; i++) {
                        if (a1 == initialData.get(i)[0]) {
                            index1 = i;
                        }
                        if (a2 == initialData.get(i)[0]) {
                            index2 = i;
                        }
                    }
                    if (index1 != -1 && index2 != -1) {
                        setInterval(index1, index2, speed, value);
                    } else {
                        if (index1 != -1) {
                            for (int i = index1; i < initialData.size() - 1; i++) {
                                if (pointBetween(a2, i)) {
                                    initialData.add(i + 1, new double[]{a2, getValue(a2)});
                                    setInterval(index1, i + 1, speed, value);
                                    break;
                                }
                            }
                        } else {
                            if (index2 != -1) {
                                for (int i = 0; i < index2; i++) {
                                    if (pointBetween(a1, i)) {
                                        initialData.add(i + 1, new double[]{a1, getValue(a1)});
                                        setInterval(i + 1, index2, speed, value);
                                        break;
                                    }
                                }
                            } else {
                                int ind1 = 0, ind2 = 0;
                                for (int i = 0; i < initialData.size() - 1; i++) {
                                    if (pointBetween(a1, i)) {
                                        initialData.add(i + 1, new double[]{a1, getValue(a1)});
                                        ind1 = i + 1;
                                    }
                                    if (pointBetween(a2, i)) {
                                        initialData.add(i + 1, new double[]{a2, getValue(a2)});
                                        ind2 = i + 1;
                                    }
                                }
                                setInterval(ind1, ind2, speed, value);
                            }
                        }
                    }
                    chartLevel += value;
                    break;

                case 5:
                    initialData.add(0, new double[]{a1, 0});
                    for (int i = 0; i < initialData.size() - 1; i++) {
                        if (a2 == initialData.get(i)[0]) {
                            //System.out.println("i = " + i);
                            setInterval(0, i, speed, value);
                            break;
                        } else {
                            if (pointBetween(a2, i)) {
                                initialData.add(i + 1, new double[]{a2, getValue(a2)});
                                setInterval(0, i + 1, speed, value);
                                break;
                            }
                        }
                    }
                    chartLevel += value;
                    break;

                case 6:
                    initialData.add(new double[]{a2, chartLevel});
                    for (int i = 0; i < initialData.size() - 1; i++) {
                        if (a1 == initialData.get(i)[0]) {
                            //System.out.println("i = " + i);
                            setInterval(i, initialData.size() - 1, speed, value);
                            break;
                        } else {
                            if (pointBetween(a1, i)) {
                                initialData.add(i + 1, new double[]{a1, getValue(a1)});
                                setInterval(i + 1, initialData.size() - 1, speed, value);
                                break;
                            }
                        }
                    }
                    chartLevel += value;
                    break;
            }
        }
    }


    private byte checkLocationOfNewInterval(double a1, double a2) {  //проверяет, как новый интервал ложиться на ранее заданные
        byte result = 0;
        if ((a1 < initialData.get(0)[0] && a2 < initialData.get(0)[0])) {
            //System.out.println("Не попадает на введенные интервалы, находится слева 1 ");
            result = 1;
        }
        if ((a1 > initialData.get(initialData.size() - 1)[0] && a2 > initialData.get(initialData.size() - 1)[0])) {
            //System.out.println("Не попадает на введенные интервалы, находится справа 2");
            result = 2;
        }
        if (a1 < initialData.get(0)[0] && a2 > initialData.get(initialData.size() - 1)[0]) {
            //System.out.println("Перекрывает введнные интервалы 3");
            result = 3;
        }
        if (a1 >= initialData.get(0)[0] && a2 <= initialData.get(initialData.size() - 1)[0]) {
            //System.out.println("Полностью входит в введенные интервалы 4");
            result = 4;
        }
        if (a1 < initialData.get(0)[0] && numberInInterval(a2)) {
            //System.out.println("Попадает правая часть 5");
            result = 5;
        }
        if (numberInInterval(a1) && a2 > initialData.get(initialData.size() - 1)[0]) {
            //System.out.println("Попадает левая часть 6");
            result = 6;
        }
        return result;
    }

    private boolean numberInInterval(double argument) {  //проверка, попадает ли точка нового интервала в старые интервалы
        if (argument >= initialData.get(0)[0] && argument <= initialData.get(initialData.size() - 1)[0]) {
            return true;
        } else {
            return false;
        }
    }

    private boolean pointBetween(double point, int index) { //проверка, находится ли новая точка между ранее заданными точками в массиве
        if (initialData.get(index)[0] < point && initialData.get(index + 1)[0] > point) {
            return true;
        } else {
            return false;
        }
    }

    private void setInterval(int index1, int index2, double speed, double value) { //изменяет заданный интервал
        double sizeOfInterval;
        double increase = 0;
        double result;
        for (int i = index1; i < index2; i++) {
            sizeOfInterval = round(Math.abs(initialData.get(i + 1)[0] - initialData.get(i)[0]), 6);
            increase = increase + round(sizeOfInterval * speed, 6);
            result = round(initialData.get(i + 1)[1] + increase, 6);
            initialData.set(i + 1, new double[]{initialData.get(i + 1)[0], result});
        }
        for (int i = index2; i < initialData.size() - 1; i++) {
            initialData.set(i + 1, new double[]{initialData.get(i + 1)[0], round(initialData.get(i + 1)[1] + value, 6)});
        }
    }

    public void showInfo() { //вывод точек в текстовом виде
        for (double[] show : initialData) {
            System.out.println(Arrays.toString(show));
        }
    }

    private double round(double value, int places) {   //Округление
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double getValue(double argument) {
        double result = 666;
        if (numberInInterval(argument)) {
            for (int i = 0; i <= initialData.size() - 1; i++) {
                if (argument == initialData.get(i)[0]) {
                    result = initialData.get(i)[1];
                    break;
                }
                if (argument > initialData.get(i)[0] && argument <= initialData.get(i + 1)[0]) {
                    double speedBetweenPoint = round(Math.abs(initialData.get(i)[1] - initialData.get(i + 1)[1]) /
                            Math.abs(initialData.get(i)[0] - initialData.get(i + 1)[0]), 6);
                    result = round((Math.abs(argument - initialData.get(i)[0]) * speedBetweenPoint) + initialData.get(i)[1], 6);
                }
            }
        }
        if (argument <= initialData.get(0)[0]) {
            result = 0;
        }
        if (argument >= initialData.get(initialData.size() - 1)[0]) {
            result = chartLevel;
        }
        return result;
    }

    public double getSumBetween(double argumentStart, double argumentEnd) {  //не работает корректно, когда график пересекает 0
        addZeroPoint();  //добавляем точки пересечения графиком оси Х
        int index1 = -1, index2 = -1;
        double sizeOfInterval;
        double area;
        double areaSum = 0;
        addInterval(argumentStart, argumentEnd, 0);
        for (int i = 0; i <= initialData.size() - 1; i++) {  //ищем индексы краев интервала
            if (argumentStart == initialData.get(i)[0]) {
                index1 = i;
            }
            if (argumentEnd == initialData.get(i)[0]) {
                index2 = i;
                break;
            }
        }
        if (index1 == 0) {  //есть в интервал попадает самая первая точка, то площадь рассчитывается по формуле треугольника, а не трапеции
            sizeOfInterval = round(Math.abs(initialData.get(index1 + 1)[0] - initialData.get(index1)[0]), 6);
            areaSum += round(sizeOfInterval * initialData.get(index1 + 1)[1] / 2, 6);
            index1++;
        }
        //arrayOfZeroPoint.contains(i+1)

        for (int i = index1; i < index2; i++) {
            if (arrayOfZeroPoint.contains(i)) {     //если точка, с которой мы начинаем лежит на оси Х, то мы должны использовать формулу П\У треугольника
                sizeOfInterval = round(Math.abs(initialData.get(i + 1)[0] - initialData.get(i)[0]), 6);
                areaSum += round(sizeOfInterval * initialData.get(i + 1)[1], 6);
                i++;
            } else {    //если точка, находящаяся дальше на 1, лежит на оси Х, то мы должны использовать формулу П\У треугольника
                if (arrayOfZeroPoint.contains(i + 1)) {
                    sizeOfInterval = round(Math.abs(initialData.get(i + 1)[0] - initialData.get(i)[0]), 6);
                    areaSum += round(sizeOfInterval * initialData.get(i)[1], 6);
                    i++;
                } else {  //рассчитываем по формуле прямоугольной трапеции
                    sizeOfInterval = round(Math.abs(initialData.get(i + 1)[0] - initialData.get(i)[0]), 6); //расстояние по оси Х(высота)
                    area = round((Math.abs(initialData.get(i)[1]) + Math.abs(initialData.get(i + 1)[1])) / 2 * sizeOfInterval, 6);//площадь трапеции, высота*сумма сторон/2
                    if (initialData.get(i)[1]<=0&&initialData.get(i + 1)[1]<=0) area = -area;
                    areaSum += round(area, 6);
                }
            }
        }
        return areaSum;
    }

    private void addZeroPoint() {  //добавляем точки пересечения оси Х
        double A;                   //ищем по формуле Х=-С/А, где С=(х1*у2-х2*у1), А=(у1-у2)
        double C;
        double xZero;
        for (int i = 0; i < initialData.size() - 1; i++) {
            if ((initialData.get(i)[1] > 0 && initialData.get(i + 1)[1] < 0) || (initialData.get(i)[1] < 0 && initialData.get(i + 1)[1] > 0)) {  //находим перегиб графика через 0
                C = round((initialData.get(i)[0] * initialData.get(i + 1)[1] - initialData.get(i + 1)[0] * initialData.get(i)[1]), 6);
                A = round(initialData.get(i)[1] - initialData.get(i + 1)[1], 6);
                xZero = round(-C / A, 6);
                initialData.add(i + 1, new double[]{xZero, 0});
                System.out.println("xZero = "+xZero);
                arrayOfZeroPoint.add(i + 1);
                Collections.sort(arrayOfZeroPoint);
            }
        }
    }


    public double getMinBetween(double argumentStart, double argumentEnd) {
        int index1 = -1, index2 = -1;
        addInterval(argumentStart, argumentEnd, 0);
        for (int i = 0; i <= initialData.size() - 1; i++) {
            if (argumentStart == initialData.get(i)[0]) {
                index1 = i;
            }
            if (argumentEnd == initialData.get(i)[0]) {
                index2 = i;
            }
        }
        double minimum = initialData.get(index1)[1];
        for (int i = index1; i <= index2; i++) {
            if (minimum > initialData.get(i)[1]) {
                minimum = initialData.get(i)[1];
            }
        }
        return minimum;
    }

    public double getMaxBetween(double argumentStart, double argumentEnd) {
        int index1 = -1, index2 = -1;
        addInterval(argumentStart, argumentEnd, 0);
        for (int i = 0; i <= initialData.size() - 1; i++) {
            if (argumentStart == initialData.get(i)[0]) {
                index1 = i;
            }
            if (argumentEnd == initialData.get(i)[0]) {
                index2 = i;
            }
        }
        double maximum = initialData.get(index1)[1];
        for (int i = index1; i <= index2; i++) {
            if (maximum < initialData.get(i)[1]) {
                maximum = initialData.get(i)[1];
            }
        }
        return maximum;
    }

}
