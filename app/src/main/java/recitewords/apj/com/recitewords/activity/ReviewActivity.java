package recitewords.apj.com.recitewords.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.fragment.ExampleSentenceFragment;
import recitewords.apj.com.recitewords.view.CircleProgressView;
import recitewords.apj.com.recitewords.view.SlidingUpMenu;

public class ReviewActivity extends BaseActivity implements View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener{
    //定义好的8张背景图id数组
    private int[] images = new int[]{R.mipmap.haixin_bg_dim_01, R.mipmap.haixin_bg_dim_02,
            R.mipmap.haixin_bg_dim_03,R.mipmap.haixin_bg_dim_04,
            R.mipmap.haixin_bg_dim_05, R.mipmap.haixin_bg_dim_06};
    private int backgroundNum;
    private ViewHolder holder;
    private int num = 4; //定义4秒
    private Message msg;
    private final String FRAGMENT_SENTENCE = "fragment_sentence";

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 3:
                    //设置进度条进度
                    holder.progress.setProgress(25);
                    holder.fl_progress_click.setOnClickListener(ReviewActivity.this);
                    setProgress();
                    break;
                case 2:
                    holder.progress.setProgress(50);
                    setProgress();
                    break;
                case 1:
                    holder.progress.setProgress(75);
                    setProgress();
                    break;
                case 0:
                    holder.progress.setProgress(100);
                    holder.fl_progress_click.setOnClickListener(null);
                    holder.progress.setVisibility(View.GONE);   //隐藏进度条
                    holder.ll_information.setVisibility(View.VISIBLE);  //显示单词信息
                    num = 4;    //进度条显示完num重新设置回4秒
                    break;
            }
            return false;
        }
    });




    private class ViewHolder{
        RelativeLayout rl_review;   //复习页面根布局
        SlidingUpMenu review_sliding;   //sliding
        LinearLayout ll_information;    //展示单词信息布局
        LinearLayout ll_abcd;   //展示单词选项布局
        LinearLayout ll_star;   //学习页星星的布局
        CircleProgressView progress; //圆形进度条
        FrameLayout fl_progress_click;  //圆形进度条的点击
        LinearLayout ll_choice;     //认识的父布局
        LinearLayout ll_incognizance;   //看例句的父布局
        TextView tv_know;   //认识
        TextView tv_dim;    //模糊
        TextView tv_unknow;     //不认识
        TextView tv_incognizance_next;  //下一个
        TextView tv_incognizance_example;  //看例句
        FrameLayout fl_example;  //例句的根布局
        LinearLayout ll_show_word;  //单词音标的根布局
        TextView tv_back;   //返回按钮
        TextView tv_spell;  //拼写按钮
        TextView tv_delete; //删除按钮
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        init_view();
        init_event();
        init_data();
    }

    private void init_view(){
        holder = new ViewHolder();
        holder.rl_review = findViewByIds(R.id.activity_review);
        holder.review_sliding = findViewByIds(R.id.review_sliding);
        holder.ll_information = findViewByIds(R.id.ll_information);
        holder.ll_abcd = findViewByIds(R.id.ll_abcd);
        holder.ll_star = findViewByIds(R.id.ll_star);
        holder.progress = findViewByIds(R.id.review_progress);
        holder.fl_progress_click = findViewByIds(R.id.fl_progress_click);
        holder.ll_choice = findViewByIds(R.id.ll_choice);
        holder.ll_incognizance = findViewByIds(R.id.ll_incognizance);
        holder.tv_incognizance_next = findViewByIds(R.id.tv_incognizance_next);
        holder.tv_incognizance_example = findViewByIds(R.id.tv_incognizance_example);
        holder.tv_know = findViewByIds(R.id.tv_know);
        holder.tv_dim = findViewByIds(R.id.tv_dim);
        holder.tv_unknow = findViewByIds(R.id.tv_unknow);
        holder.fl_example = findViewByIds(R.id.fl_example);
        holder.ll_show_word = findViewByIds(R.id.ll_show_word);
        holder.tv_back = findViewByIds(R.id.tv_back);
        holder.tv_spell = findViewByIds(R.id.tv_spell);
        holder.tv_delete = findViewByIds(R.id.tv_delete);
    }

    private void init_event(){
        holder.tv_incognizance_next.setOnClickListener(this);
        holder.tv_know.setOnClickListener(this);
        holder.tv_dim.setOnClickListener(this);
        holder.tv_unknow.setOnClickListener(this);
        holder.tv_incognizance_next.setOnClickListener(this);
        holder.tv_incognizance_example.setOnClickListener(this);
        holder.ll_show_word.getViewTreeObserver().addOnGlobalLayoutListener(this);
        holder.tv_back.setOnClickListener(this);
        holder.tv_spell.setOnClickListener(this);
        holder.tv_delete.setOnClickListener(this);
    }

    private void init_data(){
        Intent intent = getIntent();
        backgroundNum = intent.getIntExtra("backgroundNum", 0);
        holder.rl_review.setBackgroundResource(images[backgroundNum]);//获取随机数设置复习页面背景图片
        holder.review_sliding.getBackground().setAlpha(100);        //设置SlidingUpMenu的透明度
        holder.fl_example.getBackground().setAlpha(70);  //更改例句界面透明度
        holder.ll_abcd.setVisibility(View.INVISIBLE);        //隐藏abcd选项模式
        holder.ll_star.setVisibility(View.GONE);        //隐藏学习页的星星
        setProgress();        //设置进度条进度
        init_fragment();    //例句Fragment替换布局文件
    }

    //发送handler消息倒数4秒显示单词信息
    private void setProgress(){
        msg = mHandler.obtainMessage();
        if (--num >= 0){
            msg.what = num;
            mHandler.sendMessageDelayed(msg, 1000);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fl_progress_click:
                holder.progress.setVisibility(View.GONE);   //隐藏进度条
                holder.ll_information.setVisibility(View.VISIBLE);  //显示单词信息
                break;
            case R.id.tv_know:
                holder.progress.setVisibility(View.VISIBLE);   //显示进度条
                holder.ll_information.setVisibility(View.GONE);  //隐藏单词信息
                reset();
                setProgress();
                break;
            case R.id.tv_dim:
                holder.progress.setVisibility(View.GONE);   //隐藏进度条
                holder.ll_information.setVisibility(View.VISIBLE);  //显示单词信息
                holder.ll_choice.setVisibility(View.GONE);  //隐藏认识布局
                holder.ll_incognizance.setVisibility(View.VISIBLE); //显示例句布局
                reset();
                break;
            case R.id.tv_unknow:
                holder.progress.setVisibility(View.GONE);   //隐藏进度条
                holder.ll_information.setVisibility(View.VISIBLE);  //显示单词信息
                holder.ll_choice.setVisibility(View.GONE);  //隐藏认识布局
                holder.ll_incognizance.setVisibility(View.VISIBLE); //显示例句布局
                reset();
                break;
            case R.id.tv_incognizance_next:
                reset();
                setProgress();
                holder.ll_choice.setVisibility(View.VISIBLE);  //隐藏认识布局
                holder.ll_incognizance.setVisibility(View.GONE); //显示例句布局
                holder.ll_information.setVisibility(View.GONE);  //隐藏单词信息
                holder.progress.setVisibility(View.VISIBLE);   //显示进度条
                break;
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_spell:
                Toast.makeText(this, "点击了拼写按钮，功能还没写", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_delete:
                Toast.makeText(this, "点击了删除按钮，功能还没写", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    //视图树的回调方法
    @Override
    public void onGlobalLayout() {
        int height = holder.ll_show_word.getHeight();
        holder.fl_example.setMinimumHeight(height);
        holder.ll_show_word.getViewTreeObserver().removeOnGlobalLayoutListener(this);//取消视图树监听
    }


    //取消handler处理，并设置进度条为0，时间重置为4秒
    private void reset(){
        holder.progress.setProgress(0);
        mHandler.removeMessages(msg.what);
        num = 4;
    }

    //用Fragment替换帧布局来显示例句
    private void init_fragment(){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_example, new ExampleSentenceFragment("abroad"), FRAGMENT_SENTENCE);
        transaction.commit();
    }
}
