package com.speedtableview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by wujun on 2017/8/9.
 * 码表
 * 最高速度300
 *
 * @author madreain
 * @desc
 */

public class SpeedTableView extends View {

    private Paint mPaint;
    private Paint mRemainderPaint;
    private Paint mtxtPaint;
    private Paint tmpPaint;
    private float dip = 13;
    private float mRingWidth;
    private float mStart = -210;
    private static final int FULL_ANGLE = 240;
    private float delta = 0;
    private float mspeed = 0;

    float radius;
    int mwidth, mheight;
    int textheight;
    int textwidth;

    int valueheight;
    int valuewidth;

    public SpeedTableView(Context context) {
        super(context);
        init();
    }

    public SpeedTableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SpeedTableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mwidth = w;
        mheight = h;
//        radius= (float) (Math.max(mwidth,mheight)/4*0.8);
//        radius = (float) (mwidth / 2 * 0.8);
        radius= (float) (Math.min(mwidth,mheight)/2*0.8);
    }

    private void init() {
        float density = getContext().getResources().getDisplayMetrics().density;
        mRingWidth = dip * density;

        mRemainderPaint = new Paint();
        mRemainderPaint.setAntiAlias(true); //消除锯齿
        mRemainderPaint.setStyle(Paint.Style.STROKE); //绘制空心圆
        mRemainderPaint.setStrokeWidth(mRingWidth); //设置进度条宽度
        mRemainderPaint.setColor(Color.rgb(31, 34, 34)); //设置进度条颜色
        mRemainderPaint.setStrokeJoin(Paint.Join.ROUND);
        mRemainderPaint.setStrokeCap(Paint.Cap.ROUND); //设置圆角

        mPaint = new Paint();
        mPaint.setAntiAlias(true); //消除锯齿
        mPaint.setStyle(Paint.Style.STROKE); //绘制空心圆
        mPaint.setStrokeWidth(mRingWidth); //设置进度条宽度
        mPaint.setColor(Color.rgb(200, 200, 200)); //设置进度条颜色
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND); //设置圆角

        mtxtPaint = new Paint();
        mtxtPaint.setAntiAlias(true);
        mtxtPaint.setStrokeJoin(Paint.Join.ROUND);
        mtxtPaint.setStrokeCap(Paint.Cap.ROUND); //设置圆角
        mtxtPaint.setTextSize(100);

        tmpPaint = new Paint(mtxtPaint); //小刻度画笔对象
        tmpPaint.setStrokeWidth(2);
        tmpPaint.setTextSize(30);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        int sideLength = Math.min(widthSpecSize, heightSpecSize);
        setMeasuredDimension(sideLength, sideLength);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画底盘及其速度盘
        RectF mBigOval = new RectF(mRingWidth / 2, mRingWidth / 2,
                getWidth() - (mRingWidth + 1) / 2, getHeight() - (mRingWidth + 1) / 2);
        canvas.drawArc(mBigOval, mStart, FULL_ANGLE, false, mRemainderPaint); //底色的弧
        canvas.drawArc(mBigOval, mStart, delta, false, mPaint); // 速度的弧

        //获取文字的宽度及其高度
        Rect rect = new Rect();
        String speed = mspeed + "Km/h";
        mtxtPaint.getTextBounds(speed, 0, speed.length(), rect);
        textheight = rect.height();
        textwidth = rect.width();
        //速度显示
        canvas.drawText(speed, mwidth / 2 - textwidth / 2, mheight / 2 - textheight / 2, mtxtPaint);


//        canvas.save();
        canvas.translate(mwidth / 2, mheight / 2);
        //循转60
        canvas.rotate(60);
//        //画速度值盘
        int count = 45;
        for (int i = 0; i < count; i++) {
            if (i <= 30) {
                if (i % 5 == 0) {
                    canvas.drawLine(0, radius + 10, 0, radius + 60, tmpPaint);
//                    canvas.drawLine(0, radius, 0, radius + 30, tmpPaint);
                    //设置码率
                    Rect recttxt = new Rect();
                    String value = String.valueOf(i / 5 * 50);
                    tmpPaint.getTextBounds(value, 0, value.length(), recttxt);
                    valueheight = recttxt.height();
                    valuewidth = recttxt.width();
                    //文字旋转180
                    canvas.save();
                    canvas.rotate(180, 0, radius);
//                    canvas.drawText(value, -4f, radius + 25f, tmpPaint);
                    canvas.drawText(value, -valuewidth / 2, radius + 25f - valueheight / 2, tmpPaint);
                    canvas.restore();
                } else {
                    canvas.drawLine(0, radius + 40, 0, radius + 60, tmpPaint);
//                    canvas.drawLine(0, radius + 10, 0, radius + 30, tmpPaint);
                }
                canvas.rotate(360 / count, 0f, 0f); //旋转画纸
            }
        }
//        canvas.restore();


        super.onDraw(canvas);
    }

    /**
     * 计算当前速度占最高速度的多少
     * 240是角度
     *
     * @param delta
     */
    public void setDelta(float delta) {
        this.mspeed = delta;
        this.delta = delta * 240 / 300;
        invalidate();
    }

}
