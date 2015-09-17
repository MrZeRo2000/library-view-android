package com.romanpulov.library.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

public class SlideNumberPicker extends View implements GestureDetector.OnGestureListener {

	//main scroll animation duration
	private static final int SCROLL_ANIMATION_DURATION = 3000;
	//adjust scroll duration
	private final static int ADJUST_SCROLL_DURATION = 500;	
	
	private static final int DEFAULT_TEXTCOLOR = Color.BLUE;
	private static final int DEFAULT_FRAMECOLOR = Color.BLACK;
	private static final int DEFAULT_TEXTSIZE = 16;
	
	private static final int DEFAULT_MAX = 99;
    private static final int DEFAULT_MIN = 0;	
	
	private String mNumberFormat;
	private int mMin;
	private int mMax;
	private int mValue;
	
	//frame appearance
	private int mFrameColor;
	
	//text appearance
	private int mTextColor;
	
	// calculated based on mMax and mMin
	private int mRange;
	
	private int mCurrentScrollOffset = 0;
	
	// value
	private int mCurrentValue;
	private int mNextValue;
	private int mNewCalcValue;
	
	private int mCurrentScrollY = 0;
	private int mCurrentAnimateScrollY = 0;
	private int mItemHeight = 0;
	
	private SparseArray<String> mDisplayValues = new SparseArray<>();
	
	private Paint mPaint;
	
	private GestureDetector mDetector; 
	private Scroller mScroller; 
	private Scroller mAdjustScroller;
	private ValueAnimator mScrollAnimator;
	
	private Scroller mCurrentScroller;
	
	private OnValueChangeListener mOnValueChangeListener;
	
    public interface OnValueChangeListener {
        void onValueChange(SlideNumberPicker picker, int oldVal, int newVal);
    }
	
	private void initDisplayValues() {
		mDisplayValues.clear();
		
		for (int i = mMin; i <= mMax; i ++) {
			mDisplayValues.put(i, String.format(mNumberFormat, i));
		}
	}
	
	private void updateRange() {
		mRange = mMax - mMin + 1;
	}

	@SuppressWarnings("unused") // it's actually used
    public void setOnValueChangedListener(OnValueChangeListener onValueChangedListener) {
        mOnValueChangeListener = onValueChangedListener;
    }	
    
    private void notifyChange(int previous, int current) {
        if (mOnValueChangeListener != null) {
            mOnValueChangeListener.onValueChange(this, previous, current);
        }
    }
    
	public void setValue(int value) {
		if (value != mValue) {
			notifyChange(mValue, value);
			mValue = value;
			invalidate();
		}
	}
	
	public void setMax(int value) {
		mMax = value;
		updateRange();		
	}
	
	public void setMin(int value) {
		mMin = value;
		updateRange();
	}
	
	public int getValue() {
		return mValue;
	}
	
	public SlideNumberPicker(Context context) {
		super(context, null);
	}

	public SlideNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);

		// prepare paint
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.STROKE);

		TypedArray attributesArray = context.obtainStyledAttributes(attrs,
                R.styleable.SlideNumberPicker);
        
        mMin = attributesArray.getInt(R.styleable.SlideNumberPicker_min, DEFAULT_MIN);
        mMax = attributesArray.getInt(R.styleable.SlideNumberPicker_max, DEFAULT_MAX);    
        mTextColor = attributesArray.getColor(R.styleable.SlideNumberPicker_textColor, DEFAULT_TEXTCOLOR);
        mFrameColor = attributesArray.getColor(R.styleable.SlideNumberPicker_frameColor, DEFAULT_FRAMECOLOR);
        int textSize = attributesArray.getDimensionPixelSize(R.styleable.SlideNumberPicker_textSize, DEFAULT_TEXTSIZE);

        int textStyle = attributesArray.getInt(R.styleable.SlideNumberPicker_textStyle, Typeface.NORMAL);
        Typeface tf = Typeface.create("", textStyle);        
        mPaint.setTypeface(tf);
        
        attributesArray.recycle();
        
        mNumberFormat = "%02d";
        
        updateRange();
        
        initDisplayValues();
        
        setValue(mMin);
        mCurrentValue = mNextValue = mValue;
        mPaint.setTextSize(textSize);
        
        mDetector = new GestureDetector(getContext(), this);
        mScroller = new Scroller(getContext(), null, true);
        mAdjustScroller = new Scroller(getContext(), null, true);
        
        mScrollAnimator = ValueAnimator.ofFloat(0,1);        
        mScrollAnimator.setDuration(SCROLL_ANIMATION_DURATION);        
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (!mCurrentScroller.isFinished()) {
                	mCurrentScroller.computeScrollOffset();

                    if ((0 == mCurrentAnimateScrollY) || (Integer.MAX_VALUE == mCurrentAnimateScrollY)) {
                    	mCurrentAnimateScrollY = mCurrentScroller.getStartY();
                    }
                    
                    scrollBy(0, mCurrentScroller.getCurrY() - mCurrentAnimateScrollY);
                    mCurrentAnimateScrollY = mCurrentScroller.getCurrY();

                    invalidate();
                } else {
                    mScrollAnimator.cancel();
                    mCurrentAnimateScrollY = 0;
                    
                    if (mScroller == mCurrentScroller) {
                    	// scroll to nearest value
                    	finishScroll();
                    } else {
                    	//scroll completed
                    	mCurrentScrollOffset = 0;
                    	mCurrentValue = mNextValue = mNewCalcValue;                    	
                    	setValue(mNewCalcValue);
                    }
                }
            }
        });	
	}
	
    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
    	super.onSizeChanged(width, height, oldWidth, oldHeight);
    	mItemHeight = height;
    }
    
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {		
		setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
	}
	
	private int measureWidth(int measureSpec) {
		int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        
        if (specMode == MeasureSpec.EXACTLY) {
        	return specSize;
        } else {
        	result = (int) mPaint.measureText(String.format(mNumberFormat, mMax));
        	if (specMode == MeasureSpec.AT_MOST) {
        		result = Math.min(result, specSize);
        	}
        }
        
        return result;
	}
	
	private int measureHeight(int measureSpec) {
		int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        
        if (specMode == MeasureSpec.EXACTLY) {
        	return specSize;
        } else {
        	result = (int) (-mPaint.ascent() + mPaint.descent());
        	if (specMode == MeasureSpec.AT_MOST) {
        		result = Math.min(result, specSize);
        	}
        }
        
        return result;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		//draw background
		super.onDraw(canvas);
		
		// control frame
		mPaint.setColor(mFrameColor);
		canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);

		if (mItemHeight > 0) {
			// draw current value
			int itemOffset = (mCurrentScrollOffset > 0) ? mCurrentScrollOffset % mItemHeight - mItemHeight : mCurrentScrollOffset % mItemHeight;
			canvas.drawRect(0, itemOffset, getWidth(), getHeight() + itemOffset, mPaint);
			mPaint.setColor(mTextColor);
			canvas.drawText(mDisplayValues.get(mCurrentValue), getWidth() / 2, getHeight() / 2 + itemOffset, mPaint);

			// draw next value
			itemOffset += mItemHeight;
			mPaint.setColor(mFrameColor);
			canvas.drawRect(0, itemOffset, getWidth(), getHeight() + itemOffset, mPaint);
			mPaint.setColor(mTextColor);
			canvas.drawText(mDisplayValues.get(mNextValue), getWidth() / 2, getHeight() / 2 + itemOffset, mPaint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (this.mDetector.onTouchEvent(event))
			return true;
		
		int action = event.getActionMasked();
		 switch (action) {
		 	case MotionEvent.ACTION_MOVE:
		 		scrollBy(0, (int) event.getY() - mCurrentScrollY);
                invalidate();
                mCurrentScrollY = (int) event.getY();
		 		break;
		 	case MotionEvent.ACTION_UP:
		 		finishScroll();
		 		break;
		 	case MotionEvent.ACTION_CANCEL:	
		 		break;
		 }
		
		return true;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		
		mScroller.forceFinished(true);
		mAdjustScroller.forceFinished(true);
		mCurrentScrollY = (int)arg0.getY();
		// need to process onDown further
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		fling((int) velocityY);
        return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// abstract method implementation
	}
	
	@Override
	public void scrollBy(int x, int y) {
		mCurrentScrollOffset += y;

		if (mRange > 0 ) {
			int currentValueOffset = (-mCurrentScrollOffset / mItemHeight) % mRange;

			// added mRange to ensure values are > 0
			if (mCurrentScrollOffset <= 0) {
				mCurrentValue = mMin + (mValue - mMin + currentValueOffset + mRange) % mRange;
				mNextValue = mMin + (mValue - mMin + 1 + currentValueOffset + mRange) % mRange;
			} else {
				mCurrentValue = mMin + (mValue - mMin - 1 + currentValueOffset + mRange) % mRange;
				mNextValue = mMin + (mValue - mMin + currentValueOffset + mRange) % mRange;
			}
		}
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}
	
	private void fling(int velocityY) {
        if (velocityY > 0) {
            mScroller.fling(0, 0, 0, velocityY, 0, 0, 0, Integer.MAX_VALUE);
        } else {
            mScroller.fling(0, Integer.MAX_VALUE, 0, velocityY, 0, 0, 0, Integer.MAX_VALUE);
        }
		
		mCurrentScroller = mScroller;
        mScrollAnimator.start();
        postInvalidate();        
	}
	
	private void finishScroll() {
		
		int currentScrollValue = (mCurrentScrollOffset > 0)? mCurrentScrollOffset % mItemHeight : mItemHeight + mCurrentScrollOffset % mItemHeight;
		mNewCalcValue = (currentScrollValue < (mItemHeight / 2)) ? mNextValue : mCurrentValue;
		int newScrollOffset = (currentScrollValue < (mItemHeight / 2)) ? -currentScrollValue : mItemHeight - currentScrollValue;

		// scroll to the nearest value
		mAdjustScroller.startScroll(0, 0, 0, newScrollOffset, ADJUST_SCROLL_DURATION);		
		mCurrentScroller = mAdjustScroller;
		mScrollAnimator.start();
	}
	
    static class SavedState extends BaseSavedState {
        int value;
        int currentValue;
        int nextValue;
        
        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            value = in.readInt();
            currentValue = in.readInt();
            nextValue = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(value);
            out.writeInt(currentValue);
            out.writeInt(nextValue);
        }

        @Override
        public String toString() {
            return "SlideNumberPicker.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " value=" + value  
                    + " currentValue=" + currentValue 
                    + " nextValue=" + nextValue + "}";
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
        
        ss.value = mValue;
        ss.currentValue = mCurrentValue;
        ss.nextValue = mNextValue;
        
        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;

        super.onRestoreInstanceState(ss.getSuperState());
        
        mValue = ss.value;
        mCurrentValue = ss.currentValue;
        mNextValue = ss.nextValue;
        
        requestLayout();
    }

	@Override
	public void onShowPress(MotionEvent e) {
		// abstract method implementation		
	}
}
