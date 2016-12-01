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
import android.widget.Toast;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.fragment.ExampleSentenceFragment;
import recitewords.apj.com.recitewords.view.SlidingUpMenu;

/**
 * Created by CGT on 2016/11/31.
 *
 * 学习界面的Activity
 */

public class LearnActivity extends BaseActivity implements View.OnClickListener, ViewTreeObserver.OnGlobalLayoutListener {

    //定义好的8张背景图id数组
    private int[] images = new int[]{R.mipmap.haixin_bg_dim_01, R.mipmap.haixin_bg_dim_02,
            R.mipmap.haixin_bg_dim_03,R.mipmap.haixin_bg_dim_04,
            R.mipmap.haixin_bg_dim_05, R.mipmap.haixin_bg_dim_06};

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

        public SlidingUpMenu learn_sliding;  //上下滑动控件

    }

    private ViewHolder holder;
    private final String FRAGMENT_SENTENCE = "fragment_sentence";
    private int backgroundNum;  //背景图片序号

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
        holder.ll_incognizance = findViewByIds(R.id.ll_incognizance);
        holder.ll_memory = findViewByIds(R.id.ll_memory);
        holder.ll_abcd = findViewByIds(R.id.ll_abcd);
        holder.ll_information = findViewByIds(R.id.ll_information);
        holder.pb_loading = findViewByIds(R.id.pb_loading);
        holder.tv_word_information = findViewByIds(R.id.tv_word_information);
        holder.ll_show_word = findViewByIds(R.id.ll_show_word);
        holder.learn_sliding = findViewByIds(R.id.learn_sliding);
        holder.tv_back = findViewByIds(R.id.tv_back);
        holder.tv_spell = findViewByIds(R.id.tv_spell);
        holder.tv_delete = findViewByIds(R.id.tv_delete);

    }

    private void initData() {

        Intent intent = getIntent();
        backgroundNum = intent.getIntExtra("backgroundNum", 0);
        //设置学习界面的背景图片与主页面的背景图片动态一致
        holder.rl_learn.setBackgroundResource(images[backgroundNum]);

        holder.learn_sliding.getBackground().setAlpha(120);  //更改学习界面透明度
        holder.fl_example.getBackground().setAlpha(70);  //更改例句界面透明度

        //用Fragment替换帧布局来显示例句
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_example, new ExampleSentenceFragment("absorb"), FRAGMENT_SENTENCE);
        transaction.commit();
    }

    private void initEvent() {
        //选择题模式下点击不认识
        holder.ll_choice.setOnClickListener(this);
        //监听视图树获取高度
        holder.ll_show_word.getViewTreeObserver().addOnGlobalLayoutListener(this);
        //监听底部返回按钮
        holder.tv_back.setOnClickListener(this);
        //监听底部拼写按钮
        holder.tv_spell.setOnClickListener(this);
        //监听底部删除单词按钮
        holder.tv_delete.setOnClickListener(this);
    }

    //点击事件回调方法
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_choice:
                holder.ll_choice.setVisibility(View.INVISIBLE);
                holder.ll_memory.setVisibility(View.INVISIBLE);
                holder.ll_incognizance.setVisibility(View.VISIBLE);

                holder.ll_abcd.setVisibility(View.INVISIBLE);
                holder.ll_information.setVisibility(View.VISIBLE);
                holder.tv_word_information.setVisibility(View.VISIBLE);
                holder.pb_loading.setVisibility(View.INVISIBLE);
                break;
            case R.id.tv_back:
                //保存数据，显示progressbar,返回主页面
//                Intent intent = new Intent(LearnActivity.this, MainActivity.class);
//                intent.putExtra("backgroundNum",backgroundNum);
//                startActivity(intent);
                finish();
                break;
            case R.id.tv_spell:
                //打开拼写界面
                Toast.makeText(LearnActivity.this, "点击拼写按钮", Toast.LENGTH_LONG).show();
                break;
            case R.id.tv_delete:
                //从学习单词表中删除单词，表示已经掌握
                Toast.makeText(LearnActivity.this, "点击删除按钮", Toast.LENGTH_LONG).show();
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
