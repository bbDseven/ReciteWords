package recitewords.apj.com.recitewords.activity;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.toolutil.MyViewPager;

/**
 * 四个子菜单界面
 * Created by Administrator on 2016/11/22 0022.
 * 作者：陈金振
 */
public class MenuActivity extends BaseActivity {

    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度

    private List<View> viewList;//用于存放4个View的集合

    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        init_view();            // 初始化各个控件
        InitImageView();        //初始化游标图片
        InitViewPager();        //设置ViewPager
        initListener();         //菜单栏图片，和文字监听事件
    }

    private class ViewHolder{
        private ViewPager menu_viewPager;//用于显示4个子菜单界面的ViewPager
        private View view_settings, view_themes, view_library, view_statistics; //4个ViewPager里面的View
        private ImageView img_set, img_the, img_lib, img_sta, cursor; //4张图片，和一张显示游标的图片
        private TextView text_set, text_the, text_lib, text_sta;// 4个TextView菜单的名称
    }

    private void init_view(){
        viewHolder = new ViewHolder();
        viewHolder.menu_viewPager = findViewByIds(R.id.menu_viewpager);
        viewHolder.img_set = findViewByIds(R.id.img_set);
        viewHolder.img_the = findViewByIds(R.id.img_the);
        viewHolder.img_lib = findViewByIds(R.id.img_lib);
        viewHolder.img_sta = findViewByIds(R.id.img_sta);
        viewHolder.text_set = findViewByIds(R.id.text_set);
        viewHolder.text_the = findViewByIds(R.id.text_the);
        viewHolder.text_lib = findViewByIds(R.id.text_lib);
        viewHolder.text_sta = findViewByIds(R.id.text_sta);
    }

    /**
     * 初始化游标图片
     */
    private void InitImageView() {
        viewHolder.cursor = findViewByIds(R.id.cursor);
        bmpW = BitmapFactory.decodeResource(getResources(), R.drawable.cursor).getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        viewHolder.cursor.setImageMatrix(matrix);// 设置动画初始位置
    }

    /**
     * 设置ViewPager
     * */
    private void InitViewPager() {
        viewList = new ArrayList<View>();
        LayoutInflater inflater = getLayoutInflater();
        viewHolder.view_settings = inflater.inflate(R.layout.activity_settings,null);
        viewHolder.view_themes = inflater.inflate(R.layout.activity_themes,null);
        viewHolder.view_library = inflater.inflate(R.layout.activity_library,null);
        viewHolder.view_statistics = inflater.inflate(R.layout.activity_statistics,null);
        //把4个View都放在集合里
        viewList.add(viewHolder.view_settings);
        viewList.add(viewHolder.view_themes);
        viewList.add(viewHolder.view_library);
        viewList.add(viewHolder.view_statistics);
        MyViewPager myViewPager = new MyViewPager(MenuActivity.this,viewList);
        viewHolder.menu_viewPager.setAdapter(myViewPager);
        viewHolder.menu_viewPager.setCurrentItem(0);
        viewHolder.text_set.setTextColor(Color.CYAN);
        viewHolder.menu_viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    /**
     * 为 菜单栏图片，文字设置监听事件
     */
    private void initListener(){
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
            viewHolder.menu_viewPager.setCurrentItem(index);
        }
    }

    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

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

    /**文字颜色改变的方法
     * */
    public void TextColorChange(int position){
        switch (position){
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

    /**游标移动的方法
     * */
    public void cursor_move(int position){
        int one = offset * 2 + bmpW;   // 1 个页卡的偏移量
        int two = one * 2;              // 2 个页卡的偏移量
        int there = one * 3;            // 3 个页卡的偏移量

        Animation animation = null;
        switch (position){
            case 0:
                if (currIndex == 1){
                    animation = new TranslateAnimation(one, 0, 0, 0);
                }else if (currIndex == 2){
                    animation = new TranslateAnimation(two, 0, 0, 0);
                }else if (currIndex == 3){
                    animation = new TranslateAnimation(there, 0, 0, 0);
                }
                break;
            case 1:
                if (currIndex == 0){
                    animation = new TranslateAnimation(offset, one, 0, 0);
                }else if (currIndex == 2){
                    animation = new TranslateAnimation(two, one, 0, 0);
                }else if(currIndex == 3){
                    animation = new TranslateAnimation(there, one, 0, 0);
                }
                break;
            case 2:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, two, 0, 0);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(one, two, 0, 0);
                }else if (currIndex == 3){
                    animation = new TranslateAnimation(there, two, 0, 0);
                }
                break;
            case 3:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, there, 0, 0);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(one, there, 0, 0);
                }else if (currIndex == 2){
                    animation = new TranslateAnimation(two, there, 0, 0);
                }
                break;
        }
        currIndex = position;
        animation.setFillAfter(true);// True:图片停在动画结束位置
        animation.setDuration(300);
        viewHolder.cursor.startAnimation(animation);
    }

}
