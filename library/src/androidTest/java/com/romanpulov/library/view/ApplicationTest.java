package com.romanpulov.library.view;

import android.app.Application;
import android.graphics.Paint;
import android.test.ApplicationTestCase;
import android.util.DisplayMetrics;
import android.util.Log;

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


}