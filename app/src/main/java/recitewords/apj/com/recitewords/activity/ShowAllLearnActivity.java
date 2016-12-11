package recitewords.apj.com.recitewords.activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.fragment.AllGraspFragment;
import recitewords.apj.com.recitewords.fragment.AllReviewingFragment;



/**
 * Created by Greetty on 2016/12/10.
 * <p/>
 * 全部已学习单词
 */
public class ShowAllLearnActivity extends BaseActivity implements View.OnClickListener {

    private static final String ALLREVIEWINGFRAGMRNT = "all_reviewing";
    private static final String ALLGRASPFRAGMRNT = "all_grasp";

    public class ViewHolder {
        ImageView show_all_info;   //分享
        ImageView show_all_close;  //关闭
        TextView show_all_learn;  //学习
        TextView show_all_review; //复习
        FrameLayout show_all_fl;  //学习复习情况
    }

    private ViewHolder holder;
    private AllReviewingFragment mAllReviewingFragment;
    private AllGraspFragment mAllGraspFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_learn);

        initView();
        initEvent();
        initDate();
    }

    private void initView() {
        holder = new ViewHolder();
        holder.show_all_info = findViewByIds(R.id.show_all_info);
        holder.show_all_close = findViewByIds(R.id.show_all_close);
        holder.show_all_learn = findViewByIds(R.id.show_all_learn);
        holder.show_all_review = findViewByIds(R.id.show_all_review);
        holder.show_all_fl = findViewByIds(R.id.show_all_fl);

    }

    private void initEvent() {
        holder.show_all_close.setOnClickListener(this);
        holder.show_all_info.setOnClickListener(this);
        holder.show_all_learn.setOnClickListener(this);
        holder.show_all_review.setOnClickListener(this);
    }

    private void initDate() {
        mAllReviewingFragment = new AllReviewingFragment();
        mAllGraspFragment = new AllGraspFragment();
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.show_all_fl, mAllReviewingFragment, ALLREVIEWINGFRAGMRNT);
        ft.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_all_info:
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setTitle("温馨提示：");
//                builder.setMessage("复习中:已学习单完成复习\n已掌握:已完全掌握，" +
//                        "不用再复习\n\n1.点击单词查看词义\n2.已掌握的单词如果有遗忘，可以向左滑动单词后选择“重学”");
//                builder.setPositiveButton("臣妾知道啦",null);
//                AlertDialog alertDialog = builder.create();
//                alertDialog.show();
                startActivity(new Intent(this,TestActivity.class));
                break;
            case R.id.show_all_close:
                //关闭当前窗口
                finish();
                break;
            case R.id.show_all_learn:
                holder.show_all_learn.setBackgroundResource(R.drawable.textview_white_shape);
                holder.show_all_review.setBackgroundResource(R.drawable.textview_themes_shape);
                holder.show_all_learn.setTextColor(Color.parseColor("#9D812E"));
                holder.show_all_review.setTextColor(Color.parseColor("#FFFFFF"));
                this.fm = getFragmentManager();
                ft = this.fm.beginTransaction();
                ft.replace(R.id.show_all_fl, mAllReviewingFragment, ALLREVIEWINGFRAGMRNT);
                ft.commit();
                break;
            case R.id.show_all_review:
                holder.show_all_learn.setBackgroundResource(R.drawable.textview_themes_shape);
                holder.show_all_review.setBackgroundResource(R.drawable.textview_white_shape);
                holder.show_all_learn.setTextColor(Color.parseColor("#FFFFFF"));
                holder.show_all_review.setTextColor(Color.parseColor("#9D812E"));
                this.fm = getFragmentManager();
                ft = this.fm.beginTransaction();
                ft.replace(R.id.show_all_fl, mAllGraspFragment, ALLGRASPFRAGMRNT);
                ft.commit();
                break;
            default:
                break;
        }
    }
}
