
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

    @Test
    public void firstTest() {
        assertFalse(1==2);
    }

    @Test
    public void chart_sorting() {
        BarChart bc = new BarChart(null);
        assertNotNull(bc);
        List<BarChart.ChartValue> series = bc.addSeries();
        series.add(new BarChart.ChartValue(3., "Three"));
        series.add(new BarChart.ChartValue(1., "One"));
        series.add(new BarChart.ChartValue(2., "Two"));
        assertEquals(3., series.get(0).value, 1e-7);
        bc.sortSeries(0);
        assertEquals(1., series.get(0).value, 1e-7);
        assertEquals(2., series.get(1).value, 1e-7);
        assertEquals(3., series.get(2).value, 1e-7);
    }
}
