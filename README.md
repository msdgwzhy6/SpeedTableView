# SpeedTableView
自定义view————码表

真的玩起了自定义view，就一直想造点什么自定义view来练练手，今天就码表来给大家讲解一下码表的撰写过程。

![效果图](https://user-gold-cdn.xitu.io/2017/8/9/a6fb6f23d83a09599bf4497ffa6962ad)

效果图看了，废话就不多说了，直接来上真家伙。分享过程：码表的底层背景；码表显示的当前进度；当前速度值显示；速度刻度值；对当前进度进行渐变处理

### 码表的底层背景

效果图可以看出，首先画给黑色的背景，这里用drawArc()来画，算出具体的RectF及其开始角度和结束角度

```
   //开始的角度
    private float mStart = -210;
    //总的角度
    private static final int FULL_ANGLE = 240;
    //进度条的宽度
    private float progressWidth = dipToPx(8);
    
    //画底盘
    RectF mBigOval = new RectF(progressWidth / 2, progressWidth / 2,
                 getWidth() - (progressWidth + 1) / 2, getHeight() - (progressWidth + 1) / 2);
    canvas.drawArc(mBigOval, mStart, FULL_ANGLE, false, mBackgroundPaint); //底色的弧
         
```
mBackgroundPaint的相关设置可以去设置你自己想要的效果   

### 码表的当前进度

码表的底层背景说完了，接下来就来介绍一下码表的时时刻刻变化的当前进度，其实和码表的底层背景的绘制是一样的，只不过就是角度时变化的

首先算出当前速度占这个码表的角度是多少，我这里的最大角度是240，最高速度是300，当时速度占的总角度=当前速度／300*240
```
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

```

角度算好了就是画了

```
canvas.drawArc(mBigOval, mStart, delta, false, mProgressPaint); // 速度的弧

```

到这一步为止，码表就能时时的根据速度来动态变化了，就下来就是来给这个码表加些花了

### 当前速度值显示

速度值动态显示用drawText()就能显示，但是这样的设置，位置不是居中，因此我们需要计算出当前速度值文字显示的宽高，然后计算准确的位置来达到好的展示效果

```
//获取文字的宽度及其高度
        Rect rect = new Rect();
        String speed = mspeed + "Km/h";
        mtxtPaint.getTextBounds(speed, 0, speed.length(), rect);
        textheight = rect.height();
        textwidth = rect.width();
        //速度显示
        canvas.drawText(speed, mwidth / 2 - textwidth / 2, mheight / 2 - textheight / 2, mtxtPaint);

```

到了这一步，去看看车子里的仪表盘差了速度刻度值，不行，这个花也得加上去

### 速度刻度值显示

我这里的最大速度是300，我用50为一个大节点，共分为0、50、100、150、200、250、300，0-50之间有小刻度值，画线drawLine(),画刻度值drawText()，再结合rotate()来达到心中的效果

```
canvas.translate(mwidth / 2, mheight / 2);
        //循转60
        canvas.rotate(60);
//        //画速度刻度值盘
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
        
```
速度刻度值就出来了，需要注意 
* canvas.rotate(60)是为了保证0刻度是和进度是从同一个位置开始的；
* canvas.rotate(360 / count, 0f, 0f); //旋转画纸保证刻度线的位置；
* 文字宽高的计算上面已经提到过了
* canvas.rotate(180, 0, radius);是为了保证文字不是倒立的，需另外注意canvas.save();，canvas.restore();保持和恢复方法

大致完整的功能是已经出来了，接下来就是给进度条的变化加一个大大的彩花

### 当前进度渐变处理

进度渐变处理，对mProgressPaint（当前进度条的画笔）进行属性的设置达到彩色渐变效果，setShader()方法结合SweepGradient

```

   //下面是给渐变颜色做准备的
        //中心点x、y
        centerX = mwidth / 2;
        centerY = mheight / 2;
        sweepGradient = new SweepGradient(centerX, centerY, colors, null);
        rotateMatrix = new Matrix();
        //这个方法很重要
        rotateMatrix.setRotate(118, centerX, centerY);
        sweepGradient.setLocalMatrix(rotateMatrix);
        mProgressPaint.setShader(sweepGradient);

```

### 完整代码

```
package com.speedtableview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
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

    //进度
    private Paint mProgressPaint;
    //背景
    private Paint mBackgroundPaint;
    //速度
    private Paint mtxtPaint;
    //速率刻度
    private Paint tmpPaint;
    //进度条的宽度
    private float progressWidth = dipToPx(8);
    //开始的角度
    private float mStart = -210;
    //总的角度
    private static final int FULL_ANGLE = 240;
    //当前速度占的角度值
    private float delta = 0;
    //速度
    private float mspeed = 0;
    //半径
    float radius;
    //宽高
    int mwidth, mheight;
    //速度值文字显示的宽高
    int textheight;
    int textwidth;
    //速度刻度值文字的宽高
    int valueheight;
    int valuewidth;
    //渐变的颜色
    private int[] colors = new int[]{Color.GREEN, Color.YELLOW, Color.RED, Color.RED};
    //    private PaintFlagsDrawFilter mDrawFilter;
    //扫描/梯度渲染
    private SweepGradient sweepGradient;
    //
    private Matrix rotateMatrix;
    //中心点x,y
    float centerX, centerY;


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
        //宽高
        mwidth = w;
        mheight = h;
        //半径
        radius = (float) (Math.min(mwidth, mheight) / 2 * 0.8);
        //下面是给渐变颜色做准备的
        //中心点x、y
        centerX = mwidth / 2;
        centerY = mheight / 2;
        sweepGradient = new SweepGradient(centerX, centerY, colors, null);
        rotateMatrix = new Matrix();
        //这个方法很重要
        rotateMatrix.setRotate(118, centerX, centerY);
        sweepGradient.setLocalMatrix(rotateMatrix);
        mProgressPaint.setShader(sweepGradient);
    }

    private void init() {
        //背景
        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true); //消除锯齿
        mBackgroundPaint.setStyle(Paint.Style.STROKE); //绘制空心圆
        mBackgroundPaint.setStrokeWidth(progressWidth); //设置进度条宽度
        mBackgroundPaint.setColor(Color.rgb(31, 34, 34)); //设置进度条颜色
        mBackgroundPaint.setStrokeJoin(Paint.Join.ROUND);
        mBackgroundPaint.setStrokeCap(Paint.Cap.ROUND); //设置圆角
        //进度
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true); //消除锯齿
        mProgressPaint.setStyle(Paint.Style.STROKE); //绘制空心圆
        mProgressPaint.setStrokeWidth(progressWidth); //设置进度条宽度
        mProgressPaint.setColor(Color.rgb(200, 200, 200)); //设置进度条颜色
        mProgressPaint.setStrokeJoin(Paint.Join.ROUND);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND); //设置圆角
        //速度值
        mtxtPaint = new Paint();
        mtxtPaint.setAntiAlias(true);
        mtxtPaint.setStrokeJoin(Paint.Join.ROUND);
        mtxtPaint.setStrokeCap(Paint.Cap.ROUND); //设置圆角
        mtxtPaint.setTextSize(100);
        //速度刻度值
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
        RectF mBigOval = new RectF(progressWidth / 2, progressWidth / 2,
                getWidth() - (progressWidth + 1) / 2, getHeight() - (progressWidth + 1) / 2);
        canvas.drawArc(mBigOval, mStart, FULL_ANGLE, false, mBackgroundPaint); //底色的弧
        canvas.drawArc(mBigOval, mStart, delta, false, mProgressPaint); // 速度的弧

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
//        //画速度刻度值盘
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

    private int dipToPx(float dip) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5f * (dip >= 0 ? 1 : -1));
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

```


以上还有很多可优化及其可提取出来的属性设置，可根据自己项目需求进行修改

[个人博客](https://madreain.github.io/)
