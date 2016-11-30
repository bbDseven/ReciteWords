package recitewords.apj.com.recitewords.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import recitewords.apj.com.recitewords.R;

public class LearnActivity extends BaseActivity {

    public class ViewHolder {
        public TextView tv_show_example;
        public LinearLayout ll_choice;  //选择题模式--中间点击按钮
        public LinearLayout ll_incognizance;   //不认识--中间点击按钮
        public LinearLayout ll_memory;   //回忆模式--中间点击按钮
        public TextView tv_incognizance_next;  //不认识--下一个
        public TextView tv_incognizance_example;  //不认识--看例句
        public TextView tv_memory_cognize;  //回忆模式--认识
        public TextView tv_memory_incognizance;  //回忆模式--不认识
        public RelativeLayout rl_learn;   //学习的根布局
        public LinearLayout ll_example;  //例句的根布局
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
        holder=new ViewHolder();
        holder.rl_learn=findViewByIds(R.id.rl_learn);
        holder.ll_example = (LinearLayout) findViewById(R.id.ll_example);
    }

    private void initData() {

        Intent intent = getIntent();
        int backgroundNum = intent.getIntExtra("backgroundNum", 0);
        int[] images = intent.getIntArrayExtra("images");
        holder.rl_learn.setBackgroundResource(images[backgroundNum]);
        holder.rl_learn.getBackground().setAlpha(70);  //更改学习界面透明度
        holder.ll_example.getBackground().setAlpha(70);  //更改例句界面透明度

    }

    private void initEvent() {

    }

}
