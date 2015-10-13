
import android.graphics.Paint;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.romanpulov.library.view.BarChart;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by rpulov on 06.10.2015.
 */
public class BarChartTest {

    //@Test
    public void first_test() {
        assertFalse(1==2);
        System.out.println("first_test completed");
    }

    //@Test
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

    //@Test
    public void value_bounds() {
        BarChart bc = new BarChart(null);
        assertNotNull(bc);
        BarChart.Series series = bc.addSeries();
        series.addXY(3., "Three", 7.);
        series.addXY(1., "One", -1.);
        series.addXY(2., "Two", 6.);
        series.updateValueBounds();
        BarChart.ChartValueBounds vb = series.getValueBounds();
        assertEquals(1., vb.getMinX(), 1e-7);
        assertEquals(3., vb.getMaxX(), 1e-7);
        assertEquals(-1., vb.getMinY(), 1e-7);
        assertEquals(7., vb.getMaxY(), 1e-7);
        System.out.println("value_bounds completed");
    }

    //@Test
    public void chartaxis_maxvalue() {
        BarChart.ChartAxis va = new BarChart.ChartAxis(BarChart.ChartAxis.AXIS_TYPE_ARGUMENT);
        va.setRange(0d, 8d, 100);
        assertEquals(0d, va.getAxisScale().getMinValue(), 1e-7);
        assertEquals(8d, va.getAxisScale().getMaxValue(), 1e-7);

        BarChart.ChartAxis vv = new BarChart.ChartAxis(BarChart.ChartAxis.AXIS_TYPE_VALUE);
        vv.setRange(0d, 8d, 100);
        assertEquals(0d, vv.getAxisScale().getMinValue(), 1e-7);
        assertEquals(10d, vv.getAxisScale().getMaxValue(), 1e-7);

        vv.setRange(0d, 23d, 100);
        assertEquals(0d, vv.getAxisScale().getMinValue(), 1e-7);
        assertEquals(25d, vv.getAxisScale().getMaxValue(), 1e-7);

        System.out.println("chartaxis_maxvalue completed");
    }

    //@Test
    public void axisValueCalculator_test10() {
        BarChart.ValueAxisScaleCalculator ax = new BarChart.ValueAxisScaleCalculator();
        for (int i = 1; i < 101; i++) {
            ax.calcAxisScale(i, 10);
            System.out.println(ax);
        }
    }

    //@Test
    public void axisValueCalculator_test7() {
        BarChart.ValueAxisScaleCalculator ax = new BarChart.ValueAxisScaleCalculator();
        for (int i = 1; i < 101; i++) {
            ax.calcAxisScale(i, 6);
            System.out.println(ax);
        }
    }

    //@Test
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

    //@Test
    public void axisScaleCalculator_class() {
        BarChart.AxisScaleCalculator ac = new BarChart.ValueAxisScaleCalculator();
        BarChart.AxisScale as = new BarChart.AxisScale();
        for (int i = 1; i < 101; i++) {
            as.setScale(0d, i, 3);
            ac.calcAxisScale(as);
            System.out.println("" + i + " " + as);
        }
    }

    private String formatValue(double value) {
        return String.valueOf(value);
    }


    //@Test
    public void valueformatter_function() {

        BarChart.ValueFormatter vf = new BarChart.ValueFormatter();
        List<Double> valueList = Arrays.asList(
                1d, 1.44345d, 9.4678d, 10d, 99d, 534234d, 5123423d
        );

        List<String> expectedValueList = Arrays.asList(
                "1.0", "1.4", "9.5", "10", "99", "534234", "5.1e+06"
        );

        for (int i = 0; i < valueList.size(); i ++) {
            assertEquals(vf.formatValue(valueList.get(i)), expectedValueList.get(i));
        }
    }

    @Test
    public void chartlayout_first() {
        //prepare data
        BarChart.Series series = new BarChart.Series();
        series.addXY(1d, "value 1", 1d);
        series.addXY(2d, "value 2", 3d);
        series.addXY(3d, "value 3", 2d);
        series.addXY(4d, "value 4", 1d);
        series.addXY(5d, "value 5", 5d);

        Paint paint = new Paint();

        BarChart.ChartLayout cl = new BarChart.ChartLayout();
        cl.setAxesTextPaint(paint);
        int width = 300;
        int height = 30;

        cl.updateLayout(width, height, series);
        System.out.print("ChartRect = " + cl.getChartRect());
    }

}
