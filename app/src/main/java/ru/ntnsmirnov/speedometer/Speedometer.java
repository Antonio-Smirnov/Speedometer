package ru.ntnsmirnov.speedometer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;

import static android.R.attr.centerX;
import static android.R.attr.centerY;
import static java.lang.Math.PI;
import static java.lang.Math.abs;
import static java.lang.Math.cos;
import static java.lang.Math.floor;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

public class Speedometer extends View {
    private int mExampleColor = Color.RED;
    private float mExampleDimension = 40;
    private float strokeWidth = 6f;

    private TextPaint mTextPaint;
    private float mTextWidth;
    private float mTextHeight;

    private Paint paint;

    double angle = PI / 2;
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

        invalidateTextPaintAndMeasurements();
    }

    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mExampleDimension);
        mTextPaint.setColor(mExampleColor);
        mTextWidth = mTextPaint.measureText(String.valueOf(speed));

        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        mTextHeight = fontMetrics.bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        this.paddingLeft = getPaddingLeft();
        this.paddingTop = getPaddingTop();
        this.paddingRight = getPaddingRight();
        this.paddingBottom = getPaddingBottom();
        this.width = getWidth();
        this.height = getHeight();

            int contentWidth = width - paddingLeft - paddingRight - (int)(strokeWidth * 2);
            int contentHeight = height - paddingTop - paddingBottom - (int)(strokeWidth * 2);

            float centerX = paddingLeft + contentWidth / 2 + strokeWidth;
            float centerY = paddingTop + contentHeight / 2 + strokeWidth;
            float radius = Math.min(contentHeight, contentWidth) / 2;


            canvas.drawCircle(centerX,
                    centerY, radius, paint);

            for (int num = 0; num<360; num+=45) {
                double radNum = toRadians(num) + PI / 2;
                String numString = String.valueOf(num);
                float numWidth = mTextPaint.measureText(numString);
                float numHeight = mTextPaint.measureText(numString);
                canvas.drawText(numString,
                        centerX + (float) ((radius - numWidth/2) * cos(radNum) - (numWidth
                                / 2)),
                        centerY + (float) ((radius - (numHeight)) * sin(radNum)),// + (numHeight*abs(cos(radNum))/2)),
//                        centerY + (float) ((radius + mTextHeight/2) * sin(radNum)) + (mTextHeight/2),
                        mTextPaint);
            }

        // Draw the text.
        canvas.drawText(speed,
                paddingLeft + (contentWidth - mTextWidth) / 2,
                paddingTop + (contentHeight + mTextHeight) / 2 - mTextHeight,
                mTextPaint);

        canvas.drawLine(centerX, centerY, (float)(centerX+radius*cos(angle)), (float)(centerY+radius*sin(angle)), paint);
        paint.setColor(Color.RED);
        canvas.drawLine(centerX, centerY+radius, centerX, centerY - radius, paint);
        canvas.drawLine(centerX - radius, centerY, centerX + radius, centerY, paint);
        paint.setColor(mExampleColor);
    }

    public void accelerate(){
        ValueAnimator animator = ValueAnimator.ofInt(0, 90, 70, 180, 160, 270, 250, 360, 0);
        animator.setDuration(5000);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) (animation.getAnimatedValue());
                speed = String.valueOf(value);
                angle = toRadians(value)+ PI / 2;
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
        invalidateTextPaintAndMeasurements();
    }

    public float getExampleDimension() {
        return mExampleDimension;
    }
    public void setExampleDimension(float exampleDimension) {
        mExampleDimension = exampleDimension;
        invalidateTextPaintAndMeasurements();
    }
}
