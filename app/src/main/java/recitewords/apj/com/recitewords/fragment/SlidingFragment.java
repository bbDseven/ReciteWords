package recitewords.apj.com.recitewords.fragment;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import recitewords.apj.com.recitewords.util.PrefUtils;

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
        private LinearLayout sliding_layout_ll; //根布局用于设置背景颜色
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
    private RadioButton rb_gold;    //主题页面的金色按钮
    private RadioButton rb_night;
    private RadioButton rb_ocean;
    private RadioButton rb_green;   //主题页面的绿色按钮
    private RadioButton rb_pink;
    private RadioButton rb_purple;  //主题页面的紫色按钮
    private SharedPreferences sp;

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
        holder.sliding_layout_ll = findViewByIds(view, R.id.sliding_layout_ll);

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

        //通过用户选择的主题，重新进入应用时将背景设置为相应的颜色
        String theme = PrefUtils.getThemes(sp, "themes", "gold");
        if ("gold".equals(theme)){
            holder.sliding_layout_ll.setBackgroundResource(R.color.Golden_Eye);
        }else if ("night".equals(theme)){
            holder.sliding_layout_ll.setBackgroundResource(R.color.Night_Hawks);
        }else if ("ocean".equals(theme)){
            holder.sliding_layout_ll.setBackgroundResource(R.color.Ocean_Deep);
        }else if ("green".equals(theme)){
            holder.sliding_layout_ll.setBackgroundResource(R.color.Green_Peace);
        }else if ("pink".equals(theme)){
            holder.sliding_layout_ll.setBackgroundResource(R.color.Pinky_Girl);
        }else if ("purple".equals(theme)){
            holder.sliding_layout_ll.setBackgroundResource(R.color.Purple_Viola);
        }
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
        setAdv();        //设置广告
        setThemes();    //设置主题
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
//                        holder.text_set.setTextColor(Color.CYAN);
                        setTextColor(holder.text_set);
                        break;
                    case R.id.ll_theme:
//                        holder.text_the.setTextColor(Color.CYAN);
                        setTextColor(holder.text_the);
                        break;
                    case R.id.ll_library:
//                        holder.text_lib.setTextColor(Color.CYAN);
                        setTextColor(holder.text_lib);
                        break;
                    case R.id.ll_statistics:
//                        holder.text_sta.setTextColor(Color.CYAN);
                        setTextColor(holder.text_sta);
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
                setTextColor(holder.text_set);
                holder.text_the.setTextColor(Color.WHITE);
                holder.text_lib.setTextColor(Color.WHITE);
                holder.text_sta.setTextColor(Color.WHITE);
                break;
            case 1:
                holder.text_set.setTextColor(Color.WHITE);
                setTextColor(holder.text_the);
                holder.text_lib.setTextColor(Color.WHITE);
                holder.text_sta.setTextColor(Color.WHITE);
                break;
            case 2:
                holder.text_set.setTextColor(Color.WHITE);
                holder.text_the.setTextColor(Color.WHITE);
                setTextColor(holder.text_lib);
                holder.text_sta.setTextColor(Color.WHITE);
                break;
            case 3:
                holder.text_set.setTextColor(Color.WHITE);
                holder.text_the.setTextColor(Color.WHITE);
                holder.text_lib.setTextColor(Color.WHITE);
                setTextColor(holder.text_sta);
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
        ll_banner_settings.addView(bannerView);
    }

    //设置主题页面的按钮事件
    private void setThemes(){
        RadioGroup rg = findViewByIds(holder.view_themes, R.id.themes_rg);
        rb_gold = findViewByIds(holder.view_themes, R.id.themes_rb_gold);
        rb_night = findViewByIds(holder.view_themes, R.id.themes_rb_night);
        rb_ocean = findViewByIds(holder.view_themes, R.id.themes_rb_ocean);
        rb_green = findViewByIds(holder.view_themes, R.id.themes_rb_green);
        rb_pink = findViewByIds(holder.view_themes, R.id.themes_rb_pink);
        rb_purple = findViewByIds(holder.view_themes, R.id.themes_rb_purple);
        final ImageView iv_gold = findViewByIds(holder.view_themes, R.id.themes_iv_gold);
        final ImageView iv_night = findViewByIds(holder.view_themes, R.id.themes_iv_night);
        final ImageView iv_ocean = findViewByIds(holder.view_themes, R.id.themes_iv_ocean);
        final ImageView iv_green = findViewByIds(holder.view_themes, R.id.themes_iv_green);
        final ImageView iv_pink = findViewByIds(holder.view_themes, R.id.themes_iv_pink);
        final ImageView iv_purple = findViewByIds(holder.view_themes, R.id.themes_iv_purple);

        sp = PrefUtils.getPref(mActivity);
        //通过用户选择的主题，重新进入应用时将主题的默认选中设置为相应的
        String theme = PrefUtils.getThemes(sp, "themes", "gold");
        if ("gold".equals(theme)){
            rb_gold.setChecked(true);
            iv_gold.setVisibility(View.VISIBLE);
        }else if ("night".equals(theme)){
            rb_night.setChecked(true);
            iv_night.setVisibility(View.VISIBLE);
        }else if ("ocean".equals(theme)){
            rb_ocean.setChecked(true);
            iv_ocean.setVisibility(View.VISIBLE);
        }else if ("green".equals(theme)){
            rb_green.setChecked(true);
            iv_green.setVisibility(View.VISIBLE);
        }else if ("pink".equals(theme)){
            rb_pink.setChecked(true);
            iv_pink.setVisibility(View.VISIBLE);
        }else if ("purple".equals(theme)){
            rb_purple.setChecked(true);
            iv_purple.setVisibility(View.VISIBLE);
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.themes_rb_gold:
                        holder.sliding_layout_ll.setBackgroundResource(R.color.Golden_Eye);
                        iv_gold.setVisibility(View.VISIBLE);
                        iv_night.setVisibility(View.GONE);
                        iv_ocean.setVisibility(View.GONE);
                        iv_green.setVisibility(View.GONE);
                        iv_pink.setVisibility(View.GONE);
                        iv_purple.setVisibility(View.GONE);
                        holder.text_the.setTextColor(mActivity.getResources().getColor(R.color.gold));
                        PrefUtils.setThemes(sp, "themes", "gold");
                        break;
                    case R.id.themes_rb_night:
                        holder.sliding_layout_ll.setBackgroundResource(R.color.Night_Hawks);
                        iv_gold.setVisibility(View.GONE);
                        iv_night.setVisibility(View.VISIBLE);
                        iv_ocean.setVisibility(View.GONE);
                        iv_green.setVisibility(View.GONE);
                        iv_pink.setVisibility(View.GONE);
                        iv_purple.setVisibility(View.GONE);
                        holder.text_the.setTextColor(mActivity.getResources().getColor(R.color.night));
                        PrefUtils.setThemes(sp, "themes", "night");
                        break;
                    case R.id.themes_rb_ocean:
                        holder.sliding_layout_ll.setBackgroundResource(R.color.Ocean_Deep);
                        iv_gold.setVisibility(View.GONE);
                        iv_night.setVisibility(View.GONE);
                        iv_ocean.setVisibility(View.VISIBLE);
                        iv_green.setVisibility(View.GONE);
                        iv_pink.setVisibility(View.GONE);
                        iv_purple.setVisibility(View.GONE);
                        holder.text_the.setTextColor(mActivity.getResources().getColor(R.color.ocean));
                        PrefUtils.setThemes(sp, "themes", "ocean");
                        break;
                    case R.id.themes_rb_green:
                        holder.sliding_layout_ll.setBackgroundResource(R.color.Green_Peace);
                        iv_gold.setVisibility(View.GONE);
                        iv_night.setVisibility(View.GONE);
                        iv_ocean.setVisibility(View.GONE);
                        iv_green.setVisibility(View.VISIBLE);
                        iv_pink.setVisibility(View.GONE);
                        iv_purple.setVisibility(View.GONE);
                        holder.text_the.setTextColor(mActivity.getResources().getColor(R.color.green));
                        PrefUtils.setThemes(sp, "themes", "green");
                        break;
                    case R.id.themes_rb_pink:
                        holder.sliding_layout_ll.setBackgroundResource(R.color.Pinky_Girl);
                        iv_gold.setVisibility(View.GONE);
                        iv_night.setVisibility(View.GONE);
                        iv_ocean.setVisibility(View.GONE);
                        iv_green.setVisibility(View.GONE);
                        iv_pink.setVisibility(View.VISIBLE);
                        iv_purple.setVisibility(View.GONE);
                        holder.text_the.setTextColor(mActivity.getResources().getColor(R.color.pink));
                        PrefUtils.setThemes(sp, "themes", "pink");
                        break;
                    case R.id.themes_rb_purple:
                        holder.sliding_layout_ll.setBackgroundResource(R.color.Purple_Viola);
                        iv_gold.setVisibility(View.GONE);
                        iv_night.setVisibility(View.GONE);
                        iv_ocean.setVisibility(View.GONE);
                        iv_green.setVisibility(View.GONE);
                        iv_pink.setVisibility(View.GONE);
                        iv_purple.setVisibility(View.VISIBLE);
                        holder.text_the.setTextColor(mActivity.getResources().getColor(R.color.purple));
                        PrefUtils.setThemes(sp, "themes", "purple");
                        break;
                }
            }
        });
    }
    //设置主题页的文本颜色
    private void setTextColor(TextView tv){
        if (rb_gold.isChecked()){
            tv.setTextColor(mActivity.getResources().getColor(R.color.gold));
        }else if (rb_night.isChecked()){
            tv.setTextColor(mActivity.getResources().getColor(R.color.night));
        }else if (rb_ocean.isChecked()){
            tv.setTextColor(mActivity.getResources().getColor(R.color.ocean));
        }else if (rb_green.isChecked()){
            tv.setTextColor(mActivity.getResources().getColor(R.color.green));
        }else if (rb_pink.isChecked()){
            tv.setTextColor(mActivity.getResources().getColor(R.color.pink));
        }else if (rb_purple.isChecked()){
            tv.setTextColor(mActivity.getResources().getColor(R.color.purple));
        }else {
            tv.setTextColor(Color.WHITE);
        }
    }
}
