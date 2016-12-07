package ru.ntnsmirnov.speedometer;


import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class Speedometer extends View {

    public static final int SEGMENT_LINE_OFFSET = 50;
    public static final int SEGMENT_NUMBER_OFFSET = SEGMENT_LINE_OFFSET + 25;

    private int mExampleColor = Color.RED;
    private float mExampleDimension = 40;
    private float strokeWidth = 6f;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private Paint paint;

    double angle = 0.75 * PI;
    String speed = "0";


    int paddingLeft = 0;
    int paddingTop = 0;
    int paddingRight = 0;
    int paddingBottom = 0;

    int width = 0;
    int height = 0;

    int contentWidth = 0;
    int contentHeight = 0;

    float centerX = 0;
    float centerY = 0;
    float radius = 0;

    Path arrow;
    Bitmap scale;

    ValueAnimator animator;

    public Speedometer(Context context) {
        super(context);
        init(null, 0);
    }

    public Speedometer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public Speedometer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.Speedometer, defStyle, 0);

        mExampleColor = a.getColor(
                R.styleable.Speedometer_exampleColor,
                mExampleColor);
        mExampleDimension = a.getDimension(
                R.styleable.Speedometer_exampleDimension,
                mExampleDimension);

        a.recycle();

        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(Paint.Align.LEFT);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(mExampleColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);

        arrow = new Path();
    }

    private void invalidateScale() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);

        if (width > 0 && height > 0) {

            scale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(scale);

            canvas.drawCircle(centerX,
                    centerY, radius, paint);

            for (int num = 0; num <= 270; num += 45) {
                double radNum = toRadians(num + 45) + PI / 2;
                String numString = String.valueOf(num);

                float cosR = (float) cos(radNum);
                float sinR = (float) sin(radNum);

                float xOnCircle = centerX + radius * cosR;
                float yOnCircle = centerY + radius * sinR;

                canvas.drawLine(
                        xOnCircle,
                        yOnCircle,
                        xOnCircle - SEGMENT_LINE_OFFSET * cosR,
                        yOnCircle - SEGMENT_LINE_OFFSET * sinR,
                        paint);

                float numWidth = mTextPaint.measureText(numString);
                Paint.FontMetrics numFontMetrics = mTextPaint.getFontMetrics();
                float numHeight = mTextHeight = numFontMetrics.bottom;

                canvas.drawText(
                        numString,
                        xOnCircle - SEGMENT_NUMBER_OFFSET * cosR - numWidth / 2 - (numWidth / 2) * cosR,
                        yOnCircle - SEGMENT_NUMBER_OFFSET * sinR + numHeight / 2 - (numHeight / 2) * sinR,
                        mTextPaint);
            }
        }


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (width > 0 && height > 0) {
            canvas.drawBitmap(scale, 0, 0, paint);

            mTextWidth = mTextPaint.measureText(String.valueOf(speed));
            Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
            mTextHeight = fontMetrics.bottom;

            canvas.drawText(speed,
                    centerX  - mTextWidth / 2,
                    centerY + 0.75f * radius - mTextHeight,
                    mTextPaint);

            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            arrow.reset();
            arrow.moveTo((float) (centerX + radius * cos(angle)),
                    (float) (centerY + radius * sin(angle)));
            arrow.lineTo((float) (centerX - SEGMENT_LINE_OFFSET * cos(angle - (0.10 * PI))),
                    (float) (centerY - SEGMENT_LINE_OFFSET * sin(angle - (0.10 * PI))));
            arrow.lineTo((float) (centerX - SEGMENT_LINE_OFFSET * cos(angle + (0.10 * PI))),
                    (float) (centerY - SEGMENT_LINE_OFFSET * sin(angle + (0.10 * PI))));
            arrow.lineTo((float) (centerX + radius * cos(angle)),
                    (float) (centerY + radius * sin(angle)));
            arrow.close();
            canvas.drawPath(arrow, paint);
            paint.setStyle(Paint.Style.STROKE);
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("sdf", "onSizeChanged: ");

        this.paddingLeft = getPaddingLeft();
        this.paddingTop = getPaddingTop();
        this.paddingRight = getPaddingRight();
        this.paddingBottom = getPaddingBottom();
        this.width = getWidth();
        this.height = getHeight();

        contentWidth = width - paddingLeft - paddingRight - (int) (strokeWidth * 2);
        contentHeight = height - paddingTop - paddingBottom - (int) (strokeWidth * 2);

        centerX = paddingLeft + contentWidth / 2 + strokeWidth;
        centerY = paddingTop + contentHeight / 2 + strokeWidth;
        radius = Math.min(contentHeight, contentWidth) / 2;

        invalidateScale();


    }

    public void accelerate() {
        ValueAnimator animator = ValueAnimator.ofInt(0, 90, 70, 180, 160, 270, 0);
        animator.setDuration(5000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) (animation.getAnimatedValue());
                speed = String.valueOf(value);
                angle = toRadians(value + 45) + PI / 2;
                invalidate();
            }
        });
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.start();
    }

    public void setSpeed(int newSpeed){
        if (newSpeed < 0){
            newSpeed *= -1;
        }
        if (newSpeed > 270) {
            newSpeed = 270;
        }
        if (animator != null && animator.isRunning()){
            animator.cancel();
        }
        animator = ValueAnimator.ofInt(Integer.parseInt(speed), newSpeed);
        animator.setDuration(900);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) (animation.getAnimatedValue());
                speed = String.valueOf(value);
                angle = toRadians(value + 45) + PI / 2;
                invalidate();
            }
        });
        animator.start();
    }

    public int getExampleColor() {
        return mExampleColor;
    }

    public void setExampleColor(int exampleColor) {
        mExampleColor = exampleColor;
        invalidateScale();
    }

    public float getExampleDimension() {
        return mExampleDimension;
    }

    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateScale();
    }
}