package com.romanpulov.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created on 05.10.2015.
 */

public class BarChart extends View {

    private static int AXES_PADDING = 30;

    private Paint mAxesPaint;
    private Paint mAxesTextPaint;

    private List<List<ChartValue>> mSeries = new ArrayList<>();

    public List<ChartValue> getSeries(int location) {
        return mSeries.get(location);
    }

    public List<ChartValue> addSeries() {
        List<ChartValue> newSeries = new ArrayList<>();
        mSeries.add(newSeries);
        return newSeries;
    }

    public static class ChartValue implements Comparable {
        public Double value;
        public String displayValue;

        public ChartValue(Double value, String displayValue) {
            this.value = value;
            this.displayValue = displayValue;
        }

        @Override
        public int compareTo(Object another) {
            if (another instanceof ChartValue)
                return value.compareTo(((ChartValue) another).value);
            else
                return 0;
        }
    }

    public void sortSeries(int seriesIndex) {
        Collections.sort(mSeries.get(seriesIndex));
    }

    public BarChart(Context context) {
        super(context, null);
    }

    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        mAxesPaint = new Paint();
        mAxesPaint.setStyle(Paint.Style.STROKE);
        mAxesTextPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int width = getWidth();
        final int height = getHeight();

        Rect axesTextBounds = new Rect();
        mAxesPaint.getTextBounds("00000", 0, 4, axesTextBounds);

        int offsetLeft = axesTextBounds.width();
        int offsetTop = axesTextBounds.height() / 2;
        int offsetBottom = 2 * axesTextBounds.height();
        int offsetRight  = axesTextBounds.height() / 2;

        Rect chartBounds = new Rect(offsetLeft, offsetTop, width - offsetRight, height - offsetBottom);
        canvas.drawRect(chartBounds, mAxesPaint);

        canvas.drawText(toString(), 0, 0, mAxesTextPaint);
    }
}
