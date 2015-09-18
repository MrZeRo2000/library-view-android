package com.romanpulov.library.view;

import java.util.Locale;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class ProgressCircle extends View {

    //progress style definition
    public static final int PROGRESS_STYLE_PERCENT = 0;
    public static final int PROGRESS_STYLE_VALUE = 1;

    //defaults
    private static final int DEFAULT_ARC_MARGIN = 5;
    private static final int DEFAULT_ARC_THICKNESS = 5;
    public static final int DEFAULT_MIN = 0;
    public static final int DEFAULT_MAX = 100;
    public static final int DEFAULT_PROGRESS = 50;
    public static final boolean DEFAULT_AUTO_HIDE = false;
    public static final int DEFAULT_PROGRESS_COLOR = Color.RED;
    public static final int DEFAULT_PROGRESS_REST_COLOR = Color.WHITE;
    public static final int DEFAULT_TEXT_SIZE = 12;
    public static final int DEFAULT_TEXT_COLOR = Color.WHITE;
    public static final int DEFAULT_PROGRESS_STYLE = PROGRESS_STYLE_PERCENT;
    public static final int MAX_PERCENT_VALUE = 100;
    public static final String[] DISPLAY_FORMAT_LIST = {
            "%%0%dd%%%%",
            "%%0%dd",
    };
    public static final int PERCENT_VALUE_DIGITS = 2;

    // values / state
    private int mMin;
    private int mMax;
    private int mProgress;

    // appearance
    private boolean mAutoHide;
    private int mProgressColor;
    private int mProgressRestColor;
    private int mArcMargin;
    private int mProgressStyle;

    private String mDisplayProgressText;
    private String mMaxDisplayValueText;
    private String mMinDisplayValueText;
    private String mDisplayValueFormat;
    private int mMaxValueDigits;

    private int mPrevProgress;
    private Paint mTextPaint;
    private Paint mArcPaint;
    private Rect mTextBounds;
    private RectF mArcRect;

    private int mMostSize = 0;

    // constructor from code
    public ProgressCircle(Context context) {
        super(context, null);
    }

    // constructor from xml
    public ProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);

        //text paint
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setTextAlign(Align.LEFT);

        //arc paint
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);

        //arc rect
        mArcRect = new RectF();

        //read resources
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ProgressCircle);
        
        int min =  a.getInteger(R.styleable.ProgressCircle_min, DEFAULT_MIN);
        int max = a.getInteger(R.styleable.ProgressCircle_max, DEFAULT_MAX);
        int progress = a.getInteger(R.styleable.ProgressCircle_progress, DEFAULT_PROGRESS);

        // if input data is wrong revert to defaults
        if ((max < min) || (progress < min) || (progress > max)) {
            mMin = DEFAULT_MIN;
            mMax = DEFAULT_MAX;
            mProgress = DEFAULT_PROGRESS;
        } else {
            mMin = min;
            mMax = max;
            mProgress = progress;
        }
        mPrevProgress = mProgress;

        mAutoHide = a.getBoolean(R.styleable.ProgressCircle_autoHide, DEFAULT_AUTO_HIDE);

        //colors
        mTextPaint.setColor(a.getColor(R.styleable.ProgressCircle_textColor, DEFAULT_TEXT_COLOR));
        mProgressColor = a.getColor(R.styleable.ProgressCircle_progressColor, DEFAULT_PROGRESS_COLOR);
        mProgressRestColor = a.getColor(R.styleable.ProgressCircle_progressRestColor, DEFAULT_PROGRESS_REST_COLOR);

        //text size and style
        int textSize = a.getDimensionPixelOffset(R.styleable.ProgressCircle_textSize, (int) (DEFAULT_TEXT_SIZE * getResources().getDisplayMetrics().density));
        mTextPaint.setTextSize(textSize);
        int textStyle = a.getInt(R.styleable.ProgressCircle_textStyle, Typeface.NORMAL);
        Typeface tf = Typeface.create("", textStyle);
        mTextPaint.setTypeface(tf);

        //arc
        mArcMargin = a.getDimensionPixelOffset(R.styleable.ProgressCircle_arcMargin, (int) (DEFAULT_ARC_MARGIN * getResources().getDisplayMetrics().density));
        int arcThickness = a.getDimensionPixelOffset(R.styleable.ProgressCircle_arcThickness, (int) (DEFAULT_ARC_THICKNESS * getResources().getDisplayMetrics().density));
        mArcPaint.setStrokeWidth(arcThickness);

        mProgressStyle = a.getInt(R.styleable.ProgressCircle_progressStyle, DEFAULT_PROGRESS_STYLE);

        a.recycle();

        updateDisplayValues();
        updateTextBounds();
        mDisplayProgressText = getDisplayProgress();
    }

    private void updateDisplayValues() {
        int maxDisplayValue = 0;
        mMaxValueDigits = 1;

        switch (mProgressStyle) {
            case PROGRESS_STYLE_PERCENT:
                maxDisplayValue = mMax > MAX_PERCENT_VALUE ? MAX_PERCENT_VALUE : mMax;
                mMaxValueDigits = PERCENT_VALUE_DIGITS;
                break;
            case PROGRESS_STYLE_VALUE:
                maxDisplayValue = mMax;
                if (maxDisplayValue > 0 )
                    mMaxValueDigits = (int)Math.floor(Math.log10((double)maxDisplayValue)) + 1;
                break;
        }

        mMaxDisplayValueText =  String.valueOf(maxDisplayValue);
        mMinDisplayValueText = "";
        mDisplayValueFormat = String.format(Locale.getDefault(), DISPLAY_FORMAT_LIST[mProgressStyle], mMaxValueDigits);
    }

    private String getDisplayProgress() {
        if ((mProgress >= mMax) && (mProgressStyle == PROGRESS_STYLE_PERCENT) && (mMax == MAX_PERCENT_VALUE)) {
            // for 100% outputting unformatted
            return mMaxDisplayValueText;
        } else if (mMin == mProgress) {
            return mMinDisplayValueText;
        } else	{
            return String.format(Locale.getDefault(), mDisplayValueFormat, mProgress);
        }
    }

    public void setMin(int min) {
        if (min <= mMax) {
            mMin = min;
            if (mProgress < mMin) {
                setProgress(mMin);
            } else {
                invalidate();
            }
        }
    }

    public void setMax(int max) {
        if ((max >= mMin) && (max != mMax)) {
            mMax = max;
            updateDisplayValues();
            updateTextBounds();
            if (mProgress > mMax) {
                setProgress(mMax);
            } else {
                invalidate();
            }
        }
    }

    public void setProgress(int progress) {
        // change progress
        if (progress < mMin) {
            mProgress = mMin;
        } else if (progress > mMax) {
            mProgress = mMax;
        } else
            mProgress = progress;
        mDisplayProgressText = getDisplayProgress();

        //redraw control
        invalidate();

        //autoHide support
        if (mAutoHide) {
            if (((mPrevProgress == mMin) && (mProgress != mMin)) || ((mPrevProgress != mMin) && (mProgress == mMin))) {
                requestLayout();
            }
            mPrevProgress = mProgress;
        }
    }

    public void setTextSize(int size) {
        mTextPaint.setTextSize(size);
        //would be required if control size gets dependent from test font 
        //requestLayout();
        updateTextBounds();
        invalidate();
    }	
    
    public void setTextColor(int color) {
        mTextPaint.setColor(color);
        invalidate();
    }

    public void setProgressColor(int color) {
        mProgressColor = color;
        invalidate();
    }

    public void setProgressRestColor(int color) {
        mProgressRestColor = color;
        invalidate();
    }

    public void setArcMargin(int value) {
        mArcMargin = value;
        invalidate();
    }

    public void setArcThickness(int value) {
        mArcPaint.setStrokeWidth(value);
        invalidate();
    }

    public void setProgressStyle(int value) {
        switch (value) {
            case PROGRESS_STYLE_PERCENT:
                mProgressStyle = PROGRESS_STYLE_PERCENT;
                break;
            case PROGRESS_STYLE_VALUE:
                mProgressStyle = PROGRESS_STYLE_VALUE;
                break;
            default:
                return;
        }

        updateDisplayValues();
        updateTextBounds();
        invalidate();
    }

    private void updateTextBounds() {
        //calc text bounds
        int maxTextLength = mProgressStyle == PROGRESS_STYLE_PERCENT ? mMaxValueDigits + 1 : mMaxValueDigits;
        String boundsString = new String(new char[maxTextLength]).replace("\0", "0");
        mTextBounds = new Rect();
        mTextPaint.getTextBounds(boundsString, 0, maxTextLength, mTextBounds);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int width = getWidth();
        final int height = getHeight();

        canvas.drawText(mDisplayProgressText, (width - mTextBounds.width()) / 2, height - (height - mTextBounds.height()) / 2, mTextPaint);

        //draw border for testing purposes
        //canvas.drawRect(0, 0, width, height, mTextPaint);

        if (width > height) {
            mArcRect.set((width - height) / 2 + mArcMargin, mArcMargin, (width - height) / 2 + height - mArcMargin, height - mArcMargin);
        } else {
            mArcRect.set(mArcMargin, (height - width ) / 2 + mArcMargin, width - mArcMargin, (height - width) / 2 + width - mArcMargin);
        }

        if (mMax > mMin) {
            // calculate progress angle
            final float progressAngle = (mProgress - mMin) * 360 / (mMax - mMin);

            //draw progress
            mArcPaint.setColor(mProgressColor);
            canvas.drawArc(mArcRect, 90, progressAngle, false, mArcPaint);

            //draw rest
            if (mProgress > mMin) {
                mArcPaint.setColor(mProgressRestColor);
                canvas.drawArc(mArcRect, 90 + progressAngle, 360 - progressAngle, false, mArcPaint);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //vars used for calculation
        final int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();
        final int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        final int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();
        
        //this is the main metric to be calculated
        int size;

        /*
          as there is no internal restriction to the control size,
          trying to get this information from the container
        */
        if ((MeasureSpec.EXACTLY ==  heightSpecMode) || (MeasureSpec.EXACTLY == widthSpecMode)) {

            if ((0 == widthWithoutPadding) || (0 == heightWithoutPadding)) {
                size = Math.max(widthWithoutPadding, heightWithoutPadding);
            } else {
                size = Math.min(widthWithoutPadding, heightWithoutPadding);
            }

            if (size > 0 ) {
                mMostSize = size;
            }

        } else {
            //unable to get to know the size, return 0
            size = 0;
        }
        
        // return most size if it was calculated during previous EXACTLY measure request
        if ((widthSpecMode == MeasureSpec.AT_MOST) || (heightSpecMode == MeasureSpec.AT_MOST)) {
            size = mMostSize;
        }
        
        //autoHide support
        if (mAutoHide) {
            if (mProgress == mMin) {
                size = 0;
            }
        }
        
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }
}
