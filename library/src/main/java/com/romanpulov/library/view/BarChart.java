package com.romanpulov.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created on 05.10.2015.
 */

public class BarChart extends View {

    private static int AXES_PADDING = 30;

    private Paint mAxesPaint;
    private Paint mAxesTextPaint;

    private List<Series> mSeriesList = new ArrayList<>();

    public Series getSeries(int location) {
        return mSeriesList.get(location);
    }

    public Series addSeries() {
        Series newSeries = new Series();
        mSeriesList.add(newSeries);
        return newSeries;
    }

    public static class ChartValueBounds {
        public double getMinX() {
            return minX;
        }

        public double getMinY() {
            return minY;
        }

        public double getMaxX() {
            return maxX;
        }

        public double getMaxY() {
            return maxY;
        }

        private double minX;
        private double minY;
        private double maxX;
        private double maxY;

        public ChartValueBounds (double minX, double minY, double maxX, double maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        public void setBounds (double minX, double minY, double maxX, double maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        public double[] getBounds() {
            return new double[] {minX, minY, maxX, maxY};
        }
    }

    public static class Series {
        private List<ChartValue> mData = new ArrayList<>();

        private ChartValueBounds mValueBounds;

        public void updateValueBounds() {
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double maxY = Double.MIN_VALUE;
            for (ChartValue v : mData) {
                if (minX > v.x)
                    minX = v.x;
                if (minY > v.y)
                    minY = v.y;
                if (maxX < v.x)
                    maxX = v.x;
                if (maxY < v.y)
                    maxY = v.y;
            }
            if (null == mValueBounds) {
                mValueBounds = new ChartValueBounds(minX, minY, maxX, maxY);
            } else
                mValueBounds.setBounds(minX, minY, maxX, maxY);
        }

        public ChartValueBounds getValueBounds() {
            return mValueBounds;
        }

        public boolean add(ChartValue value) {
            return mData.add(value);
        }

        public ChartValue get(int location) {
            return mData.get(location);
        }

        public List<ChartValue> getList() {
            return mData;
        }

        public boolean addXY(Double x, String xLabel, Double y) {
            return add(new ChartValue(x, xLabel, y));
        }

    }

    public static class ChartValue implements Comparable {
        public Double x;
        public String xLabel;
        public Double y;

        @Override
        public int compareTo(Object another) {
            if (another instanceof ChartValue)
                return x.compareTo(((ChartValue) another).x);
            else
                return 0;
        }

        public ChartValue(Double x, String xLabel, Double y) {
            this.x = x;
            this.xLabel = xLabel;
            this.y = y;
        }
    }

    public void sortSeries(int seriesIndex) {
        Collections.sort(mSeriesList.get(seriesIndex).getList());
    }

    public static class ChartAxis {
        public static final int AXIS_TYPE_ARGUMENT = 0;
        public static final int AXIS_TYPE_VALUE = 1;

        private int mAxisType;
        private AxisScale mAxisScale = new AxisScale();

        public AxisScale getAxisScale() {
            return mAxisScale;
        }

        public ChartAxis(int axisType) {
            mAxisType = axisType;
        }

        public void setRange(double minValue, double maxValue, int maxCount) {

            if (minValue > 0)
                minValue = 0d;

            int iMaxValue = (int)maxValue;

            switch (mAxisType) {
                case AXIS_TYPE_ARGUMENT:
                    mAxisScale.setScale(minValue, iMaxValue, iMaxValue);
                    break;
                case AXIS_TYPE_VALUE:
                    mAxisScale.setScale(minValue, iMaxValue, maxCount);
                    AxisScaleCalculator ax = new BarChart.ValueAxisScaleCalculator();
                    ax.calcAxisScale(mAxisScale);
                    break;
                default:
                    mAxisScale.resetScale();
            }
        }

        @Override
        public String toString() {
            return "{AxisType=" +
                    mAxisType + ", " +
                    "AxisScale=" + mAxisScale.toString() +
                    "}";
        }
    }

    public static class AxisScale {
        private double mMinValue;
        private double mMaxValue;
        private int mCount;

        public double getMinValue() {
            return mMinValue;
        }

        public double getMaxValue() {
            return mMaxValue;
        }

        public int getCount() {
            return mCount;
        }

        public void setScale(double minValue, double maxValue, int count) {
            mMinValue = minValue;
            mMaxValue = maxValue;
            mCount = count;
        }

        public void resetScale() {
            mMinValue = 0d;
            mMaxValue = 0d;
            mCount = 0;
        }

        @Override
        public String toString() {
            return String.format(Locale.getDefault(), "[value=%f, count=%d]", mMaxValue, mCount);
        }
    }

    public interface AxisScaleCalculator {
        void calcAxisScale(AxisScale axisScale);
    }

    public static class ValueAxisScaleCalculator implements AxisScaleCalculator {
        private int mValue;
        private int mFirstNum;
        private int mSecondNum;
        private int mFactor;
        private int mScaleFactor;
        private double mMaxValue;
        private int mCount;

        public void calcAxisScale(int value, int maxCount) {
            mValue = value;

            //count correction
            if (maxCount > 10)
                maxCount = 10;
            else if (maxCount > 6)
                maxCount = 6;

            if (value == 1) {
                mMaxValue = 2.;
                mCount = 1;
                return;
            }

            if ((value > 1) && (value < 10) && (maxCount > 1)) {
                calcAxisScale(value * maxCount, maxCount);
                mValue = mValue / maxCount;
                mMaxValue = mMaxValue / maxCount;
                return;
            }

            //calc basics - first, second, factor
            mFirstNum = -1;
            mSecondNum = -1;
            mFactor = 0;
            while (value > 9) {
                if (value < 100) {
                    mFirstNum = value / 10;
                    mSecondNum = value - 10 * (value / 10);
                    break;
                }
                value = value / 10;
                mFactor ++;
            }

            //scale factor logic
            if (mFirstNum == 1)
                mScaleFactor = 2;
            else if (mFirstNum < 5)
                mScaleFactor = 5;
            else
                mScaleFactor = 10;
            mScaleFactor = mScaleFactor * (int)Math.pow(10, mFactor);

            //max value
            mMaxValue = mValue - (mValue % mScaleFactor) + mScaleFactor;
            //count
            mCount = (int)mMaxValue / mScaleFactor;

            if (maxCount < 10) {
                mCount = maxCount;
                if (mScaleFactor == 2)
                    mScaleFactor = 5;
                mMaxValue = (int)(mValue / maxCount);
                mMaxValue = (mMaxValue - (mMaxValue % mScaleFactor) + mScaleFactor) * mCount;
            }
        }

        public void calcAxisScaleAdaptive(int value, int maxCount) {
            //initial calc
            calcAxisScale(value, maxCount);
            //save calc data
            double origMaxValue = mMaxValue;
            int origCount = mCount;
            double origGap = mMaxValue - value;

            //try to find better values
            if ((mCount > 2) && (mCount < 10)) {
                calcAxisScale(value, mCount - 1);
                // revert original if no good
                if ((mMaxValue - value) >= origGap) {
                    mMaxValue = origMaxValue;
                    mCount = origCount;
                }
            }
        }

        @Override
        public String toString() {
            return "value=" + mValue + ", " +
                    "FirstNum=" + mFirstNum + ", " +
                    "SecondNum=" + mSecondNum + ", " +
                    "Factor=" + mFactor + ", " +
                    "ScaleFactor=" + mScaleFactor + ", " +
                    "MaxValue=" + mMaxValue + ", " +
                    "Count=" + mCount
                    ;
        }

        @Override
        public void calcAxisScale(AxisScale axisScale) {
            calcAxisScaleAdaptive((int)axisScale.getMaxValue(), axisScale.getCount());
            axisScale.setScale(0d, mMaxValue, mCount);
        }
    }

    public static class ValueFormatter {
        public static String formatValue(double value) {
            if (value < 10) {
                return String.format(Locale.getDefault(), "%1.1f", value);
            } else if (value < 1000000) {
                return String.format(Locale.getDefault(), "%.0f", value);
            } else
                return String.format(Locale.getDefault(), "%3.1e", value);
        }
    }


    public static class ChartLayout {
        //general margin
        static final int CHART_MARGIN = 5;
        static final int CHART_TEXT_MARGIN = 2;

        private Paint mAxesTextPaint;
        private Rect mAxesTextSymbolBounds = new Rect();
        //input data
        private int mWidth;
        private int mHeight;
        private Series mSeries;
        //calculated
        private Rect mChartRect = new Rect();
        private ChartAxis mXAxis = new ChartAxis(ChartAxis.AXIS_TYPE_ARGUMENT);
        private ChartAxis mYAxis = new ChartAxis(ChartAxis.AXIS_TYPE_VALUE);
        private boolean mIsLayoutValid = true;

        public Rect getChartRect() {
            return mChartRect;
        }

        public ChartAxis getXAxis() {
            return mXAxis;
        }

        public ChartAxis getYAxis() {
            return mYAxis;
        }

        public boolean getLayoutValid() {
            return mIsLayoutValid;
        }

        public void setAxesTextPaint(Paint axesTextPaint) {
            mAxesTextPaint = axesTextPaint;
            mAxesTextPaint.getTextBounds("0", 0, 1, mAxesTextSymbolBounds);
        }

        public void updateLayout(int width, int height, Series series) {
            mWidth = width;
            mHeight = height;
            mSeries = series;
            calcLayout();

            mIsLayoutValid = mChartRect.height() > 3 * (mAxesTextSymbolBounds.height() + CHART_TEXT_MARGIN);
        }

        private void calcLayout() {
            //chart body rect
            mChartRect.top = CHART_MARGIN;
            //  ensure bounds are calculated
            mSeries.updateValueBounds();
            //  calc according to bounds
            ChartValueBounds chartValueBounds = mSeries.getValueBounds();
            double maxY = chartValueBounds.getMaxY();
            String displayMaxY = ValueFormatter.formatValue(maxY);
            mChartRect.left = CHART_MARGIN + mAxesTextSymbolBounds.width() * displayMaxY.length();
            mChartRect.right = mWidth - CHART_MARGIN;
            mChartRect.bottom = mHeight - 2 * (mAxesTextSymbolBounds.height() + CHART_TEXT_MARGIN);

            //axes
            mXAxis.setRange(0, chartValueBounds.getMaxX(), (int)chartValueBounds.getMaxX());
            int yAxisCount = mHeight / (mAxesTextSymbolBounds.height() * 2);
            if (yAxisCount < 1)
                yAxisCount = 1;
            mYAxis.setRange(0, chartValueBounds.getMaxY(), yAxisCount);
        }
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

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
