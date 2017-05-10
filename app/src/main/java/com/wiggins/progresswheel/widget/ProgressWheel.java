package com.wiggins.progresswheel.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.wiggins.progresswheel.R;

/**
 * @Description 进度条
 * @Author 一花一世界
 */
public class ProgressWheel extends View {

    //绘制View用到的各种默认长、宽度大小
    private int layoutHeight = 0;
    private int layoutWidth = 0;
    private int barWidth = 20;//进度条宽度
    private int rimWidth = 20;//默认轮廓宽度
    private int defaultProgress = 0;//默认进度
    private float textSize = dip2px(15);//字体大小
    private float outerEdgeSize = 0;//外边缘大小
    private float innerEdgeSize = 0;//内边缘大小

    //与页边的默认间距
    private int paddingTop = 0;
    private int paddingBottom = 0;
    private int paddingLeft = 0;
    private int paddingRight = 0;

    //View要绘制的默认颜色
    private int barColor = 0xAA000000;//进度条
    private int rimColor = 0xAADDDDDD;//默认轮廓
    private int textColor = 0xFF000000;//字体
    private int circleInnerColor = 0x00000000;//圈内
    private int outerEdgeColor = 0x00000000;//外边缘
    private int innerEdgeColor = 0x00000000;//内边缘

    //绘制要用的画笔
    private Paint barPaint = new Paint();//进度条
    private Paint rimPaint = new Paint();//默认轮廓
    private Paint textPaint = new Paint();//字体
    private Paint circleInnerPaint = new Paint();//圈内
    private Paint outerEdgePaint = new Paint();//外边缘
    private Paint innerEdgePaint = new Paint();//内边缘

    //绘制要用的矩形
    private RectF outerEdgeContour = new RectF();//外边缘
    private RectF innerEdgeContour = new RectF();//内边缘
    private RectF innerEdgeBounds = new RectF();//内圈边界
    private RectF outerEdgeBounds = new RectF();//外圈边界

    //其他
    private String text = "";
    private float progress = 0;
    private String[] splitText = {};

    /**
     * ProgressWheel的构造方法
     *
     * @param context
     * @param attrs
     */
    public ProgressWheel(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttributes(context.obtainStyledAttributes(attrs, R.styleable.ProgressWheel));
    }

    /********************* 初始化一些元素 *********************/

    /**
     * 调用这个方法时，使View绘制为方形
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 在这里我们不能使用getWidth()和getHeight()，因为这两个方法只能在View的布局完成后才能使用，
         * 而一个View的绘制过程是先绘制元素，再绘制Layout，所以我们必须使用getMeasuredWidth()和getMeasuredHeight()。
         */
        int size = 0;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heightWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        /**
         * 在比较View的长宽前我们不考虑间距，但当我们设置View所需要绘制的面积时，我们则要考虑它。
         * 不考虑间距的View（View内的实际画面）此时就应该是方形的，但是由于间距的存在，最终View所占的面积可能不是方形的。
         */
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (heightMode != MeasureSpec.UNSPECIFIED && widthMode != MeasureSpec.UNSPECIFIED) {
            if (widthWithoutPadding > heightWithoutPadding) {
                size = heightWithoutPadding;
            } else {
                size = widthWithoutPadding;
            }
        } else {
            size = Math.max(heightWithoutPadding, widthWithoutPadding);
        }

        /**
         * 如果你重写了onMeasure()方法，你必须调用setMeasuredDimension()方法，这是你设置View大小的唯一途径。
         * 如果你不调用setMeasuredDimension()方法，父控件会抛出异常，并且程序会崩溃。
         * 如果我们使用了超类的onMeasure()方法，我们就不是那么需要setMeasuredDimension()方法，然而，
         * 重写onMeasure()方法是为了改变既有的绘制流程，所以我们必须调用setMeasuredDimension()方法以达到我们的目的。
         */
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    /**
     * 使用onSizeChanged方法代替onAttachedToWindow获得View的面积，因为这个方法会在测量了 MATCH_PARENT 和 WRAP_CONTENT 后马上被调用使用获得的面积设置View。
     */
    @Override
    protected void onSizeChanged(int newWidth, int newHeight, int oldWidth, int oldHeight) {
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        layoutWidth = newWidth;
        layoutHeight = newHeight;
        setupBounds();
        setupPaints();
        invalidate();
    }

    /**
     * 设置元素边界
     */
    private void setupBounds() {
        // 为了保持宽度和长度的一致，我们要获得layout_width和layout_height中较小的一个，从而绘制一个圆
        int minValue = Math.min(layoutWidth, layoutHeight);

        // 计算在绘制过程中在x，y方向的偏移量
        int xOffset = layoutWidth - minValue;
        int yOffset = layoutHeight - minValue;

        // 间距加上偏移量
        paddingTop = this.getPaddingTop() + (yOffset / 2);
        paddingBottom = this.getPaddingBottom() + (yOffset / 2);
        paddingLeft = this.getPaddingLeft() + (xOffset / 2);
        paddingRight = this.getPaddingRight() + (xOffset / 2);

        int width = getWidth();
        int height = getHeight();

        innerEdgeBounds = new RectF(
                paddingLeft + (1.5f * barWidth),
                paddingTop + (1.5f * barWidth),
                width - paddingRight - (1.5f * barWidth),
                height - paddingBottom - (1.5f * barWidth));

        outerEdgeBounds = new RectF(
                paddingLeft + barWidth,
                paddingTop + barWidth,
                width - paddingRight - barWidth,
                height - paddingBottom - barWidth);

        innerEdgeContour = new RectF(
                outerEdgeBounds.left + (rimWidth / 2.0f) + (outerEdgeSize / 2.0f),
                outerEdgeBounds.top + (rimWidth / 2.0f) + (outerEdgeSize / 2.0f),
                outerEdgeBounds.right - (rimWidth / 2.0f) - (outerEdgeSize / 2.0f),
                outerEdgeBounds.bottom - (rimWidth / 2.0f) - (outerEdgeSize / 2.0f));

        outerEdgeContour = new RectF(
                outerEdgeBounds.left - (rimWidth / 2.0f) - (innerEdgeSize / 2.0f),
                outerEdgeBounds.top - (rimWidth / 2.0f) - (innerEdgeSize / 2.0f),
                outerEdgeBounds.right + (rimWidth / 2.0f) + (innerEdgeSize / 2.0f),
                outerEdgeBounds.bottom + (rimWidth / 2.0f) + (innerEdgeSize / 2.0f));
    }

    /**
     * 绘制元素属性
     */
    private void setupPaints() {
        //进度条
        barPaint.setColor(barColor);//设置画笔颜色
        barPaint.setAntiAlias(true);//设置抗锯齿
        barPaint.setStyle(Style.STROKE);//设置画笔为空心
        barPaint.setStrokeWidth(barWidth);//设置线宽

        //圆环
        rimPaint.setColor(rimColor);
        rimPaint.setAntiAlias(true);
        rimPaint.setStyle(Style.STROKE);
        rimPaint.setStrokeWidth(rimWidth);

        //环内
        circleInnerPaint.setColor(circleInnerColor);
        circleInnerPaint.setAntiAlias(true);
        circleInnerPaint.setStyle(Style.FILL);

        //字体
        textPaint.setColor(textColor);
        textPaint.setStyle(Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);

        //外边缘
        outerEdgePaint.setColor(outerEdgeColor);
        outerEdgePaint.setAntiAlias(true);
        outerEdgePaint.setStyle(Style.STROKE);
        outerEdgePaint.setStrokeWidth(outerEdgeSize);

        //内边缘
        innerEdgePaint.setColor(innerEdgeColor);
        innerEdgePaint.setAntiAlias(true);
        innerEdgePaint.setStyle(Style.STROKE);
        innerEdgePaint.setStrokeWidth(innerEdgeSize);
    }

    /**
     * 从XML中解析控件的属性
     *
     * @param a the attributes to parse
     */
    private void parseAttributes(TypedArray a) {
        textColor = a.getColor(R.styleable.ProgressWheel_pwTextColor, textColor);
        textSize = a.getDimension(R.styleable.ProgressWheel_pwTextSize, textSize);

        barWidth = a.getInteger(R.styleable.ProgressWheel_pwBarWidth, barWidth);
        barColor = a.getColor(R.styleable.ProgressWheel_pwBarColor, barColor);
        defaultProgress = a.getInteger(R.styleable.ProgressWheel_pwDefaultProgress, defaultProgress);

        if (a.hasValue(R.styleable.ProgressWheel_pwText)) {
            setText(a.getString(R.styleable.ProgressWheel_pwText));
        } else {
            setProgress(defaultProgress);
        }

        rimWidth = a.getInteger(R.styleable.ProgressWheel_pwRimWidth, rimWidth);
        rimColor = a.getColor(R.styleable.ProgressWheel_pwRimColor, rimColor);

        circleInnerColor = a.getColor(R.styleable.ProgressWheel_pwCircleInnerColor, circleInnerColor);

        outerEdgeSize = a.getDimension(R.styleable.ProgressWheel_pwOuterEdgeSize, outerEdgeSize);
        outerEdgeColor = a.getColor(R.styleable.ProgressWheel_pwOuterEdgeColor, outerEdgeColor);

        innerEdgeSize = a.getDimension(R.styleable.ProgressWheel_pwInnerEdgeSize, innerEdgeSize);
        innerEdgeColor = a.getColor(R.styleable.ProgressWheel_pwInnerEdgeColor, innerEdgeColor);

        // 使用TypedArray获得控件属性时必须要注意：使用结束后必须回收TypedArray的对象
        a.recycle();
    }

    /********************* 动画 *********************/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制边界
        canvas.drawArc(innerEdgeBounds, 360, 360, false, circleInnerPaint);
        canvas.drawArc(outerEdgeBounds, 360, 360, false, rimPaint);

        //绘制边缘
        canvas.drawArc(outerEdgeContour, 360, 360, false, outerEdgePaint);
        canvas.drawArc(innerEdgeContour, 360, 360, false, innerEdgePaint);

        //绘制进度
        canvas.drawArc(outerEdgeBounds, -90, progress, false, barPaint);

        //绘制文字(并让它显示在圆水平和垂直方向的中心处)
        float textHeight = textPaint.descent() - textPaint.ascent();
        float verticalTextOffset = (textHeight / 2) - textPaint.descent();

        for (String line : splitText) {
            float horizontalTextOffset = textPaint.measureText(line) / 2;
            canvas.drawText(
                    line,
                    this.getWidth() / 2 - horizontalTextOffset,
                    this.getHeight() / 2 + verticalTextOffset,
                    textPaint);
        }
    }

    /**
     * @Description 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    private int dip2px(float dpValue) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 重设进度条的值
     */
    public void resetCount() {
        progress = 0;
        setText("0%");
        invalidate();
    }

    /**
     * 让进度条每次增加1（最大值为360）
     */
    public void incrementProgress() {
        incrementProgress(1);
    }

    public void incrementProgress(int amount) {
        progress += amount;
        if (progress >= 360) {
            progress = 360;
        }
        setText(Math.round((progress / 360) * 100) + "%");
        postInvalidate();
    }

    /**
     * 更新视图
     */
    public void resetView() {
        postInvalidate();
    }

    /********************* get和set方法 *********************/

    /**
     * 设置进度条为一个确切的数值
     */
    public void setProgress(int pg) {
        progress = pg;
        if (progress >= 360) {
            progress = 360;
        }
        setText(Math.round((progress / 360) * 100) + "%");
        postInvalidate();
    }

    /**
     * @Description 获取当前进度
     */
    public int getProgress() {
        return (int) progress;
    }

    /**
     * 设置progress bar的文字并不需要刷新View
     */
    public void setText(String text) {
        this.text = text;
        splitText = this.text.split("\n");
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        if (this.textPaint != null) {
            this.textPaint.setTextSize(this.textSize);
        }
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        if (this.textPaint != null) {
            this.textPaint.setColor(this.textColor);
        }
    }

    public int getBarColor() {
        return barColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;
        if (this.barPaint != null) {
            this.barPaint.setColor(this.barColor);
        }
    }

    public int getBarWidth() {
        return barWidth;
    }

    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
        if (this.barPaint != null) {
            this.barPaint.setStrokeWidth(this.barWidth);
        }
    }

    public int getRimColor() {
        return rimColor;
    }

    public void setRimColor(int rimColor) {
        this.rimColor = rimColor;
        if (this.rimPaint != null) {
            this.rimPaint.setColor(this.rimColor);
        }
    }

    public int getRimWidth() {
        return rimWidth;
    }

    public void setRimWidth(int rimWidth) {
        this.rimWidth = rimWidth;
        if (this.rimPaint != null) {
            this.rimPaint.setStrokeWidth(this.rimWidth);
        }
    }

    public Shader getRimShader() {
        return rimPaint.getShader();
    }

    public void setRimShader(Shader shader) {
        this.rimPaint.setShader(shader);
    }

    public int getCircleInnerColor() {
        return circleInnerColor;
    }

    public void setCircleInnerColor(int circleInnerColor) {
        this.circleInnerColor = circleInnerColor;
        if (this.circleInnerPaint != null) {
            this.circleInnerPaint.setColor(this.circleInnerColor);
        }
    }

    public int getOuterEdgeColor() {
        return outerEdgeColor;
    }

    public void setOuterEdgeColor(int outerEdgeColor) {
        this.outerEdgeColor = outerEdgeColor;
        if (outerEdgePaint != null) {
            this.outerEdgePaint.setColor(this.outerEdgeColor);
        }
    }

    public float getOuterEdgeSize() {
        return this.outerEdgeSize;
    }

    public void setOuterEdgeSize(float outerEdgeSize) {
        this.outerEdgeSize = outerEdgeSize;
        if (outerEdgePaint != null) {
            this.outerEdgePaint.setStrokeWidth(this.outerEdgeSize);
        }
    }

    public int getInnerEdgeColor() {
        return innerEdgeColor;
    }

    public void setInnerEdgeColor(int innerEdgeColor) {
        this.innerEdgeColor = innerEdgeColor;
        if (innerEdgePaint != null) {
            this.innerEdgePaint.setColor(this.innerEdgeColor);
        }
    }

    public float getInnerEdgeSize() {
        return this.innerEdgeSize;
    }

    public void setInnerEdgeSize(float innerEdgeSize) {
        this.innerEdgeSize = innerEdgeSize;
        if (innerEdgePaint != null) {
            this.innerEdgePaint.setStrokeWidth(this.innerEdgeSize);
        }
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }
}
