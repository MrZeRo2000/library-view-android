package com.romanpulov.wheelcontrol;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector;
import android.widget.OverScroller;
import android.widget.Scroller;
import android.widget.Toast;
import android.widget.NumberPicker;

public class SlideNumberPicker extends View implements GestureDetector.OnGestureListener {
	
	private final static int SCALE = 4;
	
	private String mNumberFormat;
	private int mMin;
	private int mMax;
	private int mValue;
	private String mTextValue;
	
	private Paint mPaint;
	
	private GestureDetector mDetector; 
	private Scroller mScroller; 
	private ValueAnimator mScrollAnimator;
	
	private void setValue(int value) {
		mValue = value;
		mTextValue = String.format(mNumberFormat, mValue); 
	}
	
	public int getValue() {
		return mValue;
	}
	
	public SlideNumberPicker(Context context) {
		super(context);
		initNumberPicker();
		// TODO Auto-generated constructor stub
	}
	

	public SlideNumberPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		initNumberPicker();		
		
		TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.SlideNumberPicker);
		
		
	}
	
    private final void initNumberPicker() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(16);
        mPaint.setColor(0xFF000000);
        mPaint.setStyle(Style.STROKE);
        
        mMin = 0;
        mMax = 99;        
        mNumberFormat = "%02d";
        
        setValue(mMin);
        
        mDetector = new GestureDetector(getContext(), this);
        mScroller = new Scroller(getContext(), null, true);
        
        mScrollAnimator = ValueAnimator.ofFloat(0,1);
        
        mScrollAnimator.setDuration(3000);
        
        mScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
            	Log.d("onFling", "onAnimationUpdate");
            	
                if (!mScroller.isFinished()) {
                    mScroller.computeScrollOffset();
                    Log.d("onFling", "getCurrY = " + mScroller.getCurrY());
                    //setPieRotation(mScroller.getCurrY());
                    
                    scrollBy(0, mScroller.getFinalY() - mScroller.getCurrY());
                    
                } else {
                    mScrollAnimator.cancel();
                    Log.d("onFling", "cancel");
                    //onScrollFinished();        
                    
                    
                }
            }
        });
        

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
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawRect(0, 0, getWidth(), getHeight(), mPaint);
		canvas.drawText(mTextValue, 25, 25, mPaint);
		Log.d("onDraw", "ScrollY=" + getScrollY());
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		
		return this.mDetector.onTouchEvent(event);
		
		//return super.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		
		mScroller.forceFinished(true);
		
		// need to process onDown further
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		// TODO Auto-generated method stub
		fling((int) -velocityX, (int) -velocityY);
		Log.d("onFling", "VelocityX = " + velocityX + ", VelocityY = " + velocityY);
        return true;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float distanceX, float distanceY) {
		Log.d("onScroll", "onScroll");
		
		scrollBy(0, (int) distanceY);
		
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
		
		
		int currentX = getWidth() / 2;
		int currentY = getHeight() / 2;
		
		
		mScroller.fling(currentX, currentY, velocityX / SCALE, velocityY / SCALE, 0, getWidth(), 0, getHeight());	
		
        
        mScrollAnimator.start();
        
        postInvalidate();        
		
	}
	
}
