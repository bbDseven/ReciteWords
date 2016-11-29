package recitewords.apj.com.recitewords.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.fragment.MainFragment;
import recitewords.apj.com.recitewords.fragment.SlidingFragment;

public class MainActivity extends BaseActivity implements PanelSlideListener, View.OnTouchListener {

    public class ViewHolder {
        public SlidingUpPanelLayout mLayout;
        private FrameLayout fl_main;
        private FrameLayout fl_sliding;
    }

    private static final String FRAGMENT_MAIN = "fragment_main";  //主页面Fragment标识,Tag
    private static final String FRAGMENT_SLIDING = "fragment_sliding";  //滑动页面Fragment标识

    public static int userID=0;  //userID,用户的登陆ID
    public static String dbName="ReciteWords_"+ userID+".db";   //数据库名字

    private static final String TAG = "MainActivity";
    public ViewHolder holder;
    private Scroller mScroller;
    private int height = 0;  //底部导航栏的高度
    private float mDownY;    //点击坐标Y轴
    private float mMoveY;  //移动坐标Y轴
    private int diffY;  //偏移量
    private boolean show_state = false;  //底部导航显示状态
    private MainFragment mainFragment;   //主页面Fragment
    private SlidingFragment slidingFragment;  //滑动页面Fragment
    private FragmentManager fm;
    private int NavigateHeight = 160;   //导航菜单栏的高度


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mScroller = new Scroller(this);    // 创建scroller
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        holder = new ViewHolder();
        holder.mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        holder.fl_main = (FrameLayout) findViewById(R.id.fl_main);
        holder.fl_sliding = (FrameLayout) findViewById(R.id.fl_sliding);
    }

    private void initData() {
        holder.mLayout.setPanelHeight(0);
        mainFragment = new MainFragment(this);
        slidingFragment = new SlidingFragment(this);
        initFragment();
    }

    private void initEvent() {
        holder.mLayout.addPanelSlideListener(this);
        holder.fl_main.setOnTouchListener(this);
    }

    /**
     * 初始化Fragment布局
     */
    private void initFragment() {
        fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();  // 开启事务
        transaction.replace(R.id.fl_main, mainFragment, FRAGMENT_MAIN);
        transaction.replace(R.id.fl_sliding, slidingFragment, FRAGMENT_SLIDING);
        transaction.commit(); //提交事务
    }

    //SlidingUpPanelLayout页面滑动addPanelSlideListener的回调方法
    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        holder.mLayout.scrollTo(0, 0);
        height = 0;
    }

    // SlidingUpPanelLayout页面滑动addPanelSlideListener的回调方法
    //状态改变
    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
            Log.e("haha","mainActivity状态"+newState);
        //状态由EXPANDED变成COLLAPSED，设置字体颜色为白色
        if (newState== SlidingUpPanelLayout.PanelState.COLLAPSED){
            FragmentManager fragmentManager = getFragmentManager();
            SlidingFragment fragment = (SlidingFragment) fragmentManager.
                    findFragmentByTag(FRAGMENT_SLIDING);
            fragment.setTextWhite();
        }
    }

    //setOnTouchListener监听触摸事件的回调方法
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveY = event.getY();
                diffY = (int) (mMoveY - mDownY + 0.5);
                mDownY = mMoveY;
                if (diffY <= 0) {
                    if (height >= NavigateHeight) {
                        show_state = true;
                    } else if (height < NavigateHeight) {
                        height += Math.abs(diffY);   //计算总偏移量
                        if (height <= NavigateHeight) {  //总偏移量小于导航栏高度
                            holder.mLayout.scrollBy(0, Math.abs(diffY));
                        } else {
                            holder.mLayout.scrollTo(0, NavigateHeight);
                        }
                    }
                } else {
                    if (height == 0) {
                        show_state = false;
                    } else if (height > 0) {
                        height -= Math.abs(diffY);   //计算总偏移量
                        if (height >= 0) {
                            holder.mLayout.scrollBy(0, -diffY);
                        } else {
                            holder.mLayout.scrollTo(0, 0);
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
               // Log.e(TAG,"height: "+height);
                if (height < (NavigateHeight / 2 - 10) && height > 0) {
                    holder.mLayout.scrollTo(0, 0);
                    //mScroller.startScroll(0,0,0,160,1000);
                    height = 0;
                    show_state = false;   //改变显示状态
                } else if (height < NavigateHeight && height > 0) {
                    holder.mLayout.scrollTo(0, NavigateHeight);
                    height = NavigateHeight;
                    show_state = true;
                }

                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 底部导航菜单栏显示状态
     *
     * @return
     */
    public boolean getNavigateShowState() {
        return show_state;
    }

    /**
     * 设置底部导航菜单栏显示状态
     */
    public void setNavigateShowState(boolean state) {
        show_state = state;
    }

    /**
     * 隐藏导航菜单栏
     */
    public void setNavigateHide() {
        holder.mLayout.scrollTo(0, 0);
        height=0;
    }

    /**
     * 显示导航菜单栏
     */
    public void setNavigateShow() {
        holder.mLayout.scrollTo(0, NavigateHeight);
        height=NavigateHeight;
    }

    /**
     * 设置导航菜单栏高度
     */
    public void setNavigateHeight(int height){
        NavigateHeight=height;
    }


}
