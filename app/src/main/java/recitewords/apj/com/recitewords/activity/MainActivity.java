package recitewords.apj.com.recitewords.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;

import recitewords.apj.com.recitewords.R;

public class MainActivity extends BaseActivity implements PanelSlideListener, View.OnTouchListener {


    public class ViewHolder {
        private SlidingUpPanelLayout mLayout;
        private LinearLayout ll_main;
    }

    private static final String TAG = "MainActivity";
    private ViewHolder holder;
    private Scroller mScroller;
    private int height = 0;  //底部导航栏的高度
    private float mDownY;    //点击坐标Y轴
    private float mMoveY;  //移动坐标Y轴
    private int diffY;  //偏移量
    public boolean show_state = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        holder = new ViewHolder();
        // 创建scroller
        mScroller = new Scroller(this);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        holder.mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        holder.ll_main = (LinearLayout) findViewById(R.id.ll_main);
    }

    private void initData() {
        holder.mLayout.setPanelHeight(0);

    }

    private void initEvent() {
        holder.mLayout.addPanelSlideListener(this);
        holder.ll_main.setOnTouchListener(this);
        holder.ll_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "点击了主页面");
            }
        });
    }

    //SlidingUpPanelLayout页面滑动addPanelSlideListener的回调方法
    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        holder.mLayout.scrollTo(0, 0);
        height = 0;
    }

    // SlidingUpPanelLayout页面滑动addPanelSlideListener的回调方法
    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

    }

    //setOnTouchListener监听触摸事件的回调方法
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownY = event.getY();
                Log.e(TAG, "点击坐标Y轴" + mDownY);
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveY = event.getY();
//                Log.e(TAG,"位置为："+mMoveY);
                diffY = (int) (mMoveY - mDownY + 0.5);
                mDownY = mMoveY;
                if (diffY <= 0) {
                    Log.e(TAG, "height：" + height);
                    if (height >= 160) {
                        Log.e(TAG, "已经显示了");
                        show_state = true;
                    } else if (height < 160) {
                        height += Math.abs(diffY);
                        if (height <= 160) {
                            holder.mLayout.scrollBy(0, Math.abs(diffY));
                        } else {
                            holder.mLayout.scrollTo(0, 160);
                        }
                    }
                } else {
                    if (height == 0) {
                        if (show_state) {
                            // holder.mLayout.scrollTo(0, 0);
                        }
                        show_state = false;
                        Log.e(TAG, "已经隐藏了");
                    } else if (height > 0) {
                        height -= Math.abs(diffY);
                        if (height >= 0) {
                            holder.mLayout.scrollBy(0, -diffY);
                        } else {
                            holder.mLayout.scrollTo(0, 0);
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                Log.e(TAG, "height：" + height);
                if (height < 70) {
                    //if (show_state){
                    holder.mLayout.scrollTo(0, 0);
                    height = 0;
                    // }
                } else {
                    holder.mLayout.scrollTo(0, 160);
                    height = 160;
                }


                break;
            default:
                break;
        }
        return true;
    }


}
