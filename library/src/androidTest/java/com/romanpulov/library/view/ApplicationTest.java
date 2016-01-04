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

    public void testCase3() throws Exception{
        BarChart bc = new BarChart(getContext());

        BarChart.Series series = bc.addSeries();
        series.addXY(0d, "A", 1d);
        series.addXY(1d, "B", 2d);

        series = bc.addSeries();
        series.addXY(0d, "B", 1d);
        series.addXY(1d, "C", 2d);

        bc.updateSeriesListValueBounds();
        bc.updateChartLayout();

        Field chartDrawLayoutField = bc.getClass().getDeclaredField("mChartDrawLayout");
        chartDrawLayoutField.setAccessible(true);
        BarChart.ChartDrawLayout chartDrawLayout = (BarChart.ChartDrawLayout)chartDrawLayoutField.get(bc);
        assertEquals(2, chartDrawLayout.getSeriesDrawDataList().size());
        assertEquals(3, chartDrawLayout.getSeriesDrawDataList().get(0).size());
        assertEquals(3, chartDrawLayout.getSeriesDrawDataList().get(1).size());
    }
}