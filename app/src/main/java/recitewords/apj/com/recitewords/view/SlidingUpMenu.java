package recitewords.apj.com.recitewords.view;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by CGT on 2016/11/29.
 * <p/>
 * 滑动显示头部菜单
 */
public class SlidingUpMenu extends ViewGroup {

    private View mTopView;  //头部菜单
    private View mContentView;  //主页面内容
    private int mUpHeight;  //头部菜单的高度
    private float mDownX;
    private float mDownY;
    private boolean mUpMenuOpen=false;  //是否打开头部菜单栏
    private OnToggleListener mListener;

    private Scroller scroller;

    public SlidingUpMenu(Context context) {
        super(context);
        scroller=new Scroller(context);
    }

    public SlidingUpMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller=new Scroller(context);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //获取view
        mTopView = getChildAt(0);
        mContentView = getChildAt(1);
        // 获取头部菜单的高
        LayoutParams params = mTopView.getLayoutParams();
        mUpHeight = params.height;
    }


    //测量
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //合成32位数值
        int topWidthMeasureSpec = MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY);
        int topHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mUpHeight, MeasureSpec.UNSPECIFIED);
        //给头部菜单测量
        mTopView.measure(topWidthMeasureSpec, topHeightMeasureSpec);
        //给主页面内容测量
        // 直接用widthMeasureSpec,heightMeasureSpec使用因为都是math_parent,宽高一样
        mContentView.measure(widthMeasureSpec, heightMeasureSpec);

        //针对自己
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measuredHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    //布局
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //头部菜单
        int lvLeft = 0;
        int lvTop = -mTopView.getMeasuredHeight();
        int lvRight = mTopView.getMeasuredWidth();
        int lvBottom = 0;

        mTopView.layout(lvLeft, lvTop, lvRight, lvBottom);
        //主页面内容
        int cvLeft = 0;
        int cvBottom = mContentView.getMeasuredHeight();
        int cvRight = mContentView.getMeasuredWidth();
        int cvTop = 0;
        mContentView.layout(cvLeft, cvTop, cvRight, cvBottom);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();

                if (Math.abs(moveY - mDownY) > Math.abs(moveX - mDownX)) {
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

    //触摸监听
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float mMoveX = event.getX();
                float mMoveY = event.getY();
                int diffY = (int) (mDownY - mMoveY + 0.5);

                // 防止划出屏幕
                if (getScrollY() + diffY < -mTopView.getHeight()) {
                    //防止向上划出屏幕
                    scrollTo(0, -mTopView.getMeasuredHeight());
                } else if (getScrollY() + diffY > 0) {
                    //防止向下划出屏幕
                    scrollTo(0, 0);
                } else {
                    //当diffY为负数时，屏幕向上移动，头部菜单显示出来，为正数则相反
                    scrollBy(0, diffY);
                }

                mDownX = mMoveX;
                mDownY = mMoveY;

                break;
            case MotionEvent.ACTION_UP:
                switchMenu(getScrollY() < -mTopView.getHeight() / 2);
                break;

            default:
                break;
        }
        return true;
    }


    /**
     * 该变布局
     *
     * @param isTop  true --显示头部菜单
     */
    private void switchMenu(boolean isTop) {

        if (mListener!=null){
            mListener.onToggleChange(this,isTop);
        }
        this.mUpMenuOpen=isTop;

        if (isTop) {
            // 开始移动时的X，Y坐标
            int startX = getScrollX();
            int startY = getScrollY();
            // XY的偏移量
            int dx = 0 - startX ;
            int dy =-mTopView.getHeight() - startY;
            int duration = Math.abs(dy) * 10;
            if (duration > 600) {
                duration = 600;
            }
            scroller.startScroll(startX, startY, dx, dy, duration);
        } else {
            // 开始移动时的X，Y坐标
            int startX = getScrollX();
            int startY = getScrollY();
            // XY的偏移量
            int dx = 0 - startX;
            int dy = 0 - startY;
            int duration = Math.abs(dy) * 10;
            if (duration > 600) {
                duration = 600;
            }
            scroller.startScroll(startX, startY, dx, dy, duration);
        }
        invalidate();
        // 触发ui绘制 --> draw() --> dispatchDraw()--> drawChild --> computeScroll()
    }

    /**
     * 处理scroller的信息，在一个时间段处理一定的偏移量
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.computeScrollOffset()) {
            scrollTo(0,scroller.getCurrY());
        }
        invalidate();
    }

    /**
     * 获取头部菜单状态
     * @return
     */
    public boolean getMenuState(){
        return mUpMenuOpen;
    }

    /**
     * 打开头部菜单
     */
    public void openMenu(){
        switchMenu(true);
    }

    /**
     * 关闭头部菜单
     */

    public void closeMenu(){
        switchMenu(false);
    }

    /**
     * 监听头部菜单状态
     * @param listener  监听者
     */
    public void setOnToggleListener(OnToggleListener listener){
        this.mListener=listener;
    }

    public interface  OnToggleListener{
        void onToggleChange(SlidingUpMenu view,boolean isOpen);
    }

}
