package recitewords.apj.com.recitewords.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Greetty on 2016/12/11.
 * <p/>
 * 滑动删除
 */

public class SwipeLayout extends LinearLayout {

    private ViewDragHelper viewDragHelper;
    private View contentView;
    private View actionView;
    private int dragDistance;
    private final double AUTO_OPEN_SPEED_LIMIT = 800.0;
    private int draggedX;
    private OnSweepListener mListener;
    private  boolean settleToOpen;  //是否打开

    public SwipeLayout(Context context) {
        this(context, null);
    }

    public SwipeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public SwipeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        viewDragHelper = ViewDragHelper.create(this, new DragHelperCallback());
    }

    @Override
    protected void onFinishInflate() {
        contentView = getChildAt(0);
        actionView = getChildAt(1);
        actionView.setVisibility(GONE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        dragDistance = actionView.getMeasuredWidth();
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(View view, int i) {
            return view == contentView || view == actionView;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            draggedX = left;
            if (changedView == contentView) {
                actionView.offsetLeftAndRight(dx);
            } else {
                contentView.offsetLeftAndRight(dx);
            }
            if (actionView.getVisibility() == View.GONE) {
                actionView.setVisibility(View.VISIBLE);
            }
            invalidate();
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contentView) {
                final int leftBound = getPaddingLeft();
                final int minLeftBound = -leftBound - dragDistance;
                final int newLeft = Math.min(Math.max(minLeftBound, left), 0);
                return newLeft;
            } else {
                final int minLeftBound = getPaddingLeft() + contentView.getMeasuredWidth() - dragDistance;
                final int maxLeftBound = getPaddingLeft() + contentView.getMeasuredWidth() + getPaddingRight();
                final int newLeft = Math.min(Math.max(left, minLeftBound), maxLeftBound);
                return newLeft;
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return dragDistance;
//            return contentView == child ? child.getWidth() : 0;
//            return contentView.getWidth()+actionView.getWidth();
        }

//        @Override
        public int getViewVerticalDragRange(View child) {
            return contentView == child ? child.getHeight() : 0;
//            return super.getViewVerticalDragRange(child);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            settleToOpen = false;
            if (xvel > AUTO_OPEN_SPEED_LIMIT) {
                mListener.OnSweepChanged(SwipeLayout.this,false);  //监听状态改变
                settleToOpen = false;
            } else if (xvel < -AUTO_OPEN_SPEED_LIMIT) {
                mListener.OnSweepChanged(SwipeLayout.this,true);
                settleToOpen = true;
            } else if (draggedX <= -dragDistance / 2) {
                mListener.OnSweepChanged(SwipeLayout.this,true);
                settleToOpen = true;
            } else if (draggedX > -dragDistance / 2) {
                mListener.OnSweepChanged(SwipeLayout.this,false);
                settleToOpen = false;
            }

            final int settleDestX = settleToOpen ? -dragDistance : 0;
            viewDragHelper.smoothSlideViewTo(contentView, settleDestX, 0);
            ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {


        if (viewDragHelper.shouldInterceptTouchEvent(event)) {
            return true;
        }
        float mDownX = 0;
        float mDownY = 0;
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();

                if (Math.abs(moveY - mDownY) < Math.abs(moveX - mDownX)) {
                    // 上下滑动，拦截事件，不给子控件事件，自己需要处理
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        return super.onInterceptTouchEvent(event);

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * 关闭删除按钮
     */
    public void closeSwipe() {
        viewDragHelper.smoothSlideViewTo(contentView, 0, 0);
        settleToOpen=false;
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    /**
     * 打开删除按钮
     */
    public void openSwipe() {
        settleToOpen=true;
        viewDragHelper.smoothSlideViewTo(contentView, -dragDistance, 0);
        ViewCompat.postInvalidateOnAnimation(SwipeLayout.this);
    }

    /**
     * 获取当前控件是否打开
     * @return
     */
    public boolean getOpenSwipeState(){
        return settleToOpen;
    }

    /**
     * 暴露接口方法
     * @param listener 监听者
     */
    public void setOnSweepListener(OnSweepListener listener) {
        this.mListener = listener;
    }

    public interface OnSweepListener {
        void OnSweepChanged(SwipeLayout view, boolean mIsOpened);
    }

}
