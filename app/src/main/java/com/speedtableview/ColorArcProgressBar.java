package com.speedtableview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by hua on 2016/3/22.
 */
public class ColorArcProgressBar extends View {
    private float startAngle = 30;
    private float sweepAngle = -240;
    private float currentAngle = -240;

    private int[] colors = new int[]{Color.GREEN, Color.YELLOW, Color.RED, Color.RED};
    private int bgArcColor = Color.rgb(17, 17, 17);

    private float bgArcWidth = dipToPx(3);
    private float progressWidth = dipToPx(3);

    private int diameter = dipToPx(234);
    private float centerX;
    private float centerY;

    private RectF bgRect;
    private Paint allArcPaint;
    private Paint progressPaint;

    private double mToatleMileage = 100;
    private double mSurplusMileage = 100;

    private double k;

    private ValueAnimator progressAnimator;

    private PaintFlagsDrawFilter mDrawFilter;
    private SweepGradient sweepGradient;
    private Matrix rotateMatrix;

    public ColorArcProgressBar(Context context) {
        super(context);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initCofig(context, attrs);
        initView();
    }

    public ColorArcProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCofig(context, attrs);
        initView();
    }

    /**
     * 初始化布局配置
     */
    private void initCofig(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorArcProgressBar);
        bgArcColor = a.getColor(R.styleable.ColorArcProgressBar_capb_back_color, Color.rgb(17, 17, 17));
        int color1 = a.getColor(R.styleable.ColorArcProgressBar_capb_front_color1, Color.GREEN);
        int color2 = a.getColor(R.styleable.ColorArcProgressBar_capb_front_color2, Color.YELLOW);
        int color3 = a.getColor(R.styleable.ColorArcProgressBar_capb_front_color3, Color.RED);
        colors = new int[]{color1, color2, color3, color3};

        a.recycle();
    }

    private void initView() {
        bgRect = new RectF();
        bgRect.top = progressWidth / 2;
        bgRect.left = progressWidth / 2;
        bgRect.right = diameter + progressWidth / 2;
        bgRect.bottom = diameter + progressWidth / 2;

        centerX = (progressWidth + diameter) / 2;
        centerY = (progressWidth + diameter) / 2;

        allArcPaint = new Paint();
        allArcPaint.setAntiAlias(true);
        allArcPaint.setStyle(Paint.Style.STROKE);
        allArcPaint.setStrokeWidth(bgArcWidth);
        allArcPaint.setColor(bgArcColor);
        allArcPaint.setStrokeCap(Paint.Cap.ROUND);

        progressPaint = new Paint();
        progressPaint.setAntiAlias(true);
        progressPaint.setStyle(Paint.Style.STROKE);
        progressPaint.setStrokeCap(Paint.Cap.ROUND);
        progressPaint.setStrokeWidth(progressWidth);
        progressPaint.setColor(Color.GREEN);

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        sweepGradient = new SweepGradient(centerX, centerY, colors, null);
        rotateMatrix = new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = (int) (progressWidth + diameter);
        int height = (int) (progressWidth + diameter);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(mDrawFilter);

        canvas.drawArc(bgRect, startAngle, sweepAngle, false, allArcPaint);

        rotateMatrix.setRotate(149, centerX, centerY);
        sweepGradient.setLocalMatrix(rotateMatrix);
        progressPaint.setShader(sweepGradient);

        canvas.drawArc(bgRect, startAngle, currentAngle, false, progressPaint);

        invalidate();
    }

    /**
     * 设置总里程
     */
    public void setToatleMileage(long toatleMileage) {
        if (toatleMileage != 0) {
            mToatleMileage = toatleMileage;
            k = sweepAngle / mToatleMileage;
        } else {
            mToatleMileage = toatleMileage;
        }
    }

    /**
     * 设置剩余里程
     */
    public void setSurplusMileage(long surplusMileage) {
        if (mToatleMileage == 0) {
            currentAngle = -240;
        } else {
            if (surplusMileage >= mToatleMileage) {
                mSurplusMileage = mToatleMileage;
            } else if (surplusMileage <= 0) {
                mSurplusMileage = 0;
            } else {
                mSurplusMileage = surplusMileage;
            }
            currentAngle = (float) (mSurplusMileage * sweepAngle / mToatleMileage);
        }
        invalidate();
    }

    /**
     * 加满油箱
     */
    public void fillGas() {
        if (mToatleMileage == 0) {
            showOFFStatus();
        } else {
            progressAnimator = ValueAnimator.ofFloat(0, -240);
            progressAnimator.setDuration(1000);
            progressAnimator.setTarget(currentAngle);
            progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    currentAngle = (float) animation.getAnimatedValue();
                    mSurplusMileage = currentAngle / k;
                }

            });
            progressAnimator.start();
        }
    }

    /**
     * 显示未启用状态
     */
    public void showOFFStatus() {
        currentAngle = -240;
        invalidate();
    }

    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
    }

}
