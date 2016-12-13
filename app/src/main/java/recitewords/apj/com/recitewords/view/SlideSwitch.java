package recitewords.apj.com.recitewords.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import recitewords.apj.com.recitewords.R;

/**
 * Created by Administrator on 2016/12/12 0012.
 */
public class SlideSwitch extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG); //抗锯齿

    boolean isOn = false;
    float curX = 0;
    float centerY; //y固定
    float viewWidth;
    float radius;
    float lineStart; //直线段开始的位置（横坐标，即
    float lineEnd; //直线段结束的位置（纵坐标
    float lineWidth;
    final int SCALE = 4; // 控件长度为滑动的圆的半径的倍数
    OnStateChangedListener onStateChangedListener;

    public SlideSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SlideSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlideSwitch(Context context) {
        super(context);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        curX = event.getX();
        if(event.getAction() == MotionEvent.ACTION_UP)
        {
            if(curX > viewWidth / 2)
            {
                curX = lineEnd;
                if(false == isOn)
                {
                    //只有状态发生改变才调用回调函数， 下同
                    if(null != onStateChangedListener)
                    {
                        onStateChangedListener.onStateChanged(true);
                    }
                    isOn = true;
                }
            }
            else
            {
                curX = lineStart;
                if(true == isOn)
                {
                    if(null != onStateChangedListener)
                    {
                        onStateChangedListener.onStateChanged(false);
                    }
                    isOn = false;
                }
            }
        }
        /*通过刷新调用onDraw*/
        this.postInvalidate();
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /*保持宽是高的SCALE / 2倍， 即圆的直径*/
        this.setMeasuredDimension(this.getMeasuredWidth(), this.getMeasuredWidth() * 2 / SCALE);
        viewWidth = this.getMeasuredWidth();
        radius = viewWidth / SCALE;
        lineWidth = radius * 2f; //直线宽度等于滑块直径
        curX = radius;
        centerY = this.getMeasuredWidth() / SCALE; //centerY为高度的一半
        lineStart = radius;
        lineEnd = (SCALE - 1) * radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        /*限制滑动范围*/
        curX = curX > lineEnd?lineEnd:curX;
        curX = curX < lineStart?lineStart:curX;

        /*划线*/
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(lineWidth);
        /*开关打开时左边的背景色*/
        mPaint.setColor(getResources().getColor(R.color.colorWhite));
        canvas.drawLine(lineStart, centerY, curX, centerY, mPaint);
        /*开关关闭时右边的背景色*/
        mPaint.setColor(getResources().getColor(R.color.color_item_word));
        canvas.drawLine(curX, centerY, lineEnd, centerY, mPaint);

        /*画圆*/
        /*画最左和最右的圆，直径为直线段宽度， 即在直线段两边分别再加上一个半圆*/
        mPaint.setStyle(Paint.Style.FILL);
        /*开关关闭时，右边圆形的颜色*/
        mPaint.setColor(getResources().getColor(R.color.color_item_word));
        canvas.drawCircle(lineEnd, centerY, lineWidth / 2, mPaint);
        /*开关关闭时，左边圆形的颜色*/
        mPaint.setColor(getResources().getColor(R.color.colorWhite));
        canvas.drawCircle(lineStart, centerY, lineWidth / 2, mPaint);
        /*圆形滑块的颜色*/
        mPaint.setColor(getResources().getColor(R.color.color_item_word_mean));
        canvas.drawCircle(curX, centerY, radius , mPaint);

    }
    /*设置开关状态改变监听器*/
    public void setOnStateChangedListener(OnStateChangedListener o)
    {
        this.onStateChangedListener = o;
    }

    /*内部接口，开关状态改变监听器*/
    public interface OnStateChangedListener
    {
        public void onStateChanged(boolean state);
    }
}
