package recitewords.apj.com.recitewords.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.bean.Book;
import recitewords.apj.com.recitewords.db.dao.BookDao;
import recitewords.apj.com.recitewords.fragment.TodayLearnFragment;
import recitewords.apj.com.recitewords.fragment.TodayReviewFragment;
import recitewords.apj.com.recitewords.util.DateUtil;

public class ShowTodayLearnActivity extends BaseActivity implements View.OnClickListener {

    private static final String TODAYLEARNFRAGMENT = "today_learn";
    private static final String TODAYREVIEWFRAGMENT = "today_review";

    public class ViewHolder {
        ImageView show_today_share;   //分享
        ImageView show_today_close;  //关闭
        TextView show_today_learn;  //学习
        TextView show_today_review; //复习
        FrameLayout show_today_fl;  //学习复习情况
    }

    private ViewHolder holder;
    private TodayLearnFragment mTodayLearnFragment;
    private TodayReviewFragment mTodayReviewFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_today_learn);

        initView();
        initEvent();
        initDate();
    }

    private void initView() {
        holder = new ViewHolder();
        holder.show_today_share = findViewByIds(R.id.show_today_share);
        holder.show_today_close = findViewByIds(R.id.show_today_close);
        holder.show_today_learn = findViewByIds(R.id.show_today_learn);
        holder.show_today_review = findViewByIds(R.id.show_today_review);
        holder.show_today_fl = findViewByIds(R.id.show_today_fl);

    }

    private void initEvent() {
        holder.show_today_close.setOnClickListener(this);
        holder.show_today_share.setOnClickListener(this);
        holder.show_today_learn.setOnClickListener(this);
        holder.show_today_review.setOnClickListener(this);
    }

    private void initDate() {
        mTodayLearnFragment = new TodayLearnFragment();
        mTodayReviewFragment = new TodayReviewFragment();
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.show_today_fl, mTodayLearnFragment, TODAYLEARNFRAGMENT);
        ft.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_today_share:
                Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show();
                break;
            case R.id.show_today_close:
                //关闭当前窗口
                finish();
                break;
            case R.id.show_today_learn:
                holder.show_today_learn.setBackgroundResource(R.drawable.textview_white_shape);
                holder.show_today_review.setBackgroundResource(R.drawable.textview_themes_shape);
                holder.show_today_learn.setTextColor(Color.parseColor("#9D812E"));
                holder.show_today_review.setTextColor(Color.parseColor("#FFFFFF"));
                fm = getFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.show_today_fl, mTodayLearnFragment, TODAYLEARNFRAGMENT);
                ft.commit();
                break;
            case R.id.show_today_review:
                holder.show_today_learn.setBackgroundResource(R.drawable.textview_themes_shape);
                holder.show_today_review.setBackgroundResource(R.drawable.textview_white_shape);
                holder.show_today_learn.setTextColor(Color.parseColor("#FFFFFF"));
                holder.show_today_review.setTextColor(Color.parseColor("#9D812E"));
                fm = getFragmentManager();
                ft = fm.beginTransaction();
                ft.replace(R.id.show_today_fl, mTodayReviewFragment, TODAYREVIEWFRAGMENT);
                ft.commit();
                break;
            default:
                break;
        }
    }

}
