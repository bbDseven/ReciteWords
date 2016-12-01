package recitewords.apj.com.recitewords.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.fragment.ExampleSentenceFragment;
import recitewords.apj.com.recitewords.view.SlidingUpMenu;

public class LearnActivity extends BaseActivity implements View.OnClickListener {

    public class ViewHolder {
        public LinearLayout ll_choice;  //选择题模式--中间按钮--不认识
        public LinearLayout ll_incognizance;   //不认识--中间按钮
        public LinearLayout ll_memory;   //回忆模式--中间按钮

        public LinearLayout ll_abcd;   //选择题模式--显示ABCD选项
        public LinearLayout ll_information;   //不认识-，回忆模式-显示词性词义
        public ProgressBar pb_loading;   //回忆模式加载中
        public TextView tv_word_information;  //不认识-，回忆模式-词性词义

        public TextView tv_incognizance_next;  //不认识--下一个
        public TextView tv_incognizance_example;  //不认识--看例句
        public TextView tv_memory_cognize;  //回忆模式--认识
        public TextView tv_memory_incognizance;  //回忆模式--不认识
        public RelativeLayout rl_learn;   //学习的根布局
        public FrameLayout ll_example;  //例句的根布局
        public LinearLayout ll_show_word;  //顶部显示的单词，音标和学习情况根布局
        public SlidingUpMenu learn_sliding;
    }

    private ViewHolder holder;
    private final String FRAGMENT_SENTENCE = "fragment_sentence";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        holder = new ViewHolder();
        holder.rl_learn = findViewByIds(R.id.rl_learn);
        holder.ll_example = findViewByIds(R.id.ll_example);
        holder.ll_choice = findViewByIds(R.id.ll_choice);
        holder.ll_incognizance=findViewByIds(R.id.ll_incognizance);
        holder.ll_memory=findViewByIds(R.id.ll_memory);
        holder.ll_abcd=findViewByIds(R.id.ll_abcd);
        holder.ll_information=findViewByIds(R.id.ll_information);
        holder.pb_loading=findViewByIds(R.id.pb_loading);
        holder.tv_word_information=findViewByIds(R.id.tv_word_information);
        holder.ll_show_word=findViewByIds(R.id.ll_show_word);
        holder.learn_sliding = findViewByIds(R.id.learn_sliding);

        holder.ll_show_word.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = holder.ll_show_word.getHeight();  //和例句一样的高度
                holder.ll_example.setMinimumHeight(height);
                holder.ll_show_word.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void initData() {

        Intent intent = getIntent();
        int backgroundNum = intent.getIntExtra("backgroundNum", 0);
        int[] images = intent.getIntArrayExtra("images");
        holder.rl_learn.setBackgroundResource(images[backgroundNum]);
        holder.learn_sliding.getBackground().setAlpha(150);  //更改学习界面透明度
        holder.ll_example.getBackground().setAlpha(70);  //更改例句界面透明度

        //用Fragment替换帧布局来显示例句
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.ll_example, new ExampleSentenceFragment("about"), FRAGMENT_SENTENCE);
        transaction.commit();
    }

    private void initEvent() {
        holder.ll_choice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_choice:
                holder.ll_choice.setVisibility(View.INVISIBLE);
                holder.ll_memory.setVisibility(View.INVISIBLE);
                holder.ll_incognizance.setVisibility(View.VISIBLE);

                holder.ll_abcd.setVisibility(View.INVISIBLE);
                holder.ll_information.setVisibility(View.VISIBLE);
                holder.tv_word_information.setVisibility(View.VISIBLE);
                holder.pb_loading.setVisibility(View.INVISIBLE);
                break;
            default:
                break;
        }
    }


}
