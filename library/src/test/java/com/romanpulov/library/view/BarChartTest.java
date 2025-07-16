package com.romanpulov.library.view;

import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Created by rpulov on 06.10.2015.
 */
public class BarChartTest {

    @Test
    public void first_test() {
        assertNotEquals(1, 2);
        System.out.println("first_test completed");
    }

    @Test
    @Ignore("Need to mock")
    public void chart_sorting() {
        BarChart bc = new BarChart(null);
        assertNotNull(bc);
        BarChart.Series series = bc.addSeries();
        series.addXY(3., "Three", 1.);
        series.addXY(1., "One", 1.);
        series.addXY(2., "Two", 1.);
        assertEquals(3., series.get(0).x, 1e-7);
        bc.sortSeries(0);
        assertEquals(1., series.get(0).x, 1e-7);
        assertEquals(2., series.get(1).x, 1e-7);
        assertEquals(3., series.get(2).x, 1e-7);
        System.out.println("chart_sorting completed");
    }

    @Test
    @Ignore("Need to mock")
    public void value_bounds() {
        BarChart bc = new BarChart(null);
        assertNotNull(bc);
        BarChart.Series series = bc.addSeries();
        series.addXY(3., "Three", 7.);
        series.addXY(1., "One", -1.);
        series.addXY(2., "Two", 6.);
        series.updateValueBounds();
        BarChart.ChartValueBounds vb = series.getValueBounds();
        assertEquals(1., vb.minX, 1e-7);
        assertEquals(3., vb.maxX, 1e-7);
        assertEquals(-1., vb.minY, 1e-7);
        assertEquals(7., vb.maxY, 1e-7);
        System.out.println("value_bounds completed");
    }

    @Test
    public void chart_axis_maxvalue() {
        BarChart.ChartAxis va = new BarChart.ChartAxis(BarChart.ChartAxis.AXIS_TYPE_ARGUMENT);
        va.setRange(0d, 8d, 100);
        assertEquals(0d, va.getAxisScale().getMinValue(), 1e-7);
        assertEquals(8d, va.getAxisScale().getMaxValue(), 1e-7);

        BarChart.ChartAxis vv = new BarChart.ChartAxis(BarChart.ChartAxis.AXIS_TYPE_VALUE);
        vv.setRange(0d, 8d, 100);
        assertEquals(0d, vv.getAxisScale().getMinValue(), 1e-7);
        // for Excel like ValueAxisScaleCalculator
        // assertEquals(10d, vv.getAxisScale().getMaxValue(), 1e-7);
        assertEquals(9d, vv.getAxisScale().getMaxValue(), 1e-7);

        vv.setRange(0d, 23d, 100);
        assertEquals(0d, vv.getAxisScale().getMinValue(), 1e-7);
        // for Excel like ValueAxisScaleCalculator
        // assertEquals(25d, vv.getAxisScale().getMaxValue(), 1e-7);
        assertEquals(27d, vv.getAxisScale().getMaxValue(), 1e-7);

        System.out.println("chartaxis_maxvalue completed");
    }

    @Test
    public void value_formatter_function() {
        System.out.printf(Locale.getDefault(), "%1.1f%n", 1.44345d);

        List<Double> valueList = Arrays.asList(
                1d, 1.44345d, 9.4678d, 10d, 99d, 534234d, 5123423d
        );

        List<String> expectedValueList = Arrays.asList(
                "1", "1.4", "9.5", "10", "99", "534234", "5.1e+06"
        );

        for (int i = 0; i < valueList.size(); i ++) {
            assertEquals(expectedValueList.get(i), BarChart.ChartValueFormatter.formatValue(valueList.get(i)));
        }
    }
}
