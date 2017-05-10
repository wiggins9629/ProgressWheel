package com.wiggins.progresswheel.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.wiggins.progresswheel.R;

/**
 * @Description 单百分比或多百分比进度条
 * @Author 一花一世界
 */
public class RoundProgressBar extends View {

    // 进度画笔
    private Paint mProgressPaint = new Paint();
    // 圆环画笔
    private Paint mRoundPaint = new Paint();
    // 绘制字体的画笔
    private Paint mTextPaint = new Paint();
    // 圆边界
    private RectF mBounds;

    // 圆环的宽度
    private float mRoundWidth;
    // 圆环的颜色
    private int mRoundColor;
    // 进度的颜色
    private int mProgressColor;
    // 进度百分比
    private float mProgressRatio;
    // 字体颜色
    private int mTextColor;
    // 字体大小
    private float mTextSize;
    // 是否显示中间的进度值
    private boolean mTextIsShow;

    // 圆环颜色 - orange
    private int defaultRoundColor = Color.parseColor("#fa7c20");
    // 进度颜色 - red
    private int defaultProgressColor = Color.parseColor("#ea5450");
    // 字体颜色 - black
    private int defaultTextColor = Color.parseColor("#333333");
    // 进度百分比
    private float defaultProgressRatio = 0;
    // 圆环宽度
    private float defaultRoundWidth = dip2px(5);
    // 字体大小
    private float defaultTextSize = dip2px(15);
    // 是否显示中间进度值
    private boolean defaultTextIsShow = true;

    // 进度颜色数组
    private String[] mColors;
    // 进度值数组
    private float[] mRatio;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);

        // 获取自定义属性和默认值
        mRoundColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, defaultRoundColor);
        mRoundWidth = mTypedArray.getFloat(R.styleable.RoundProgressBar_roundWidth, defaultRoundWidth);
        mProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_progressColor, defaultProgressColor);
        mProgressRatio = mTypedArray.getFloat(R.styleable.RoundProgressBar_progressRatio, defaultProgressRatio);
        mTextColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColors, defaultTextColor);
        mTextSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSizes, defaultTextSize);
        mTextIsShow = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsShow, defaultTextIsShow);

        mTypedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setupBounds();
        setupPaints();
        invalidate();
    }

    private void setupBounds() {
        int minValue = Math.min(getWidth(), getHeight());
        int centre = minValue / 2; // 获取圆心的x坐标
        int radius = (int) (centre - mRoundWidth / 2); // 圆环的半径
        mBounds = new RectF(centre - radius, centre - radius, centre + radius, centre + radius);
    }

    private void setupPaints() {
        mRoundPaint.setColor(mRoundColor);// 设置画笔颜色
        mRoundPaint.setAntiAlias(true); // 消除锯齿
        mRoundPaint.setStyle(Style.STROKE); // 设置空心
        mRoundPaint.setStrokeWidth(mRoundWidth);// 设置线宽

        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);

        mProgressPaint.setStrokeWidth(mRoundWidth);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制边界
        canvas.drawArc(mBounds, 0, 360, false, mRoundPaint);

        // 绘制进度百分比
        int percent = Math.round((mProgressRatio / 360) * 100);
        // 测量字体宽度
        float textWidth = mTextPaint.measureText(percent + "%");
        // 绘制的起点X轴坐标：画布宽度的一半 - 文字宽度的一半
        int baseX = (int) (canvas.getWidth() / 2 - textWidth / 2);
        // 绘制的起点Y轴坐标：画布高度的一半 - 文字总高度的一半
        int baseY = (int) ((canvas.getHeight() / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));

        // 绘制进度
        float start = -90;
        if (mRatio != null) {
            for (int i = 0; i < mRatio.length; i++) {
                mProgressPaint.setColor(Color.parseColor(mColors[i]));// 进度的颜色
                canvas.drawArc(mBounds, start, mRatio[i], false, mProgressPaint);
                start += mRatio[i];
            }
        } else {
            mProgressPaint.setColor(mProgressColor); // 进度的颜色
            canvas.drawArc(mBounds, start, mProgressRatio, false, mProgressPaint);
            if (mTextIsShow) {
                canvas.drawText(percent + "%", baseX, baseY, mTextPaint);
            }
        }
    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void setProgress(float[] ratio, String[] colors) {
        this.mRatio = ratio;
        this.mColors = colors;
        postInvalidate();
    }

    public float getProgressRatio() {
        return mProgressRatio;
    }

    public void setProgressRatio(float progressRatio) {
        this.mProgressRatio = progressRatio;
        postInvalidate();
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
        if (this.mTextPaint != null) {
            this.mTextPaint.setColor(textColor);
        }
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
        if (this.mTextPaint != null) {
            this.mTextPaint.setTextSize(textSize);
        }
    }

    public float getRoundWidth() {
        return mRoundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.mRoundWidth = roundWidth;
        if (this.mRoundPaint != null) {
            this.mRoundPaint.setStrokeWidth(roundWidth);
        }
    }

    public int getRoundColor() {
        return mRoundColor;
    }

    public void setRoundColor(int roundColor) {
        this.mRoundColor = roundColor;
        if (this.mRoundPaint != null) {
            this.mRoundPaint.setColor(roundColor);
        }
    }

    public int getProgressColor() {
        return mProgressColor;
    }

    public void setProgressColor(int progressColor) {
        this.mProgressColor = progressColor;
        if (this.mProgressPaint != null) {
            this.mProgressPaint.setColor(progressColor);
        }
    }

    public boolean isTextIsShow() {
        return mTextIsShow;
    }

    public void setTextIsShow(boolean textIsShow) {
        this.mTextIsShow = textIsShow;
    }
}