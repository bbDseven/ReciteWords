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
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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

    private class ViewHolder {
        private ViewPager menu_viewPager;//用于显示4个子菜单界面的ViewPager
        private View view_settings, view_themes, view_library, view_statistics; //4个ViewPager里面的View
        private ImageView img_set, img_the, img_lib, img_sta, cursor; //4张图片，和一张显示游标的图片
        private TextView text_set, text_the, text_lib, text_sta;// 4个TextView菜单的名称
    }

    public Context mContext;
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度

    private MainActivity mainActivity;
    private List<View> viewList;//用于存放4个View的集合
    private ViewHolder viewHolder;
    private OnMyClickListerner mOnMyClickListerner;
    private SlidingUpPanelLayout.PanelState panelState;  //滑动页面状态
    private int clickSlidinMenugSum = 1;  //一个菜单的点击次数
    private int oldClickIndex = -1;  //上一次点击的菜单位置


    public SlidingFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public View initView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_sliding, null);
        viewHolder = new ViewHolder();
        mainActivity = (MainActivity) mActivity;

        viewHolder.menu_viewPager = findViewByIds(view, R.id.menu_viewpager);
        viewHolder.img_set = findViewByIds(view, R.id.img_set);
        viewHolder.img_the = findViewByIds(view, R.id.img_the);
        viewHolder.img_lib = findViewByIds(view, R.id.img_lib);
        viewHolder.img_sta = findViewByIds(view, R.id.img_sta);
        viewHolder.text_set = findViewByIds(view, R.id.text_set);
        viewHolder.text_the = findViewByIds(view, R.id.text_the);
        viewHolder.text_lib = findViewByIds(view, R.id.text_lib);
        viewHolder.text_sta = findViewByIds(view, R.id.text_sta);
        viewHolder.cursor = findViewByIds(view, R.id.cursor);

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
        viewHolder.cursor.setImageMatrix(matrix);// 设置动画初始位置
    }

    /**
     * 设置ViewPager
     */
    private void InitViewPager() {
        viewList = new ArrayList<View>();
        LayoutInflater inflater = mActivity.getLayoutInflater();
        viewHolder.view_settings = inflater.inflate(R.layout.viewpager_settings, null);
        viewHolder.view_themes = inflater.inflate(R.layout.viewpager_themes, null);
        viewHolder.view_library = inflater.inflate(R.layout.viewpager_library, null);
        viewHolder.view_statistics = inflater.inflate(R.layout.viewpager_statistics, null);
        //把4个View都放在集合里
        viewList.add(viewHolder.view_settings);
        viewList.add(viewHolder.view_themes);
        viewList.add(viewHolder.view_library);
        viewList.add(viewHolder.view_statistics);
        MyViewPagerAdapter myViewPager = new MyViewPagerAdapter(mActivity, viewList);
        viewHolder.menu_viewPager.setAdapter(myViewPager);
        viewHolder.menu_viewPager.setCurrentItem(0);
        viewHolder.text_set.setTextColor(Color.CYAN);
        viewHolder.menu_viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    @Override
    public void initEvent() {
        viewHolder.img_set.setOnClickListener(new MyOnClickListener(0));
        viewHolder.img_the.setOnClickListener(new MyOnClickListener(1));
        viewHolder.img_lib.setOnClickListener(new MyOnClickListener(2));
        viewHolder.img_sta.setOnClickListener(new MyOnClickListener(3));

        viewHolder.text_set.setOnClickListener(new MyOnClickListener(0));
        viewHolder.text_the.setOnClickListener(new MyOnClickListener(1));
        viewHolder.text_lib.setOnClickListener(new MyOnClickListener(2));
        viewHolder.text_sta.setOnClickListener(new MyOnClickListener(3));
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
            //监听点击导航菜单
            // mOnMyClickListerner.onclick(index);

            panelState = mainActivity.holder.mLayout.getPanelState();
            Log.e("ha", "点击了第：" + index + "   状态为：" + panelState);
            int currentItem = viewHolder.menu_viewPager.getVisibility();
            Log.e("ha", "currentItem：" + currentItem);
            if (panelState == SlidingUpPanelLayout.PanelState.COLLAPSED) {
                mainActivity.holder.mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
            } else if (panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                if (oldClickIndex == index) {
//                    clickSlidinMenugSum++;
//                    if(clickSlidinMenugSum==2){
                    mainActivity.holder.mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    oldClickIndex = -1;
//                    }
                }
            }
            viewHolder.menu_viewPager.setCurrentItem(index);
            oldClickIndex = index;
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
        switch (position) {
            case 0:
                viewHolder.text_set.setTextColor(Color.CYAN);
                viewHolder.text_the.setTextColor(Color.WHITE);
                viewHolder.text_lib.setTextColor(Color.WHITE);
                viewHolder.text_sta.setTextColor(Color.WHITE);
                break;
            case 1:
                viewHolder.text_set.setTextColor(Color.WHITE);
                viewHolder.text_the.setTextColor(Color.CYAN);
                viewHolder.text_lib.setTextColor(Color.WHITE);
                viewHolder.text_sta.setTextColor(Color.WHITE);
                break;
            case 2:
                viewHolder.text_set.setTextColor(Color.WHITE);
                viewHolder.text_the.setTextColor(Color.WHITE);
                viewHolder.text_lib.setTextColor(Color.CYAN);
                viewHolder.text_sta.setTextColor(Color.WHITE);
                break;
            case 3:
                viewHolder.text_set.setTextColor(Color.WHITE);
                viewHolder.text_the.setTextColor(Color.WHITE);
                viewHolder.text_lib.setTextColor(Color.WHITE);
                viewHolder.text_sta.setTextColor(Color.CYAN);
                break;
        }
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
        animation.setFillAfter(true);// True:图片停在动画结束位置
        animation.setDuration(300);
        viewHolder.cursor.startAnimation(animation);
    }

    /**
     * 点击导航菜单回调方法发
     *
     * @param onMyClickListerner 监听者
     * @return 点击位置
     */
    public void setOnMyClickListerner(OnMyClickListerner onMyClickListerner) {
        this.mOnMyClickListerner = onMyClickListerner;
    }

    public interface OnMyClickListerner {
        void onclick(int index);
    }

}
