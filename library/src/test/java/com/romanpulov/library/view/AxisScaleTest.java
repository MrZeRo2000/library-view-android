package com.romanpulov.library.view;

import org.junit.Test;

import java.util.Locale;

/**
 * Created by romanpulov on 30.12.2016.
 */

public class AxisScaleTest {

    private static class NewValueAxisScaleCalculator implements BarChart.AxisScaleCalculator {
        private static final int[] COUNT_VALUES = new int[] {2, 3, 4, 5, 6, 7, 8};

        static class AxisScaleData {
            final int mMaxValue;
            final int mCount;

            AxisScaleData(int maxValue, int count) {
                mMaxValue = maxValue;
                mCount = count;
            }
        }

        AxisScaleData getAxisScaleData(int maxValue, int count, int decrement) {
            // for value = 1
            if (maxValue == 1) {
                return new AxisScaleData(2, 2);
            }

            // for value = 2
            if (maxValue == 2) {
                if (count > 3)
                    return new AxisScaleData(4, 4);
                else
                    return new AxisScaleData(4, 2);
            }

            // for count = 2
            if (count == 2) {
                return new AxisScaleData(maxValue * count, count);
            }

            // use decreased count first
            count = count - decrement;

            int calcMaxValue = (maxValue / count + 1) * count;
            int step = calcMaxValue / count;

            if ((calcMaxValue - maxValue) < step) {
                // increase count using decrement
                if (decrement > 0) {
                    calcMaxValue = calcMaxValue + step;
                    count++;
                } else
                    return null;
            } else {
                // try without decrement if it was not used
                if (decrement > 0) {
                    AxisScaleData data = getAxisScaleData(maxValue, count + decrement, 0);
                    if (data != null)
                        return data;
                }
            }

            return new AxisScaleData(calcMaxValue, count);
        }

        @Override
        public void calcAxisScale(BarChart.AxisScale axisScale) {
            int count = axisScale.getCount();

            //count correction
            if (count > 9)
                count = 9;

            AxisScaleData minAxisScaleData = null;
            for (int c = count; c > 1; c--) {
                AxisScaleData axisScaleData = getAxisScaleData((int)axisScale.getMaxValue(), c, 1);
                if (minAxisScaleData == null)
                    minAxisScaleData = axisScaleData;
                else
                    if (axisScaleData.mMaxValue < minAxisScaleData.mMaxValue)
                        minAxisScaleData = axisScaleData;
            }

            //AxisScaleData axisScaleData = getAxisScaleData((int)axisScale.getMaxValue(), axisScale.getCount());
            axisScale.setScale(0, minAxisScaleData.mMaxValue, minAxisScaleData.mCount);
        }
    }


    private void printAxisScales(BarChart.AxisScaleCalculator calculator) {
        BarChart.AxisScale as = new BarChart.AxisScale();
        for (int i = 2; i < 300; i++) {
            String s = String.format(Locale.getDefault(), "%3d", i);
            for (int j = 2; j < 9; j++) {
                as.setScale(0d, i, j);
                calculator.calcAxisScale(as);
                s += String.format(Locale.getDefault(), " %3d/%-3d", as.getCount(), (int)as.getMaxValue());
                if (i < as.getMaxValue() / 2)
                    s += " (!)";
                if ((int)as.getMaxValue() % as.getCount() >0)
                    s+= " (*)";
                if (i == (int)as.getMaxValue())
                    s += " (@)";
                if (i > (int)as.getMaxValue())
                    s += " (E)";
                if (j < as.getCount())
                    s += " (C)";
            }
            System.out.println(s);
        }
    }

    @Test
    public void testValueAxisScaleCalculator() {
        System.out.println("ValueAxisScaleCalculator ======================");
        printAxisScales(new BarChart.ValueAxisScaleCalculator());
        System.out.println("ValueAxisScaleCalculator ======================");
    }

    @Test
    public void testNewValueAxisScaleCalculator() {
        System.out.println("ValueAxisScaleCalculator ======================");
        printAxisScales(new NewValueAxisScaleCalculator());
        System.out.println("ValueAxisScaleCalculator ======================");
    }

    @Test
    public void test_294_5() {
        BarChart.AxisScale as = new BarChart.AxisScale();
        as.setScale(0d, 294, 5);
        NewValueAxisScaleCalculator calculator = new NewValueAxisScaleCalculator();
        calculator.calcAxisScale(as);
        System.out.println(as);
    }

    @Test
    public void test_4_5() {
        BarChart.AxisScale as = new BarChart.AxisScale();
        as.setScale(0d, 4, 5);
        NewValueAxisScaleCalculator calculator = new NewValueAxisScaleCalculator();
        calculator.calcAxisScale(as);
        System.out.println(as);
    }

    @Test
    public void test_4_2() {
        BarChart.AxisScale as = new BarChart.AxisScale();
        as.setScale(0d, 4, 2);
        NewValueAxisScaleCalculator calculator = new NewValueAxisScaleCalculator();
        calculator.calcAxisScale(as);
        System.out.println(as);
    }

}
