package recitewords.apj.com.recitewords.activity;

import android.app.Fragment;
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

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.bean.Book;
import recitewords.apj.com.recitewords.db.dao.BookDao;
import recitewords.apj.com.recitewords.fragment.TodayLearnFragment;
import recitewords.apj.com.recitewords.fragment.TodayReviewFragment;
import recitewords.apj.com.recitewords.util.DateUtil;


/**
 * Created by Greetty on 2016/12/9.
 * <p/>
 * 今日已学习单词
 */
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
    private int learnSum;  //完成学习的单词总数

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
                FragmentManager fragmentManager = getFragmentManager();
                TodayLearnFragment fragment = (TodayLearnFragment)fragmentManager.
                        findFragmentByTag(TODAYLEARNFRAGMENT);
                learnSum = fragment.getLearnSum();
//                Toast.makeText(this, "分享", Toast.LENGTH_SHORT).show();
                showShare();  //分享
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
                this.fm = getFragmentManager();
                ft = this.fm.beginTransaction();
                ft.replace(R.id.show_today_fl, mTodayLearnFragment, TODAYLEARNFRAGMENT);
                ft.commit();
                break;
            case R.id.show_today_review:
                holder.show_today_learn.setBackgroundResource(R.drawable.textview_themes_shape);
                holder.show_today_review.setBackgroundResource(R.drawable.textview_white_shape);
                holder.show_today_learn.setTextColor(Color.parseColor("#FFFFFF"));
                holder.show_today_review.setTextColor(Color.parseColor("#9D812E"));
                this.fm = getFragmentManager();
                ft = this.fm.beginTransaction();
                ft.replace(R.id.show_today_fl, mTodayReviewFragment, TODAYREVIEWFRAGMENT);
                ft.commit();
                break;
            default:
                break;
        }
    }

    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("毛线单词助你飞");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("今儿我用不背单词轻松hold住"+learnSum+"个单词，表羡慕、~^~、");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("今儿我用不背单词轻松hold住"+learnSum+"个单词，表羡慕、~^~、");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

// 启动分享GUI
        oks.show(this);
    }

}
