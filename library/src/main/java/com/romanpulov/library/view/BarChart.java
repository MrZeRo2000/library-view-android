package com.romanpulov.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created on 05.10.2015.
 */

public class BarChart extends View {

    private static int AXES_PADDING = 30;

    private Paint mAxesPaint;

    public BarChart(Context context) {
        super(context, null);
    }

    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        mAxesPaint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawText(toString(), 50, 50, mAxesPaint);
    }
}
