package com.romanpulov.wheelcontrol;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
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
	//fling gesture velocity correction factor
	private final static int FLING_VELOCITY_SCALE_FACTOR = 4;
	
	private static final int DEFAULT_TEXTCOLOR = Color.BLUE;
	
	private static final int DEFAULT_MAX = 99;
    private static final int DEFAULT_MIN = 0;	
	
	private String mNumberFormat;
	private int mMin;
	private int mMax;
	private int mValue;
	
	//text appearance
	private int mTextSize;
	private int mTextColor; 
	
	// calculated based on mMax and mMin
	private int mRange;
	
	private int mCurrentScrollOffset = 0;
	
	private int mCurrentValue;
	private int mNextValue;
	private int mNewCalcValue;
	
	private int mCurrentScrollY = 0;
	private int mCurrentAnimateScrollY = 0;
	private int mItemHeight = 0;
	
	private SparseArray<String> mDisplayValues = new SparseArray<String>();  
	
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
	
    public void setOnValueChangedListener(OnValueChangeListener onValueChangedListener) {
        mOnValueChangeListener = onValueChangedListener;
    }	
    
    private void notifyChange(int previous, int current) {
        if (mOnValueChangeListener != null) {
            mOnValueChangeListener.onValueChange(this, previous, mValue);
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
		//initNumberPicker();
		// TODO Auto-generated constructor stub
	}
	

	public SlideNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(16);
        //mPaint.setColor(0xFF000000);
        mPaint.setStyle(Style.STROKE);

		TypedArray attributesArray = context.obtainStyledAttributes(attrs,
                R.styleable.SlideNumberPicker);
        
        mMin = attributesArray.getInt(R.styleable.SlideNumberPicker_min, DEFAULT_MIN);
        mMax = attributesArray.getInt(R.styleable.SlideNumberPicker_max, DEFAULT_MAX);    
        mTextColor = attributesArray.getColor(R.styleable.SlideNumberPicker_textColor, DEFAULT_TEXTCOLOR);
        
        attributesArray.recycle();
        
        mNumberFormat = "%02d";
        
        updateRange();
        
        initDisplayValues();
        
        setValue(mMin);
        mCurrentValue = mNextValue = mValue;
        
        mDetector = new GestureDetector(getContext(), this);
        mScroller = new Scroller(getContext(), null, true);
        mAdjustScroller = new Scroller(getContext(), null, true);
        
        mScrollAnimator = ValueAnimator.ofFloat(0,1);        
        mScrollAnimator.setDuration(SCROLL_ANIMATION_DURATION);        
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
            	Log.d("onFling", "onAnimationUpdate");
            	
                if (!mCurrentScroller.isFinished()) {
                	mCurrentScroller.computeScrollOffset();

                	Log.d("onFling", "getStartY = " + mCurrentScroller.getStartY() + " getCurrY = " + mCurrentScroller.getCurrY() + ", getFinalY = " + mCurrentScroller.getFinalY());
                    Log.d("onFling", "mCurrentAnimateScrollY = " + mCurrentAnimateScrollY);
                    
                    if ((0 == mCurrentAnimateScrollY) || (Integer.MAX_VALUE == mCurrentAnimateScrollY)) {
                    	mCurrentAnimateScrollY = mCurrentScroller.getStartY();
                    }
                    
                    Log.d("onFling", "scrolling by " + (mCurrentScroller.getCurrY() - mCurrentAnimateScrollY));                    
                    scrollBy(0, mCurrentScroller.getCurrY() - mCurrentAnimateScrollY);
                    
                    mCurrentAnimateScrollY = mCurrentScroller.getCurrY();                    
                    invalidate();
                    
                } else {
                    mScrollAnimator.cancel();
                    
                    mCurrentAnimateScrollY = 0;
                    
                    Log.d("onFling", "cancel");
                    if (mScroller == mCurrentScroller) {
                    	
                    	// scroll to nearest value
                    	Log.d("onFling", "mScroller cancel");
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	// TODO Auto-generated method stub
    	super.onSizeChanged(w, h, oldw, oldh);
    	mItemHeight = h;
    }
    
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {		
		setMeasuredDimension(measureWidth(widthMeasureSpec),
                measureHeight(heightMeasureSpec));
	}
	
	private int measureWidth(int measureSpec) {
		int result = 0;
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
		int result = 0;
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
		
		Log.d("onDraw", "mCurrentValue=" + mCurrentValue + ", mNextValue = " + mNextValue);
		
		// control frame for testing purposes
		canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
		
		int itemOffset = (mCurrentScrollOffset > 0) ? mCurrentScrollOffset % mItemHeight - mItemHeight : mCurrentScrollOffset % mItemHeight;
		
		canvas.drawRect(0, itemOffset, getWidth(), getHeight() + itemOffset, mPaint);
		mPaint.setColor(mTextColor);
		canvas.drawText(mDisplayValues.get(mCurrentValue), getWidth() / 2, getHeight() / 2 + itemOffset, mPaint);
		
		itemOffset += mItemHeight;
		canvas.drawRect(0, itemOffset, getWidth(), getHeight() + itemOffset, mPaint);
		canvas.drawText(mDisplayValues.get(mNextValue), getWidth() / 2, getHeight() / 2 + itemOffset, mPaint);
		
		
/*		
		for (int i = -1; i < 2; i ++) {
			
			int itemOffset = mCurrentScrollOffset % itemHeight + i * itemHeight;
			int valueOffset = 0;
			
			canvas.drawRect(0, itemOffset, getWidth(), getHeight() + itemOffset, mPaint);
			canvas.drawText(mDisplayValues.get(valueOffset), getWidth() / 2, getHeight() / 2 + itemOffset, mPaint);
			
			Log.d("onDraw", "mTextValue=" + mTextValue + ", itemOffset = " + itemOffset);
		}
	*/	
		
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		if (this.mDetector.onTouchEvent(event))
			return true;
		
		int action = event.getActionMasked();
		 switch (action) {
		 	case MotionEvent.ACTION_MOVE:
		 		Log.d("onTouchEvent", "Move");
		 		scrollBy(0, (int) event.getY() - mCurrentScrollY);
                invalidate();
                mCurrentScrollY = (int) event.getY();
		 		break;
		 	case MotionEvent.ACTION_UP:
		 		Log.d("onTouchEvent", "Up");
		 		finishScroll();
		 		break;
		 	case MotionEvent.ACTION_CANCEL:	
		 		Log.d("onTouchEvent", "Cancel");
		 		break;		 		
		 }
		
		return true;
		//return super.onTouchEvent(event);
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
		
		fling((int) velocityX, (int) velocityY);
		
		Log.d("onFling", "VelocityX = " + velocityX + ", VelocityY = " + velocityY);
        return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void scrollBy(int x, int y) {
		
		mCurrentScrollOffset += y;
		int currentValueOffset = (-mCurrentScrollOffset / mItemHeight) % mRange;
	
		// added mRange to ensure values are > 0
		if (mCurrentScrollOffset <= 0) {
			mCurrentValue = mMin + (mValue - mMin + currentValueOffset + mRange) % mRange;
			mNextValue = mMin + (mValue - mMin + 1 + currentValueOffset + mRange) % mRange;
		} else {
			mCurrentValue = mMin + (mValue - mMin - 1 + currentValueOffset + mRange) % mRange;
			mNextValue = mMin + (mValue - mMin + currentValueOffset + mRange) % mRange;
		}
		
		Log.d("scroll", "mCurrentScrollOffset = " + mCurrentScrollOffset +  ", currentValueOffset =" + currentValueOffset + ", mCurrentValue = " + mCurrentValue + ", mNextValue = " + mNextValue);
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float distanceX, float distanceY) {
		//Log.d("onScroll", "onScroll");
		
		//scrollBy(0, (int) distanceY);
		//mCurrentScrollOffset += distanceY;
		
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		return false;
	}
	
	private void fling(int velocityX, int velocityY) {		
		
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
		Log.d("scrollFinish", "NewCalcValue = " + mNewCalcValue + ", mNewScrollOffset = " + newScrollOffset + ", currentScrollValue = " + currentScrollValue);
		
		// scroll to the nearest value
		mAdjustScroller.startScroll(0, 0, 0, newScrollOffset, ADJUST_SCROLL_DURATION);		
		mCurrentScroller = mAdjustScroller;
		mScrollAnimator.start();
	}
}
