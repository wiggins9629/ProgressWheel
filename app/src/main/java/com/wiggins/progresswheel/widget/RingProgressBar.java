package com.wiggins.progresswheel.widget;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.wiggins.progresswheel.R;

/**
 * @Description 进度条
 * @Author 一花一世界
 */
public class RingProgressBar extends View {

    // 绘制圆环的画笔
    private Paint mRingPaint = new Paint();
    // 绘制字体的画笔
    private Paint mTextPaint = new Paint();
    // 绘制进度的画笔
    private Paint mProgressPaint = new Paint();

    // 圆环的颜色
    private int mRingColor;
    // 圆环的宽度
    private float mRingWidth;
    // 圆环是否空心
    private boolean mRingIsStroke;
    // 进度的颜色
    private int mRingProgressColor;
    // 进度条的风格：实心或者空心
    private int mRingProgressStyle;
    // 中间进度百分比字符串的字体颜色
    private int mTextColor;
    // 中间进度百分比字符串的字体大小
    private float mTextSize;
    // 是否显示中间的进度值
    private boolean mTextIsDisplayable;
    // 最大进度值
    private int mMaxProgress;
    // 当前进度值
    private int mCurrentProgress;

    // 圆环颜色 - orange
    private int defaultRingColor = Color.parseColor("#fa7c20");
    // 进度颜色 - red
    private int defaultRingProgressColor = Color.parseColor("#ea5450");
    // 字体颜色 - black
    private int defaultTextColor = Color.parseColor("#333333");
    // 圆环宽度
    private float defaultRingWidth = dip2px(5);
    // 字体大小
    private float defaultTextSize = dip2px(15);
    // 进度条的风格：实心或者空心
    private int defaultRingProgressStyle = 0;
    // 最大进度值
    private int defaultMaxProgress = 100;
    // 圆环是否空心
    private boolean defaultRingIsStroke = true;
    // 是否显示中间进度值
    private boolean defaultTextIsDisplayable = true;

    public static final int STROKE = 0;
    public static final int FILL = 1;

    public RingProgressBar(Context context) {
        this(context, null);
    }

    public RingProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.RingProgressBar);

        // 获取自定义属性和默认值
        mRingColor = mTypedArray.getColor(R.styleable.RingProgressBar_ringColor, defaultRingColor);
        mRingWidth = mTypedArray.getDimension(R.styleable.RingProgressBar_ringWidth, defaultRingWidth);
        mRingIsStroke = mTypedArray.getBoolean(R.styleable.RingProgressBar_ringIsStroke, defaultRingIsStroke);

        mRingProgressColor = mTypedArray.getColor(R.styleable.RingProgressBar_ringProgressColor, defaultRingProgressColor);
        mRingProgressStyle = mTypedArray.getInt(R.styleable.RingProgressBar_ringProgressStyle, defaultRingProgressStyle);

        mTextColor = mTypedArray.getColor(R.styleable.RingProgressBar_textColor, defaultTextColor);
        mTextSize = mTypedArray.getDimension(R.styleable.RingProgressBar_textSize, defaultTextSize);
        mTextIsDisplayable = mTypedArray.getBoolean(R.styleable.RingProgressBar_textIsDisplayable, defaultTextIsDisplayable);

        mMaxProgress = mTypedArray.getInteger(R.styleable.RingProgressBar_maxProgress, defaultMaxProgress);

        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 绘制默认圆环
         */
        int centre = getWidth() / 2; // 获取圆心的x坐标
        int radius = (int) (centre - mRingWidth / 2); // 圆环的半径
        mRingPaint.setColor(mRingColor); // 圆环颜色
        mRingPaint.setStrokeWidth(mRingWidth); // 圆环宽度
        mRingPaint.setAntiAlias(true); // 消除锯齿
        if (mRingIsStroke) {
            mRingPaint.setStyle(Paint.Style.STROKE); // 设置空心
        } else {
            mRingPaint.setStyle(Paint.Style.FILL); // 设置实心
        }
        canvas.drawCircle(centre, centre, radius, mRingPaint); // 画出圆环

        /**
         * 绘制进度百分比
         */
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
        mTextPaint.setStyle(Paint.Style.FILL);
        mTextPaint.setAntiAlias(true);
        // 进度百分比
        int percent = (int) (((float) mCurrentProgress / (float) mMaxProgress) * 100);
        // 测量字体宽度
        float textWidth = mTextPaint.measureText(percent + "%");

        // 绘制的起点X轴坐标：画布宽度的一半 - 文字宽度的一半
        int baseX = (int) (canvas.getWidth() / 2 - textWidth / 2);
        // 绘制的起点Y轴坐标：画布高度的一半 - 文字总高度的一半
        int baseY = (int) ((canvas.getHeight() / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));

        if (mTextIsDisplayable && mRingProgressStyle == STROKE) {
            canvas.drawText(percent + "%", baseX, baseY, mTextPaint);
        }

        /**
         * 绘制进度的圆弧
         */
        mProgressPaint.setStrokeWidth(mRingWidth);
        mProgressPaint.setColor(mRingProgressColor);
        mProgressPaint.setAntiAlias(true);
        RectF oval = new RectF(centre - radius, centre - radius, centre + radius, centre + radius); // 用于定义的圆弧的形状和大小的界限
        switch (mRingProgressStyle) {
            case STROKE: {
                mProgressPaint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 270, 360 * mCurrentProgress / mMaxProgress, false, mProgressPaint); // 根据进度画圆弧
                break;
            }
            case FILL: {
                mProgressPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (mCurrentProgress != 0) {
                    canvas.drawArc(oval, 270, 360 * mCurrentProgress / mMaxProgress, true, mProgressPaint); // 根据进度画圆弧
                }
                break;
            }
        }
    }

    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public int getCurrentProgress() {
        return mCurrentProgress;
    }

    public void setCurrentProgress(int currentProgress) {
        this.mCurrentProgress = currentProgress;
        if (currentProgress < 0) {
            throw new IllegalArgumentException("currentProgress not less than 0");
        }
        if (currentProgress > mMaxProgress) {
            currentProgress = mMaxProgress;
        }
        if (currentProgress <= mMaxProgress) {
            this.mCurrentProgress = currentProgress;
        }
        postInvalidate();
    }

    public int getMaxProgress() {
        return mMaxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        if (maxProgress < 0) {
            throw new IllegalArgumentException("maxProgress not less than 0");
        }
        this.mMaxProgress = maxProgress;
    }

    public int getRingColor() {
        return mRingColor;
    }

    public void setRingColor(int ringColor) {
        this.mRingColor = ringColor;
    }

    public float getRingWidth() {
        return mRingWidth;
    }

    public void setRingWidth(float ringWidth) {
        this.mRingWidth = ringWidth;
    }

    public boolean isRingIsStroke() {
        return mRingIsStroke;
    }

    public void setRingIsStroke(boolean ringIsStroke) {
        this.mRingIsStroke = ringIsStroke;
    }

    public int getRingProgressColor() {
        return mRingProgressColor;
    }

    public void setRingProgressColor(int ringProgressColor) {
        this.mRingProgressColor = ringProgressColor;
    }

    public int getRingProgressStyle() {
        return mRingProgressStyle;
    }

    public void setRingProgressStyle(int ringProgressStyle) {
        this.mRingProgressStyle = ringProgressStyle;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public void setTextColor(int textColor) {
        this.mTextColor = textColor;
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
    }

    public boolean isTextIsDisplayable() {
        return mTextIsDisplayable;
    }

    public void setTextIsDisplayable(boolean textIsDisplayable) {
        this.mTextIsDisplayable = textIsDisplayable;
    }
}