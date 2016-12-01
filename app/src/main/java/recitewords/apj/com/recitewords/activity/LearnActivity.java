package recitewords.apj.com.recitewords.activity;

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

public class LearnActivity extends BaseActivity implements View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener {

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
        public FrameLayout fl_example;  //例句的根布局
        public LinearLayout ll_show_word;  //顶部显示的单词，音标和学习情况根布局

        public TextView tv_back;  //底部返回按钮
        public TextView tv_spell;  //底部拼写按钮
        public TextView tv_delete;  //底部删除按钮
    }

    private ViewHolder holder;

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
        holder.fl_example = findViewByIds(R.id.fl_example);
        holder.ll_choice = findViewByIds(R.id.ll_choice);
        holder.ll_incognizance=findViewByIds(R.id.ll_incognizance);
        holder.ll_memory=findViewByIds(R.id.ll_memory);
        holder.ll_abcd=findViewByIds(R.id.ll_abcd);
        holder.ll_information=findViewByIds(R.id.ll_information);
        holder.pb_loading=findViewByIds(R.id.pb_loading);
        holder.tv_word_information=findViewByIds(R.id.tv_word_information);
        holder.ll_show_word=findViewByIds(R.id.ll_show_word);

        holder.tv_back=findViewByIds(R.id.tv_back);
        holder.tv_spell=findViewByIds(R.id.tv_back);
        holder.tv_delete=findViewByIds(R.id.tv_back);

    }

    private void initData() {

        Intent intent = getIntent();
        int backgroundNum = intent.getIntExtra("backgroundNum", 0);
        int[] images = intent.getIntArrayExtra("images");
        holder.rl_learn.setBackgroundResource(images[backgroundNum]);
        holder.rl_learn.getBackground().setAlpha(70);  //更改学习界面透明度
        holder.fl_example.getBackground().setAlpha(70);  //更改例句界面透明度
    }

    private void initEvent() {
        //选择题模式下点击不认识
        holder.ll_choice.setOnClickListener(this);
        //监听视图树获取高度
        holder.ll_show_word.getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    //点击事件回调方法
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

    //视图树的回调方法
    @Override
    public void onGlobalLayout() {
        int height = holder.ll_show_word.getHeight();  //和例句一样的高度
        holder.fl_example.setMinimumHeight(height);  //设置例句高度
        holder.ll_show_word.getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }


}
