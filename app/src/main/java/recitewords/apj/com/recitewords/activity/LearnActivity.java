package recitewords.apj.com.recitewords.activity;

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

    }

    private void initData() {

        holder.rl_learn.getBackground().setAlpha(100);

    }

    private void initEvent() {

    }

}
