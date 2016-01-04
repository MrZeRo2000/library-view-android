package com.romanpulov.library.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
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
    private static final int DEFAULT_AXIS_COLOR = Color.BLACK;
    private static final int DEFAULT_AXIS_WIDTH = 1;
    private static final int DEFAULT_AXIS_TEXT_COLOR = Color.BLACK;
    private static final int DEFAULT_AXIS_TEXT_SIZE = 14;
    private static final int DEFAULT_BAR_COLOR = Color.BLACK;
    private static final boolean DEFAULT_GRID_VISIBLE = false;
    private static final int DEFAULT_GRID_COLOR = Color.GRAY;
    private static final int DEFAULT_GRADIENT_COLOR = Color.BLACK;
    private static final int LABEL_WINDOW_FRAME_DELAY = 200;
    private static final int LABEL_WINDOW_VALUE_DURATION = 1000;
    private static final int LABEL_WINDOW_TEXT_WIDTH_MARGIN = 8;
    private static final int LABEL_WINDOW_TEXT_HEIGHT_MARGIN = 4;
    private static final int LABEL_WINDOW_HEIGHT_OFFSET = 6;
    private static final boolean DEFAULT_SHOW_LABEL_ON_CLICK = false;
    private static final int DEFAULT_VALUE_LABEL_BACKGROUND_COLOR = 0xFFFFFF96;
    private static final int DEFAULT_VALUE_LABEL_BORDER_COLOR = Color.BLACK;
    private static final int DEFAULT_VALUE_LABEL_TEXT_COLOR = Color.BLUE;
    private static final int DEFAULT_VALUE_LABEL_TEXT_SIZE = 14;

    private ChartLayout mChartLayout;
    private ChartDrawLayout mChartDrawLayout;

    private Paint mAxesPaint;
    private Paint mAxesTextPaint;
    private Paint mXGridPaint;
    private Paint mYGridPaint;
    private Paint mBarPaint;
    //label
    private Paint mLabelBackgroundPaint;
    private Paint mLabelBorderPaint;
    private Paint mLabelTextPaint;

    private int mDefaultGradientColor0;
    private int mDefaultGradientColor;

    private boolean mShowLabelOnClick;
    private PointerTracker mPointerTracker;

    private ValueAnimator fadeAnimator;
    static
    {
        ValueAnimator.setFrameDelay(LABEL_WINDOW_FRAME_DELAY);
    }
    private ValueLabelDrawable valueLabelDrawable;

    public boolean getShowLabelOnClick() {
        return mShowLabelOnClick;
    }
    public void setShowLabelOnClick(boolean value) {
        mShowLabelOnClick = value;
    }

    private SeriesList mSeriesList;

    public Series getSeries(int location) {
        return mSeriesList.get(location);
    }

    public Series addSeries() {
        Series newSeries = new Series();
        mSeriesList.add(newSeries);
        newSeries.setGradientColors(mDefaultGradientColor0, mDefaultGradientColor);
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

    public static class SeriesList implements Iterable<Series>, Parcelable  {
        private List<Series> mData = new ArrayList<>();

        @Override
        public Iterator<Series> iterator() {
            return mData.iterator();
        }

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
            mData = in.readParcelable(SeriesList.class.getClassLoader());
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
    
    public static class Series implements Iterable<ChartValue>, Parcelable  {
        private List<ChartValue> mData = new ArrayList<>();

        public Series() {

        }

        private int mGradientColor0 = Color.BLACK;
        private int mGradientColor = Color.BLACK;

        public void setGradientColors(int color0, int color) {
            mGradientColor0 = color0;
            mGradientColor = color;
        }

        public int getGradientColor0() {
            return mGradientColor0;
        }

        public int getGradientColor() {
            return mGradientColor;
        }

        private ChartValueBounds mValueBounds;

        public void updateValueBounds() {
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double maxY = Double.MIN_VALUE;
            for (ChartValue chartValue : mData) {
                if (minX > chartValue.x)
                    minX = chartValue.x;
                if (minY > chartValue.y)
                    minY = chartValue.y;
                if (maxX < chartValue.x)
                    maxX = chartValue.x;
                if (maxY < chartValue.y)
                    maxY = chartValue.y;
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(mData);
            dest.writeInt(mGradientColor0);
            dest.writeInt(mGradientColor);
        }

        public static final Parcelable.Creator<Series> CREATOR
                = new Parcelable.Creator<Series>() {
            public Series createFromParcel(Parcel in) {
                return new Series(in);
            }

            public Series[] newArray(int size) {
                return new Series[size];
            }
        };

        private Series(Parcel in) {
            in.readTypedList(mData, ChartValue.CREATOR);
            mGradientColor0 = in.readInt();
            mGradientColor = in.readInt();
        }

        @Override
        public Iterator<ChartValue> iterator() {
            return mData.iterator();
        }
    }

    public static class ChartValue implements Comparable<ChartValue>, Parcelable {
        public Double x;
        public String xLabel;
        public Double y;

        @Override
        public int compareTo(ChartValue another) {
            return x.compareTo(another.x);
        }

        public ChartValue(Double x, String xLabel, Double y) {
            this.x = x;
            this.xLabel = xLabel;
            this.y = y;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeDouble(x);
            dest.writeString(xLabel);
            dest.writeDouble(y);
        }

        public static final Parcelable.Creator<ChartValue> CREATOR
                = new Parcelable.Creator<ChartValue>() {
            public ChartValue createFromParcel(Parcel in) {
                return new ChartValue(in);
            }

            public ChartValue[] newArray(int size) {
                return new ChartValue[size];
            }
        };

        private ChartValue(Parcel in) {
            x = in.readDouble();
            xLabel = in.readString();
            y = in.readDouble();
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
                if (value - (int)value > 1e-7)
                    return String.format(Locale.getDefault(), "%1.1f", value);
                else
                    return String.format(Locale.getDefault(), "%d", (int)value);
            } else if (value < 1000000) {
                return String.format(Locale.getDefault(), "%.0f", value);
            } else
                return String.format(Locale.getDefault(), "%3.1e", value);
        }
    }

    public final static class ChartLayout {
        //general margin
        private static final int CHART_MARGIN = 5;
        private static final int CHART_TEXT_MARGIN = 2;
        private static final int MIN_BAR_ITEM_WIDTH = 20;
        private static final int DEFAULT_BAR_ITEM_WIDTH = 100;
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

        public ChartLayout(DisplayMetrics displayMetrics) {
            mDisplayMetrics = displayMetrics;
            calcLayoutConstant();
        }

        private void calcLayoutConstant() {
            mChartMargin = dpToDIP(CHART_MARGIN);
            mChartTextMargin = dpToDIP(CHART_TEXT_MARGIN);
            mBarItemWidth = dpToDIP(DEFAULT_BAR_ITEM_WIDTH);
            mAxisMarkSize = dpToDIP(AXIS_MARK_SIZE);
        }

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

        public void setBarItemWidth(int value) {
            mBarItemWidth = value < MIN_BAR_ITEM_WIDTH ? MIN_BAR_ITEM_WIDTH : value;
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

        public void updateLayout(int width, int height, SeriesList seriesList) {
            mWidth = width;
            mHeight = height;
            mSeriesList = seriesList;

            calcLayout();

            mIsLayoutValid = mChartRect.height() > 3 * (mAxesTextSymbolBounds.height() + mChartMargin);
        }

        public int dpToDIP(double value) {
            return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float)value, mDisplayMetrics);
        }

        private void calcLayout() {
            //chart body rect
            mChartRect.top = mChartMargin + mAxesTextSymbolBounds.height() / 2;
            //  ensure bounds are calculated
            mSeriesList.updateValueBounds();
            //  calc according to bounds
            ChartValueBounds chartValueBounds = mSeriesList.getValueBounds();
            double maxY = chartValueBounds.maxY;
            String displayMaxY = ChartValueFormatter.formatValue(maxY);

            //X setting range
            mXAxis.setRange(0, chartValueBounds.maxX, (int)chartValueBounds.maxX);
            //calculating according to range
            mChartRect.left = mChartMargin + mAxisMarkSize + mAxesTextSymbolBounds.width() * (displayMaxY.length() + 2);
            mChartRect.right = (int)(mChartRect.left + getXAxis().getAxisScale().getMaxValue() * mBarItemWidth);
            mChartRect.bottom = mHeight - 2 * (mAxesTextSymbolBounds.height() + mChartTextMargin) - mChartMargin - mChartMargin - mAxesTextSymbolBounds.height() / 2;

            //Y calculations
            int yAxisCount = (mHeight - mChartMargin - mChartMargin) / ((mAxesTextSymbolBounds.height() + mChartTextMargin) * 2);
            if (yAxisCount < 1)
                yAxisCount = 1;
            mYAxis.setRange(0, chartValueBounds.maxY, yAxisCount);

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

    private final static class ArgumentDrawData {
        int markX;
        int markY0;
        int markY;
        int labelX;
        int labelY;
        String labelText;
    }

    private final static class ValueDrawData {
        int markX0;
        int markX;
        int markY;
        int labelX;
        int labelY;
        String labelText;
    }

    private final static class SeriesDrawData {
        String valueText;
        Shader barShader;
        int barX0;
        int barY0;
        int barX;
        int barY;
        boolean containsBarPoint(int pointX, int pointY) {
            return pointX >= barX0 && pointX <= barX && pointY >= barY0;
        }
    }

    private final static class ChartDrawLayout {
        private ChartLayout mChartLayout;
        private List<ArgumentDrawData> mArgumentDrawDataList;
        private List<ValueDrawData> mValueDrawDataList;
        private List<List<SeriesDrawData>> mSeriesDrawDataList;

        private Paint mAxesTextPaint;

        public void setAxesTextPaint(Paint axesTextPaint) {
            mAxesTextPaint = axesTextPaint;
        }

        public Iterable<ArgumentDrawData> getArgumentDrawDataList() {
            return mArgumentDrawDataList;
        }

        public SeriesDrawData getSeriesDrawDataItemAtPos(int posX, int posY) {
            for (List<SeriesDrawData> listItem : mSeriesDrawDataList) {
                for (SeriesDrawData item : listItem) {
                    if ((item != null) && (item.containsBarPoint(posX, posY)))
                        return item;
                }
            }
            return null;
        }

        public List<ValueDrawData> getValueDrawDataList() {
            return mValueDrawDataList;
        }

        public List<List<SeriesDrawData>> getSeriesDrawDataList() {
            return mSeriesDrawDataList;
        }

        public ChartDrawLayout(ChartLayout chartLayout) {
            mChartLayout = chartLayout;
        }

        public void updateLayout(SeriesList seriesList) {
            updateArgumentLayout(seriesList);
            updateSeriesDrawData(seriesList);
            updateValueLayout();
        }

        private int getRectY(Rect rect, float value) {
            return (int) (rect.bottom - (value * rect.height()) / mChartLayout.getYAxis().getAxisScale().getMaxValue());
        }

        public void updateSeriesDrawData(SeriesList seriesList) {
            //lazy list creation
            if (mSeriesDrawDataList == null)
                mSeriesDrawDataList = new ArrayList<>();
            else
                mSeriesDrawDataList.clear();

            if (seriesList.size() > 0) {
                final Rect chartRect = mChartLayout.getChartRect();
                final int barWidth = mChartLayout.getBarItemWidth() / 2 / seriesList.size();
                int barStartLeft = 0;

                for (Series series : seriesList) {
                    List<SeriesDrawData> seriesDrawDataList = new ArrayList<>();
                    int barLeft = chartRect.left + barStartLeft + mChartLayout.getBarItemWidth() / 4;
                    for (ArgumentDrawData argumentDrawData : mArgumentDrawDataList) {
                        SeriesDrawData seriesDrawData = null;

                        for (ChartValue chartValue : series) {
                            if (chartValue.xLabel.equals(argumentDrawData.labelText)) {
                                seriesDrawData = new SeriesDrawData();
                                seriesDrawData.valueText = String.valueOf(chartValue.y.intValue());

                                //bar
                                seriesDrawData.barX0 = barLeft;
                                seriesDrawData.barY0 = getRectY(chartRect, chartValue.y.floatValue());
                                seriesDrawData.barX = barLeft + barWidth;
                                seriesDrawData.barY = chartRect.bottom;
                                seriesDrawData.barShader = new LinearGradient(
                                        seriesDrawData.barX0, seriesDrawData.barY0, seriesDrawData.barX0, seriesDrawData.barY,
                                        series.getGradientColor0(), series.getGradientColor(),
                                        Shader.TileMode.CLAMP);

                                break;
                            }
                        }
                        barLeft += mChartLayout.getBarItemWidth();
                        seriesDrawDataList.add(seriesDrawData);
                    }
                    barStartLeft += barWidth;
                    mSeriesDrawDataList.add(seriesDrawDataList);
                }
            }
        }

        public void updateValueLayout() {
            //lazy list creation
            if (mValueDrawDataList == null)
                mValueDrawDataList = new ArrayList<>(mChartLayout.getYAxis().getAxisScale().getCount());
            else
                mValueDrawDataList.clear();

            final Rect chartRect = mChartLayout.getChartRect();
            int axisMarkSize = mChartLayout.getAxisMarkSize();

            float axisValueStep = (float)mChartLayout.getYAxis().getAxisScale().getMaxValue() / mChartLayout.getYAxis().getAxisScale().getCount();
            float axisValue = 0f;
            int axisCount = mChartLayout.getYAxis().getAxisScale().getCount();
            for (int i = 0; i <= axisCount; i++) {
                int y = getRectY(chartRect, axisValue);

                ValueDrawData valueDrawData = new ValueDrawData();

                valueDrawData.markX0 = chartRect.left - axisMarkSize;
                valueDrawData.markX = chartRect.left + axisMarkSize;
                valueDrawData.markY = y;
                valueDrawData.labelText = ChartValueFormatter.formatValue(axisValue);

                float textMeasure = mAxesTextPaint.measureText(valueDrawData.labelText, 0, valueDrawData.labelText.length());
                valueDrawData.labelX = (int)(chartRect.left - mChartLayout.getAxisMarkSize() - textMeasure);
                valueDrawData.labelY = y + mChartLayout.getAxesTextSymbolBounds().height() / 2;

                mValueDrawDataList.add(valueDrawData);

                axisValue += axisValueStep;
            }
        }

        public void updateArgumentLayout(SeriesList seriesList) {
            //lazy list creation
            if (mArgumentDrawDataList == null) {
                mArgumentDrawDataList = new ArrayList<>((int)mChartLayout.getXAxis().getAxisScale().getMaxValue());
            } else {
                mArgumentDrawDataList.clear();
            }

            final Rect chartRect = mChartLayout.getChartRect();
            //argument axis
            int axisItemWidth = mChartLayout.getBarItemWidth();
            int axisMarkSize = mChartLayout.getAxisMarkSize();
            int x = chartRect.left + axisItemWidth / 2;

            for (Series series : seriesList) {
                for (ChartValue chartValue : series) {
                    //find existing chart value
                    boolean skipChartValue = false;
                    for (ArgumentDrawData argumentDrawData : mArgumentDrawDataList) {
                        if (argumentDrawData.labelText.equals(chartValue.xLabel)) {
                            skipChartValue = true;
                            break;
                        }
                    }

                    //add if not found
                    if (!skipChartValue) {
                        //create new item
                        ArgumentDrawData argumentDrawData = new ArgumentDrawData();

                        //draw mark
                        argumentDrawData.markX = x + axisItemWidth / 2;
                        argumentDrawData.markY0 = chartRect.bottom - axisMarkSize;
                        argumentDrawData.markY = chartRect.bottom + axisMarkSize;

                        //draw label
                        String labelText = chartValue.xLabel;
                        float textMeasure = mAxesTextPaint.measureText(labelText, 0, labelText.length());

                        //handling of long labels
                        int textLength = labelText.length();
                        if (textMeasure > mChartLayout.getBarItemWidth()) {
                            //need to truncate text size
                            double textRate = textLength * mChartLayout.getBarItemWidth() / textMeasure;
                            labelText = labelText.substring(0, (int) textRate);
                            textMeasure = mChartLayout.getBarItemWidth();
                        }

                        argumentDrawData.labelX = (int)(x - textMeasure / 2);
                        argumentDrawData.labelY = (int)(chartRect.bottom + mChartLayout.getChartTextMargin() + mAxesTextPaint.getTextSize());
                        argumentDrawData.labelText = labelText;

                        //add to list
                        mArgumentDrawDataList.add(argumentDrawData);
                        //next step
                        x += axisItemWidth;
                    }
                }
            }
        }
    }

    public BarChart(Context context) {
        this(context, null);
    }

    public BarChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        mSeriesList = new SeriesList();

        mAxesPaint = new Paint();
        mAxesPaint.setStyle(Paint.Style.STROKE);
        mAxesTextPaint = new Paint();
        mBarPaint = new Paint();

        mLabelBackgroundPaint = new Paint();
        mLabelBackgroundPaint.setAntiAlias(false);
        mLabelBackgroundPaint.setStyle(Paint.Style.FILL);

        mLabelBorderPaint = new Paint();
        mLabelBorderPaint.setAntiAlias(false);
        mLabelBorderPaint.setStyle(Paint.Style.STROKE);

        mLabelTextPaint = new Paint();
        mLabelTextPaint.setAntiAlias(false);

        //read resources
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BarChart);
        //axes
        mAxesPaint.setColor(a.getColor(R.styleable.BarChart_axisColor, DEFAULT_AXIS_COLOR));
        mAxesPaint.setStrokeWidth(a.getDimensionPixelOffset(R.styleable.BarChart_axisWidth, (int) (DEFAULT_AXIS_WIDTH * getResources().getDisplayMetrics().density)));
        mAxesTextPaint.setColor(a.getColor(R.styleable.BarChart_textColor, DEFAULT_AXIS_TEXT_COLOR));
        mAxesTextPaint.setTextSize(a.getDimensionPixelSize(R.styleable.BarChart_textSize, DEFAULT_AXIS_TEXT_SIZE) * getResources().getDisplayMetrics().density);
        Typeface tf = Typeface.create("", a.getInt(R.styleable.BarChart_textStyle, Typeface.NORMAL));
        mAxesTextPaint.setTypeface(tf);
        //bar
        mBarPaint.setColor(a.getColor(R.styleable.BarChart_barColor, DEFAULT_BAR_COLOR));
        //x grid
        boolean xGridVisible = a.getBoolean(R.styleable.BarChart_xGridVisible, DEFAULT_GRID_VISIBLE);
        if (xGridVisible) {
            mXGridPaint = new Paint();
            mXGridPaint.setStyle(Paint.Style.STROKE);
            mXGridPaint.setColor(a.getColor(R.styleable.BarChart_xGridColor, DEFAULT_GRID_COLOR));
            mXGridPaint.setStrokeWidth(a.getDimensionPixelOffset(R.styleable.BarChart_xGridWidth, (int) (DEFAULT_AXIS_WIDTH * getResources().getDisplayMetrics().density)));
        }
        //y grid
        boolean yGridVisible = a.getBoolean(R.styleable.BarChart_yGridVisible, DEFAULT_GRID_VISIBLE);
        if (yGridVisible) {
            mYGridPaint = new Paint();
            mYGridPaint.setStyle(Paint.Style.STROKE);
            mYGridPaint.setColor(a.getColor(R.styleable.BarChart_yGridColor, DEFAULT_GRID_COLOR));
            mYGridPaint.setStrokeWidth(a.getDimensionPixelOffset(R.styleable.BarChart_yGridWidth, (int) (DEFAULT_AXIS_WIDTH * getResources().getDisplayMetrics().density)));
        }
        //gradient colors
        mDefaultGradientColor0 = a.getColor(R.styleable.BarChart_defaultGradientColor0, DEFAULT_GRADIENT_COLOR);
        mDefaultGradientColor = a.getColor(R.styleable.BarChart_defaultGradientColor, DEFAULT_GRADIENT_COLOR);
        //label on click
        mShowLabelOnClick = a.getBoolean(R.styleable.BarChart_showLabelOnClick, DEFAULT_SHOW_LABEL_ON_CLICK);
        //label
        mLabelBackgroundPaint.setColor(a.getColor(R.styleable.BarChart_valueLabelBackgroundColor, DEFAULT_VALUE_LABEL_BACKGROUND_COLOR));
        mLabelBorderPaint.setColor(a.getColor(R.styleable.BarChart_valueLabelBorderColor, DEFAULT_VALUE_LABEL_BORDER_COLOR));
        mLabelTextPaint.setColor(a.getColor(R.styleable.BarChart_valueLabelTextColor, DEFAULT_VALUE_LABEL_TEXT_COLOR));
        mLabelTextPaint.setTextSize(a.getDimensionPixelSize(R.styleable.BarChart_valueLabelTextSize, DEFAULT_VALUE_LABEL_TEXT_SIZE)* getResources().getDisplayMetrics().density);
        //resource read complete
        a.recycle();

        mChartLayout = new ChartLayout(getResources().getDisplayMetrics());
        mChartLayout.setAxesTextPaint(mAxesTextPaint);
        mChartDrawLayout = new ChartDrawLayout(mChartLayout);
        mChartDrawLayout.setAxesTextPaint(mAxesTextPaint);
    }

    private class ValueLabelDrawable extends Drawable {
        private int mAlpha = 0xFF;
        private SeriesDrawData mData;
        private Rect mWindowBounds;
        private float mTextX;
        private float mTextY;

        public ValueLabelDrawable(SeriesDrawData data) {
            mData = data;
            calcBounds();
        }

        private void calcBounds() {
            Rect measureTextBounds = new Rect();
            DisplayMetrics displayMetrics = BarChart.this.getResources().getDisplayMetrics();
            mLabelTextPaint.getTextBounds(mData.valueText, 0, mData.valueText.length(), measureTextBounds);
            int windowWidth = measureTextBounds.width() + (int)(2 * LABEL_WINDOW_TEXT_WIDTH_MARGIN * displayMetrics.density);
            int windowHeight = measureTextBounds.height() + (int)(2 * LABEL_WINDOW_TEXT_HEIGHT_MARGIN * displayMetrics.density);
            int windowLeft = (mData.barX0 + mData.barX) / 2 - windowWidth / 2;
            int windowTop =  mData.barY0 - windowHeight - (int)(LABEL_WINDOW_HEIGHT_OFFSET * displayMetrics.density);
            mWindowBounds = new Rect(
                    windowLeft,
                    windowTop,
                    windowLeft + windowWidth,
                    windowTop + windowHeight
            );
            mTextX = windowLeft + (windowWidth - measureTextBounds.width()) / 2;
            mTextY = windowTop + (windowHeight + measureTextBounds.height()) / 2;
        }

        @Override
        public void draw(Canvas canvas) {
            //background
            mLabelBackgroundPaint.setAlpha(mAlpha);
            canvas.drawRect(mWindowBounds, mLabelBackgroundPaint);
            //frame
            mLabelBorderPaint.setAlpha(mAlpha);
            canvas.drawRect(mWindowBounds, mLabelBorderPaint);
            //text
            mLabelTextPaint.setAlpha(mAlpha);
            canvas.drawText(
                    mData.valueText,
                    mTextX,
                    mTextY,
                    mLabelTextPaint);
        }

        @Override
        public void setAlpha(int alpha) {
            mAlpha = alpha;
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getOpacity() {
            return mAlpha;
        }
    }

    private static class PointerTracker {
        private static int MOVE_THRESHOLD = 8;
        int oldX;
        int oldY;
        int oldMotionAction;
        int x;
        int y;
        int motionAction;

        public boolean isMoved() {
            return (Math.abs(oldX - x) > MOVE_THRESHOLD) || (Math.abs(oldY - y) > MOVE_THRESHOLD);
        }

        private void saveOld() {
            oldX = x;
            oldY = y;
            oldMotionAction = motionAction;
        }

        public void setMotionAction(int motionAction, int x, int y) {
            if (this.motionAction != motionAction) {
                saveOld();
                this.motionAction = motionAction;
            }
            this.x = x;
            this.y = y;
        }
    }

    public int getBarItemWidth() {
        return mChartLayout.getBarItemWidth();
    }

    public void setBarItemWidth(int value) {
        mChartLayout.setBarItemWidth(value);
    }

    public void updateSeriesListValueBounds() {
        mSeriesList.updateValueBounds();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateChartLayout();
    }

    public void updateChartLayout() {
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        mChartLayout.updateLayout(widthWithoutPadding, heightWithoutPadding, mSeriesList);
        mChartDrawLayout.updateLayout(mSeriesList);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getMeasuredHeight();

        int newWidth = mChartLayout.getCalcWidth() + getPaddingLeft() + getPaddingRight();
        setMeasuredDimension(newWidth, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final Rect chartRect = mChartLayout.getChartRect();
        final Iterable<ArgumentDrawData> argumentDrawDataList = mChartDrawLayout.getArgumentDrawDataList();
        final Iterable<ValueDrawData> valueDrawDataList = mChartDrawLayout.getValueDrawDataList();
        final List<List<SeriesDrawData>> seriesDrawDataList = mChartDrawLayout.getSeriesDrawDataList();

        //argument grid
        if ((mXGridPaint != null) && (argumentDrawDataList != null))
            for (ArgumentDrawData dd : argumentDrawDataList) {
                canvas.drawLine(dd.markX, chartRect.top, dd.markX, dd.markY, mXGridPaint);
            }
        //value grid
        if ((mYGridPaint != null) && (valueDrawDataList != null))
            for (ValueDrawData dd : valueDrawDataList) {
                canvas.drawLine(dd.markX, dd.markY, chartRect.right, dd.markY, mYGridPaint);
            }

        //argument
        if (argumentDrawDataList != null)
            for (ArgumentDrawData dd : argumentDrawDataList) {
                //mark
                canvas.drawLine(dd.markX, dd.markY0, dd.markX, dd.markY, mAxesPaint);
                //label
                canvas.drawText(dd.labelText, dd.labelX, dd.labelY, mAxesTextPaint);
            }

        //series
        if (seriesDrawDataList != null)
            for (List<SeriesDrawData> seriesDrawData : seriesDrawDataList) {
                for (SeriesDrawData sdd : seriesDrawData) {
                    //bar
                    if (sdd != null) {
                        mBarPaint.setShader(sdd.barShader);
                        mBarPaint.setStyle(Paint.Style.FILL);
                        canvas.drawRect(sdd.barX0, sdd.barY0, sdd.barX, sdd.barY, mBarPaint);
                        mBarPaint.setStyle(Paint.Style.STROKE);
                        canvas.drawRect(sdd.barX0, sdd.barY0, sdd.barX, sdd.barY, mBarPaint);
                    }
                }
            }

        //argument axis
        canvas.drawLine(chartRect.left, chartRect.bottom, chartRect.right, chartRect.bottom,  mAxesPaint);

        //value
        if (valueDrawDataList != null) {
            for (ValueDrawData dd : valueDrawDataList) {
                //mark
                canvas.drawLine(dd.markX0, dd.markY, dd.markX, dd.markY, mAxesPaint);
                //label
                canvas.drawText(dd.labelText, dd.labelX, dd.labelY, mAxesTextPaint);
            }
        }
        //value axis
        canvas.drawLine(chartRect.left, chartRect.top, chartRect.left, chartRect.bottom, mAxesPaint);
        //value label
        if (valueLabelDrawable != null) {
            valueLabelDrawable.draw(canvas);
        }
    }

    public static class SavedState extends BaseSavedState {
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!mShowLabelOnClick)
            return false;
        int action = event.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPointerTracker = new PointerTracker();
                mPointerTracker.setMotionAction(action, (int) event.getX(), (int) event.getY());

                boolean needInvalidate = false;
                if ((fadeAnimator != null) && (fadeAnimator.isRunning())) {
                    fadeAnimator.cancel();
                    needInvalidate = true;
                }
                SeriesDrawData selectedItem = mChartDrawLayout.getSeriesDrawDataItemAtPos((int) event.getX(), (int) event.getY());
                if (selectedItem != null) {
                    valueLabelDrawable = new ValueLabelDrawable(selectedItem);
                    needInvalidate = true;
                }
                if (needInvalidate)
                    invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if (valueLabelDrawable != null) {
                    fadeAnimator = ValueAnimator.ofInt(valueLabelDrawable.mAlpha, 0);
                    fadeAnimator.setDuration(LABEL_WINDOW_VALUE_DURATION);
                    fadeAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            int value = (int) animation.getAnimatedValue();
                            valueLabelDrawable.setAlpha(value);
                            if (value == 0) {
                                animation.cancel();
                                valueLabelDrawable = null;
                            }
                            invalidate();
                        }
                    });
                    fadeAnimator.start();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mPointerTracker != null) {
                    mPointerTracker.setMotionAction(action, (int) event.getX(), (int) event.getY());
                    if (!mPointerTracker.isMoved())
                        break;
                }
            case MotionEvent.ACTION_CANCEL:
                if ((fadeAnimator != null) && (fadeAnimator.isRunning())) {
                    fadeAnimator.cancel();
                }
                if (valueLabelDrawable != null) {
                    valueLabelDrawable = null;
                    invalidate();
                }
                mPointerTracker = null;
                break;
            default:
        }
        return true;
    }
}