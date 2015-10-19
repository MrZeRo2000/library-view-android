package com.romanpulov.library.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Created on 05.10.2015.
 */

public class BarChart extends View {

    private ChartLayout mChartLayout;
    private ChartDrawLayout mChartDrawLayout;

    private Paint mAxesPaint;
    private Paint mAxesTextPaint;
    private Paint mBarPaint;

    private SeriesList mSeriesList = new SeriesList();

    public Series getSeries(int location) {
        return mSeriesList.get(location);
    }

    public Series addSeries() {
        Series newSeries = new Series();
        mSeriesList.add(newSeries);
        return newSeries;
    }

    public void clearSeries() {
        mSeriesList.clear();
    }

    public static class ChartValueBounds {
        public double minX;
        public double minY;
        public double maxX;
        public double maxY;
        
        public ChartValueBounds() {
            resetBounds();
        }

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
        
        public void resetBounds() {
            minX = maxX = minY = maxY = 0d;
        }
    }

    public static class SeriesList implements Parcelable  {
        private List<Series> mData = new ArrayList<>();
        private ChartValueBounds mValueBounds;

        public SeriesList() {

        }
        
        public boolean add(Series series) {
            return mData.add(series);
        }

        public void clear() {
            mData.clear();
        }

        public Series get(int location) {
            return mData.get(location);
        }

        public int size() {
            return mData.size();
        }

        public void updateValueBounds() {
            if (mValueBounds == null)
                mValueBounds = new ChartValueBounds();
            else
                mValueBounds.resetBounds();

            for (Series series : mData) {
                series.updateValueBounds();
                ChartValueBounds seriesBounds = series.getValueBounds();
                if (seriesBounds.minX < mValueBounds.minX) 
                    mValueBounds.minX = seriesBounds.minX;
                if (seriesBounds.maxX > mValueBounds.maxX)
                    mValueBounds.maxX = seriesBounds.maxX;
                if (seriesBounds.minY < mValueBounds.minY)
                    mValueBounds.minY = seriesBounds.minY;
                if (seriesBounds.maxY > mValueBounds.maxY)
                    mValueBounds.maxY = seriesBounds.maxY;
            }
        }

        public ChartValueBounds getValueBounds() {
            return mValueBounds;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeList(mData);
        }

        private SeriesList(Parcel in) {
            mData = in.readArrayList(Series.class.getClassLoader());
        }

        public static final Parcelable.Creator<SeriesList> CREATOR = new Parcelable.Creator<SeriesList>() {
            public SeriesList createFromParcel(Parcel in) {
                return new SeriesList(in);
            }

            public SeriesList[] newArray(int size) {
                return new SeriesList[size];
            }
        };

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

        public boolean addXY(double x, String xLabel, double y) {
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
            return String.format(Locale.getDefault(), "[maxValue=%f, count=%d]", mMaxValue, mCount);
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

            if (value <= maxCount) {
                mMaxValue = maxCount;
                mCount = maxCount;
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
                mMaxValue = mValue / maxCount;
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

    public static class ChartValueFormatter {
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
        private static final int CHART_MARGIN = 5;
        private static final int CHART_TEXT_MARGIN = 2;
        private static final int BAR_ITEM_WIDTH = 100;
        private static final int AXIS_MARK_SIZE = 4;

        private Paint mAxesTextPaint;
        private Rect mAxesTextSymbolBounds = new Rect();
        //input data
        private int mWidth;
        private int mHeight;
        private DisplayMetrics mDisplayMetrics;
        private SeriesList mSeriesList;
        //calculated
        private int mCalcWidth;
        private int mCalcItemHeight;
        private int mChartMargin;
        private int mChartTextMargin;
        private int mBarItemWidth;
        private int mAxisMarkSize;
        private Rect mChartRect = new Rect();
        private ChartAxis mXAxis = new ChartAxis(ChartAxis.AXIS_TYPE_ARGUMENT);
        private ChartAxis mYAxis = new ChartAxis(ChartAxis.AXIS_TYPE_VALUE);
        private boolean mIsLayoutValid = true;

        public Rect getChartRect() {
            return mChartRect;
        }

        public int getChartMargin() {
            return mChartMargin;
        }

        public int getChartTextMargin() {
            return mChartTextMargin;
        }

        public int getBarItemWidth() {
            return mBarItemWidth;
        }

        public int getItemHeight() {
            return mCalcItemHeight;
        }

        public int getAxisMarkSize() {
            return mAxisMarkSize;
        }

        public int getCalcWidth() {
            return mCalcWidth;
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

        public Rect getAxesTextSymbolBounds() {
            return mAxesTextSymbolBounds;
        }

        public void setAxesTextPaint(Paint axesTextPaint) {
            mAxesTextPaint = axesTextPaint;
            mAxesTextPaint.getTextBounds("0", 0, 1, mAxesTextSymbolBounds);
        }

        public void updateLayout(int width, int height, DisplayMetrics displayMetrics, SeriesList seriesList) {
            mWidth = width;
            mHeight = height;
            mDisplayMetrics = displayMetrics;
            mSeriesList = seriesList;
            calcLayoutConstant();
            calcLayout();

            mIsLayoutValid = mChartRect.height() > 3 * (mAxesTextSymbolBounds.height() + mChartMargin);
        }

        public int dpToDIP(double value) {
            return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)value, mDisplayMetrics);
        }

        private void calcLayoutConstant() {
            mChartMargin = dpToDIP(CHART_MARGIN);
            mChartTextMargin = dpToDIP(CHART_TEXT_MARGIN);
            mBarItemWidth = dpToDIP(BAR_ITEM_WIDTH);
            mAxisMarkSize = dpToDIP(AXIS_MARK_SIZE);
        }

        private void calcLayout() {
            //chart body rect
            mChartRect.top = mChartMargin;
            //  ensure bounds are calculated
            mSeriesList.updateValueBounds();
            //  calc according to bounds
            ChartValueBounds chartValueBounds = mSeriesList.getValueBounds();
            double maxY = chartValueBounds.maxY;
            String displayMaxY = ChartValueFormatter.formatValue(maxY);

            //X setting range
            mXAxis.setRange(0, chartValueBounds.maxX, (int)chartValueBounds.maxX);
            //calculating according to range
            mChartRect.left = mChartMargin + mAxisMarkSize + mAxesTextSymbolBounds.width() * displayMaxY.length();
            mChartRect.right = (int)(mChartRect.left + getXAxis().getAxisScale().getMaxValue() * mBarItemWidth);
            mChartRect.bottom = mHeight - 2 * (mAxesTextSymbolBounds.height() + mChartTextMargin) - mChartMargin - mChartMargin;

            //Y calculations
            int yAxisCount = (mHeight - mChartMargin - mChartMargin) / ((mAxesTextSymbolBounds.height() + mChartTextMargin) * 2);
            if (yAxisCount < 1)
                yAxisCount = 1;
            mYAxis.setRange(0, chartValueBounds.maxY, yAxisCount);
            Log.d("BarChart", "Axis.setRange maxY=" + chartValueBounds.maxY + ", yAxisCount=" + yAxisCount);

            if (mChartRect.width() < 1)
                mCalcWidth = 0;
            else
                mCalcWidth = mChartRect.left + mChartRect.width() + mChartMargin * 2;
            mCalcItemHeight = mChartRect.height() / mYAxis.getAxisScale().getCount();
        }

        @Override
        public String toString() {
            return "{" +
                    "CalcWidth=" + mCalcWidth + ", " +
                    "CalcItemHeight=" + mCalcItemHeight + ", " +
                    "ChartMargin=" + mChartMargin + ", " +
                    "ChartTextMargin=" + mChartTextMargin + ", " +
                    "BarItemWidth=" + mBarItemWidth + ", " +
                    "AxisMarkSize=" + mAxisMarkSize + ", " +
                    "ChartRect=" + mChartRect + ", " +
                    "XAxis=" + mXAxis + ", " +
                    "YAxis=" + mYAxis + ", " +
                    "IsLayoutValid=" + mIsLayoutValid
                    ;
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
        mBarPaint = new Paint();

        mChartLayout = new ChartLayout();
        mChartLayout.setAxesTextPaint(mAxesPaint);

        mChartDrawLayout = new ChartDrawLayout(mChartLayout);
        mChartDrawLayout.setAxesTextPaint(mAxesTextPaint);
        mChartDrawLayout.setGradientColors(0xffbf00ff, 0xff000033);
    }

    public void updateSeriesListValueBounds() {
        mSeriesList.updateValueBounds();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("BarChart", "onSizeChanged (" + w + ", " + h + ")");
        updateChartLayout();
        /*
        Series series = null;
        if (mSeriesList.size() > 0)
            series = mSeriesList.get(0);
        mChartDrawLayout.updateLayout(series);
        */

        /*
        mSeriesList.updateValueBounds();
        mChartLayout.updateLayout(w, h, mSeriesList);
        Log.d("BarChart", "onSizeChanged (" + w + ", " + h + ")");
        Log.d("BarChart", "ChartLayout.XAxis=" + mChartLayout.getXAxis());
        */
    }

    public void updateChartLayout() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heigthWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        Log.d("BarChart", "updating layout for (" + widthWithoutPadding + ", " + heigthWithoutPadding + ")");
        mChartLayout.updateLayout(widthWithoutPadding, heigthWithoutPadding, getResources().getDisplayMetrics(), mSeriesList);
        Log.d("BarChart", "ChartLayout" + mChartLayout);

        Series series = null;
        if (mSeriesList.size() > 0)
            series = mSeriesList.get(0);
        mChartDrawLayout.updateLayout(series);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d("BarChart", "onMeasure");

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heigthWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        Log.d("BarChart", "size without padding (" + widthWithoutPadding + ", " + heigthWithoutPadding + ")");
        int newWidth = mChartLayout.getCalcWidth() + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(newWidth, height);
        Log.d("BarChart", "Setting measured dimension (" + newWidth + ", " + height + ")");

    }

    private static class ArgumentDrawData {
        float markX;
        float markY0;
        float markY;
        float labelX;
        float labelY;
        String labelText;
        Shader barShader;
        float barX0;
        float barY0;
        float barX;
        float barY;
    }

    private static class ValueDrawData {
        float markX0;
        float markX;
        float markY;
        float labelX;
        float labelY;
        String labelText;
    }

    private static class ChartDrawLayout {
        private ChartLayout mChartLayout;
        private List<ArgumentDrawData> mArgumentDrawDataList;
        private List<ValueDrawData> mValueDrawDataList;

        private Paint mAxesTextPaint;
        private int mGradientColor0;
        private int mGradientColor;

        public void setAxesTextPaint(Paint axesTextPaint) {
            mAxesTextPaint = axesTextPaint;
        }
        public void setGradientColors(int color0, int color) {
            mGradientColor0 = color0;
            mGradientColor = color;
        }

        public List<ArgumentDrawData> getArgumentDrawDataList() {
            return mArgumentDrawDataList;
        }

        public List<ValueDrawData> getValueDrawDataList() {
            return mValueDrawDataList;
        }

        public ChartDrawLayout(ChartLayout chartLayout) {
            mChartLayout = chartLayout;
        }

        public void updateLayout(Series series) {
            updateArgumentLayout(series);
            updateValueLayout();
        }

        public void updateValueLayout() {
            //lazy list creation
            if (mValueDrawDataList == null)
                mValueDrawDataList = new ArrayList<ValueDrawData>(mChartLayout.getYAxis().getAxisScale().getCount());
            else
                mValueDrawDataList.clear();

            final Rect chartRect = mChartLayout.getChartRect();
            int axisMarkSize = mChartLayout.getAxisMarkSize();

            int axisItemHeight = mChartLayout.getItemHeight();
            int y = chartRect.bottom;
            double axisValueStep = mChartLayout.getYAxis().getAxisScale().getMaxValue() / mChartLayout.getYAxis().getAxisScale().getCount();
            double axisValue = 0d;
            for (int i = 0; i <= mChartLayout.getYAxis().getAxisScale().getCount(); i++) {
                ValueDrawData valueDrawData = new ValueDrawData();

                valueDrawData.markX0 = chartRect.left - axisMarkSize;
                valueDrawData.markX = chartRect.left + axisMarkSize;
                valueDrawData.markY = y;
                valueDrawData.labelText = ChartValueFormatter.formatValue(axisValue);

                float textMeasure = mAxesTextPaint.measureText(valueDrawData.labelText, 0, valueDrawData.labelText.length());
                valueDrawData.labelX = chartRect.left - mChartLayout.getAxisMarkSize() - textMeasure;
                valueDrawData.labelY = y + mChartLayout.getAxesTextSymbolBounds().height() / 2;

                mValueDrawDataList.add(valueDrawData);

                y -= axisItemHeight;
                axisValue += axisValueStep;
            }
        }

        public void updateArgumentLayout(Series series) {
            //lazy list creation
            if (mArgumentDrawDataList == null) {
                mArgumentDrawDataList = new ArrayList<ArgumentDrawData>((int)mChartLayout.getXAxis().getAxisScale().getMaxValue());
            } else {
                mArgumentDrawDataList.clear();
            }

            final Rect chartRect = mChartLayout.getChartRect();
            //argument axis
            int axisItemWidth = mChartLayout.getBarItemWidth();
            int axisMarkSize = mChartLayout.getAxisMarkSize();
            int x = chartRect.left + mChartLayout.getBarItemWidth() / 2;

            for (int i = 0; i < mChartLayout.getXAxis().getAxisScale().getMaxValue(); i++) {
                ArgumentDrawData argumentDrawData = new ArgumentDrawData();

                //draw mark
                argumentDrawData.markX = x;
                argumentDrawData.markY0 = chartRect.bottom - axisMarkSize;
                argumentDrawData.markY = chartRect.bottom + axisMarkSize;

                //draw label
                if (series != null) {
                    String labelText = series.get(i).xLabel;
                    float textMeasure = mAxesTextPaint.measureText(labelText, 0, labelText.length());

                    //handling of long labels
                    int textLength = labelText.length();
                    if (textMeasure > mChartLayout.getBarItemWidth()) {
                        //need to truncate text size
                        double textRate = textLength * mChartLayout.getBarItemWidth() / textMeasure;
                        labelText = labelText.substring(0, (int)textRate);
                        textMeasure = mChartLayout.getBarItemWidth();
                    }

                    argumentDrawData.labelX = x - textMeasure / 2;
                    argumentDrawData.labelY = chartRect.bottom + mChartLayout.getChartTextMargin() + mAxesTextPaint.getTextSize();
                    argumentDrawData.labelText = labelText;
                }

                //bar
                double barHeight = series.get(i).y * chartRect.height() / mChartLayout.getYAxis().getAxisScale().getMaxValue();
                argumentDrawData.barX0 = x - mChartLayout.getBarItemWidth() / 4;
                argumentDrawData.barY0 = (float) (chartRect.bottom - barHeight);
                argumentDrawData.barX = x + mChartLayout.getBarItemWidth() / 4;
                argumentDrawData.barY = chartRect.bottom + 1;
                argumentDrawData.barShader = new LinearGradient(
                        argumentDrawData.barX0, argumentDrawData.barY0, argumentDrawData.barX0, argumentDrawData.barY,
                        mGradientColor0, mGradientColor,
                        Shader.TileMode.CLAMP);

                //add to list
                mArgumentDrawDataList.add(argumentDrawData);

                //next step
                x += axisItemWidth;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.d("BarChart", "onDraw");
        long startTime = System.nanoTime();

        final Rect chartRect = mChartLayout.getChartRect();
        //argument
        for (ArgumentDrawData dd : mChartDrawLayout.getArgumentDrawDataList()) {
            //mark
            canvas.drawLine(dd.markX, dd.markY0, dd.markX, dd.markY, mAxesPaint);
            //label
            canvas.drawText(dd.labelText, dd.labelX, dd.labelY, mAxesTextPaint);
            //bar
            mBarPaint.setShader(dd.barShader);
            mBarPaint.setStyle(Paint.Style.FILL);
            canvas.drawRect(dd.barX0, dd.barY0, dd.barX, dd.barY, mBarPaint);
            mBarPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(dd.barX0, dd.barY0, dd.barX, dd.barY, mBarPaint);
        }
        //argument axis
        canvas.drawLine(chartRect.left, chartRect.bottom, chartRect.right, chartRect.bottom,  mAxesPaint);

        //value
        for (ValueDrawData dd : mChartDrawLayout.getValueDrawDataList()) {
            //mark
            canvas.drawLine(dd.markX0, dd.markY, dd.markX, dd.markY, mAxesPaint);
            //label
            canvas.drawText(dd.labelText, dd.labelX, dd.labelY, mAxesPaint);
        }
        //value axis
        canvas.drawLine(chartRect.left, chartRect.top, chartRect.left, chartRect.bottom, mAxesPaint);

        long stopTime = System.nanoTime();
        long elapsedTime = stopTime - startTime;
        Log.d("BarChart", "onDraw executed in " + elapsedTime + " ns");
    }

    static class SavedState extends BaseSavedState {
        SeriesList seriesList;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            seriesList = in.readParcelable(SeriesList.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(seriesList, 0);
        }

        public static final Parcelable.Creator<SavedState> CREATOR
                = new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);

        ss.seriesList = mSeriesList;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());

        mSeriesList = ss.seriesList;
        updateSeriesListValueBounds();
        updateChartLayout();

        requestLayout();
    }
}
