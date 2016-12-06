package recitewords.apj.com.recitewords.fragment;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import net.youmi.android.listener.Interface_ActivityListener;
import net.youmi.android.normal.banner.BannerManager;
import net.youmi.android.normal.banner.BannerViewListener;
import net.youmi.android.offers.OffersManager;

import java.util.ArrayList;
import java.util.List;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.adapter.MyViewPagerAdapter;

/**
 * Created by CGT on 2016/11/22.
 * <p/>
 * 滑动页面Fragment
 */
public class SlidingFragment extends BaseFragment {

    private class holder {
        private ViewPager menu_viewPager;//用于显示4个子菜单界面的ViewPager
        private View view_settings, view_themes, view_library, view_statistics; //4个ViewPager里面的View
        private ImageView img_set, img_the, img_lib, img_sta, cursor; //4张图片，和一张显示游标的图片
        private TextView text_set, text_the, text_lib, text_sta;// 4个TextView菜单的名称
        private LinearLayout ll_setting, ll_theme, ll_library, ll_statistics;  //菜单导航栏
        private LinearLayout ll_top;  //导航栏父控件
    }

    private static final String TAG = "SlidingFragment";

    public Context mContext;
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度

    private MainActivity mainActivity;
    private List<View> viewList;//用于存放4个View的集合
    private holder holder;
    private SlidingUpPanelLayout.PanelState panelState;  //滑动页面状态
    private int clickSlidinMenugSum = 1;  //一个菜单的点击次数
    private int oldClickIndex = -1;  //上一次点击的菜单位置
    private int NavigateHeight = 0;  //导航栏高度


    public SlidingFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public View initView() {
        OffersManager.getInstance(mActivity).onAppExit();//广告积分墙资源释放
        final View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_sliding, null);
        holder = new holder();
        mainActivity = (MainActivity) mActivity;

        holder.menu_viewPager = findViewByIds(view, R.id.menu_viewpager);
        holder.img_set = findViewByIds(view, R.id.img_set);
        holder.img_the = findViewByIds(view, R.id.img_the);
        holder.img_lib = findViewByIds(view, R.id.img_lib);
        holder.img_sta = findViewByIds(view, R.id.img_sta);
        holder.text_set = findViewByIds(view, R.id.text_set);
        holder.text_the = findViewByIds(view, R.id.text_the);
        holder.text_lib = findViewByIds(view, R.id.text_lib);
        holder.text_sta = findViewByIds(view, R.id.text_sta);
        holder.cursor = findViewByIds(view, R.id.cursor);
        holder.ll_setting = findViewByIds(view, R.id.ll_setting);
        holder.ll_theme = findViewByIds(view, R.id.ll_theme);
        holder.ll_library = findViewByIds(view, R.id.ll_library);
        holder.ll_statistics = findViewByIds(view, R.id.ll_statistics);


        //获取菜单导航栏高度，设置用户向上的最大高度
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                view.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        holder.ll_top = findViewByIds(view, R.id.ll_top);
                        NavigateHeight = holder.ll_top.getHeight();
//                        Log.e(TAG, "大小：" + NavigateHeight);
                        mainActivity.setNavigateHeight(NavigateHeight);
                    }
                }, 300);
                view.getViewTreeObserver()
                        .removeOnGlobalLayoutListener(this);  //移除监听
            }
        });

        InitImageView();
        InitViewPager();
        return view;
    }

    /**
     * 初始化游标图片
     */
    private void InitImageView() {

        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.cursor).getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        holder.cursor.setImageMatrix(matrix);// 设置动画初始位置
    }

    /**
     * 设置ViewPager
     */
    private void InitViewPager() {
        viewList = new ArrayList<View>();
        LayoutInflater inflater = mActivity.getLayoutInflater();
        holder.view_settings = inflater.inflate(R.layout.viewpager_settings, null);
        holder.view_themes = inflater.inflate(R.layout.viewpager_themes, null);
        holder.view_library = inflater.inflate(R.layout.viewpager_library, null);
        holder.view_statistics = inflater.inflate(R.layout.viewpager_statistics, null);
        //设置4个view广告
        setAdv();
        //把4个View都放在集合里
        viewList.add(holder.view_settings);
        viewList.add(holder.view_themes);
        viewList.add(holder.view_library);
        viewList.add(holder.view_statistics);
        MyViewPagerAdapter myViewPager = new MyViewPagerAdapter(mActivity, viewList);
        holder.menu_viewPager.setAdapter(myViewPager);
        holder.menu_viewPager.setCurrentItem(0);
        //holder.text_set.setTextColor(Color.CYAN);
        holder.menu_viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    @Override
    public void initEvent() {
//        holder.img_set.setOnClickListener(new MyOnClickListener(0));
//        holder.img_the.setOnClickListener(new MyOnClickListener(1));
//        holder.img_lib.setOnClickListener(new MyOnClickListener(2));
//        holder.img_sta.setOnClickListener(new MyOnClickListener(3));
//
//        holder.text_set.setOnClickListener(new MyOnClickListener(0));
//        holder.text_the.setOnClickListener(new MyOnClickListener(1));
//        holder.text_lib.setOnClickListener(new MyOnClickListener(2));
//        holder.text_sta.setOnClickListener(new MyOnClickListener(3));

        holder.ll_setting.setOnClickListener(new MyOnClickListener(0));
        holder.ll_theme.setOnClickListener(new MyOnClickListener(1));
        holder.ll_library.setOnClickListener(new MyOnClickListener(2));
        holder.ll_statistics.setOnClickListener(new MyOnClickListener(3));
    }

    /**
     * 头标点击监听
     */
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            panelState = mainActivity.holder.mLayout.getPanelState();
            if (panelState == SlidingUpPanelLayout.PanelState.COLLAPSED ||
                    panelState == SlidingUpPanelLayout.PanelState.ANCHORED) {
                mainActivity.holder.mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                //当用户点击返回首页后，再次点击导航栏，进入滑动菜单页面，
                // 返回时和再次进去同一个页面就需要设置字体颜色
                switch (v.getId()) {
                    case R.id.ll_setting:
                        holder.text_set.setTextColor(Color.CYAN);
                        break;
                    case R.id.ll_theme:
                        holder.text_the.setTextColor(Color.CYAN);
                        break;
                    case R.id.ll_library:
                        holder.text_lib.setTextColor(Color.CYAN);
                        break;
                    case R.id.ll_statistics:
                        holder.text_sta.setTextColor(Color.CYAN);
                        break;
                }
            } else if (panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                if (oldClickIndex == index) {
                    mainActivity.holder.mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    oldClickIndex = -1;
                    clickSlidinMenugSum = 1;
                }
            }
            holder.menu_viewPager.setCurrentItem(index);
            oldClickIndex = index;
            clickSlidinMenugSum++;
        }


    }

    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            TextColorChange(position);
            cursor_move(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }


    /**
     * 文字颜色改变的方法
     */
    public void TextColorChange(int position) {
        Log.e(TAG, "position:  " + position);
        switch (position) {
            case 0:
                holder.text_set.setTextColor(Color.CYAN);
                holder.text_the.setTextColor(Color.WHITE);
                holder.text_lib.setTextColor(Color.WHITE);
                holder.text_sta.setTextColor(Color.WHITE);
                break;
            case 1:
                holder.text_set.setTextColor(Color.WHITE);
                holder.text_the.setTextColor(Color.CYAN);
                holder.text_lib.setTextColor(Color.WHITE);
                holder.text_sta.setTextColor(Color.WHITE);
                break;
            case 2:
                holder.text_set.setTextColor(Color.WHITE);
                holder.text_the.setTextColor(Color.WHITE);
                holder.text_lib.setTextColor(Color.CYAN);
                holder.text_sta.setTextColor(Color.WHITE);
                break;
            case 3:
                holder.text_set.setTextColor(Color.WHITE);
                holder.text_the.setTextColor(Color.WHITE);
                holder.text_lib.setTextColor(Color.WHITE);
                holder.text_sta.setTextColor(Color.CYAN);
                break;
        }
    }

    /**
     * 设置文字全部为白色
     */
    public void setTextWhite() {
        holder.text_set.setTextColor(Color.WHITE);
        holder.text_the.setTextColor(Color.WHITE);
        holder.text_lib.setTextColor(Color.WHITE);
        holder.text_sta.setTextColor(Color.WHITE);
    }

    /**
     * 游标移动的方法
     */
    public void cursor_move(int position) {
        int one = offset * 2 + bmpW;   // 1 个页卡的偏移量
        int two = one * 2;              // 2 个页卡的偏移量
        int there = one * 3;            // 3 个页卡的偏移量

        Animation animation = null;
        switch (position) {
            case 0:
                if (currIndex == 1) {
                    animation = new TranslateAnimation(one, 0, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, 0, 0, 0);
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(there, 0, 0, 0);
                }
                break;
            case 1:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, one, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, one, 0, 0);
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(there, one, 0, 0);
                }
                break;
            case 2:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, two, 0, 0);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(one, two, 0, 0);
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(there, two, 0, 0);
                }
                break;
            case 3:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, there, 0, 0);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(one, there, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, there, 0, 0);
                }
                break;
        }
        currIndex = position;
        oldClickIndex = position;  //更新当前显示的index
        animation.setFillAfter(true);// True:图片停在动画结束位置
        animation.setDuration(300);
        holder.cursor.startAnimation(animation);
    }

    /**
     * 获取菜单导航栏高度
     *
     * @return
     */
    public int getNavigateHeight() {
        return NavigateHeight;
    }

    //设置广告条
    private void setAdv(){
        // 获取广告条
        View bannerView = BannerManager.getInstance(mActivity)
                .getBannerView(new BannerViewListener() {
                    @Override
                    public void onRequestSuccess() {

                    }

                    @Override
                    public void onSwitchBanner() {

                    }

                    @Override
                    public void onRequestFailed() {

                    }
                });
        LinearLayout ll_banner_settings = findViewByIds(holder.view_settings, R.id.ll_banner_settings);
        TextView app_recommend = findViewByIds(holder.view_statistics, R.id.app_recommend);
        app_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OffersManager.getInstance(mActivity).showOffersWall();
            }
        });

        // 将广告条加入到布局中
      //  ll_banner_settings.addView(bannerView);
    }
}
