
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.romanpulov.library.view.BarChart;

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
        assertEquals(0d, va.getMinValue(), 1e-7);
        assertEquals(8d, va.getMaxValue(), 1e-7);

        BarChart.ChartAxis vv = new BarChart.ChartAxis(BarChart.ChartAxis.AXIS_TYPE_VALUE);
        vv.setRange(0d, 8d, 100);
        assertEquals(0d, vv.getMinValue(), 1e-7);
        assertEquals(10d, vv.getMaxValue(), 1e-7);

        vv.setRange(0d, 23d, 100);
        assertEquals(0d, vv.getMinValue(), 1e-7);
        assertEquals(25d, vv.getMaxValue(), 1e-7);

        System.out.println("chartaxis_maxvalue completed");
    }

    @Test
    public void axisValueCalculator_first() {
        BarChart.AxisValueCalculator ax = new BarChart.AxisValueCalculator();
        for (int i = 11; i < 101; i++) {
            ax.calcForValue(i);
            System.out.println(ax);
        }
    }

}
