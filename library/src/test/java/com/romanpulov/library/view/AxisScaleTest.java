package com.romanpulov.library.view;

import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

/**
 * Created by romanpulov on 30.12.2016.
 */

public class AxisScaleTest {

    private void printAxisScales(BarChart.AxisScaleCalculator calculator) {
        BarChart.AxisScale as = new BarChart.AxisScale();
        for (int i = 2; i < 300; i++) {
            StringBuilder s = new StringBuilder(String.format(Locale.getDefault(), "%3d", i));
            for (int j = 2; j < 9; j++) {
                as.setScale(0d, i, j);
                calculator.calcAxisScale(as);
                s.append(String.format(Locale.getDefault(), " %3d/%-3d", as.getCount(), (int) as.getMaxValue()));
                if (i < as.getMaxValue() / 2)
                    s.append(" (!)");
                if ((int)as.getMaxValue() % as.getCount() >0)
                    s.append(" (*)");
                if (i == (int)as.getMaxValue())
                    s.append(" (@)");
                if (i > (int)as.getMaxValue())
                    s.append(" (E)");
                if (j < as.getCount())
                    s.append(" (C)");
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
    public void testProportionalValueAxisScaleCalculator() {
        System.out.println("ProportionalValueAxisScaleCalculator ======================");
        printAxisScales(new BarChart.ProportionalValueAxisScaleCalculator());
        System.out.println("ProportionalValueAxisScaleCalculator ======================");
    }

    @Test
    public void test_294_5() {
        BarChart.AxisScale as = new BarChart.AxisScale();
        as.setScale(0d, 294, 5);
        BarChart.ProportionalValueAxisScaleCalculator calculator = new BarChart.ProportionalValueAxisScaleCalculator();
        calculator.calcAxisScale(as);
        System.out.println(as);
    }

    @Test
    public void test_4_5() {
        BarChart.AxisScale as = new BarChart.AxisScale();
        as.setScale(0d, 4, 5);
        BarChart.ProportionalValueAxisScaleCalculator calculator = new BarChart.ProportionalValueAxisScaleCalculator();
        calculator.calcAxisScale(as);
        System.out.println(as);
    }

    @Test
    public void test_4_2() {
        BarChart.AxisScale as = new BarChart.AxisScale();
        as.setScale(0d, 4, 2);
        BarChart.ProportionalValueAxisScaleCalculator calculator = new BarChart.ProportionalValueAxisScaleCalculator();
        calculator.calcAxisScale(as);
        System.out.println(as);
    }

    private void testAxisScalesCalculator(BarChart.AxisScaleCalculator calculator, boolean skipNoGap) {
        BarChart.AxisScale as = new BarChart.AxisScale();
        for (int i = 2; i < 300; i++) {
            for (int j = 2; j < 9; j++) {
                as.setScale(0d, i, j);
                calculator.calcAxisScale(as);

                try {
                    // invalid count
                    Assert.assertTrue(as.getCount() <= j);

                    // invalid max value
                    Assert.assertTrue(as.getMaxValue() > i);

                    // no gap
                    if (!skipNoGap)
                        Assert.assertNotEquals(i, (int) as.getMaxValue());

                    // fractional step
                    if ((int) as.getMaxValue() % as.getCount() > 0)
                        System.out.println("Warning : fractional step: " + "(" + i + "/" + j + ") - " + "(" + (int) as.getMaxValue() + "/" + as.getCount() + ")");

                    // big MaxValue
                    if (i < as.getMaxValue() / 2)
                        System.out.println("Warning : big MaxValue: " + "(" + i + "/" + j + ") - " + "(" + (int) as.getMaxValue() + "/" + as.getCount() + ")");

                } catch (AssertionFailedError e) {
                    System.out.println("Assertion data: " + "(" + i + "/" + j + ") - " + "(" + (int) as.getMaxValue() + "/" + as.getCount() + ")");
                    throw e;
                }
            }
        }
    }

    @Test
    public void newAxisScalesCalculatorValidator() {
        testAxisScalesCalculator(new BarChart.ProportionalValueAxisScaleCalculator(), false);
    }

    @Test
    public void axisScalesCalculatorValidator() {
        testAxisScalesCalculator(new BarChart.ValueAxisScaleCalculator(), true);
    }

    @Test
    public void axisValueCalculator_adaptive() {
        BarChart.ValueAxisScaleCalculator ax = new BarChart.ValueAxisScaleCalculator();
        for (int i = 1; i < 101; i++) {
            ax.calcAxisScale(i, 7);
            System.out.println("CalcForValue:");
            System.out.println(ax);
            ax.calcAxisScaleAdaptive(i, 7);
            System.out.println("CalcForValueAdaptive:");
            System.out.println(ax);
        }
    }

}
