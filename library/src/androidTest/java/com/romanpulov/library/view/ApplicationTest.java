package com.romanpulov.library.view;

import android.app.Application;
import android.graphics.Paint;
import android.test.ApplicationTestCase;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.reflect.Field;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    public void testCase1() {
        assertTrue(true);
        Log.d("ApplicationTest", "testcase1");
    }

    public void testCase2() {
        //prepare data
        BarChart.Series series = new BarChart.Series();
        series.addXY(1d, "value 1", 1d);
        series.addXY(2d, "value 2", 3d);
        series.addXY(3d, "value 3", 2d);
        series.addXY(4d, "value 4", 1d);
        series.addXY(5d, "value 5", 5d);

        BarChart.SeriesList seriesList = new BarChart.SeriesList();
        seriesList.add(series);

        Paint paint = new Paint();

        DisplayMetrics dm = new DisplayMetrics();
        assertNotNull(dm);

        BarChart.ChartLayout cl = new BarChart.ChartLayout(dm);
        cl.setAxesTextPaint(paint);
        int width = 0;
        int height = 160;


        cl.updateLayout(width, height, seriesList);
        Log.d("ApplicationTest", "ChartRect = " + cl.getChartRect() + " IsLayoutValid = " + cl.getLayoutValid());
        Log.d("ApplicationTest", "xAxis = " + cl.getXAxis());
        Log.d("ApplicationTest", "yAxis = " + cl.getYAxis());

        cl.updateLayout(width, height, seriesList);
        Log.d("ApplicationTest", "ChartRect = " + cl.getChartRect() + " IsLayoutValid = " + cl.getLayoutValid());
        Log.d("ApplicationTest", "xAxis = " + cl.getXAxis());
        Log.d("ApplicationTest", "yAxis = " + cl.getYAxis());

    }

    public void testChartSorting() {
        BarChart bc = new BarChart(getContext());
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
        Log.d("ApplicationTest", "chart_sorting completed");
    }

    public void testValueBounds() {
        BarChart bc = new BarChart(getContext());
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
        Log.d("ApplicationTest", "value_bounds completed");
    }
}