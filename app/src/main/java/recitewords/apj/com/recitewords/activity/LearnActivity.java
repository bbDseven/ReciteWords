package recitewords.apj.com.recitewords.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.fragment.ExampleSentenceFragment;
import recitewords.apj.com.recitewords.util.MediaUtils;
import recitewords.apj.com.recitewords.util.UIUtil;
import recitewords.apj.com.recitewords.view.SlidingUpMenu;

/**
 * Created by CGT on 2016/11/31.
 * <p/>
 * 学习界面的Activity
 */

public class LearnActivity extends BaseActivity implements View.OnClickListener,
        ViewTreeObserver.OnGlobalLayoutListener, SlidingUpMenu.OnToggleListener, TextWatcher {

    //定义好的8张背景图id数组
    private int[] images = new int[]{R.mipmap.haixin_bg_dim_01, R.mipmap.haixin_bg_dim_02,
            R.mipmap.haixin_bg_dim_03, R.mipmap.haixin_bg_dim_04,
            R.mipmap.haixin_bg_dim_05, R.mipmap.haixin_bg_dim_06};


    public static class ViewHolder {
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
        public static SlidingUpMenu learn_sliding;  //上下滑动控件

        public TextView spell_tv_close;  //拼写关闭
        public RelativeLayout spell_rl_root;  //拼写根布局
        public EditText spell_et_input;  //拼写用户输入
        public TextView spell_tv_correct;  //拼写显示正确单词
        public TextView spell_tv_confirm;  //拼写确认单词
        public TextView spell_tv_prompt;  //拼写单词提示
        public RelativeLayout spell_rl_back;  //评写界面黑色背景
        public RelativeLayout spell_rl_bottom;  //评写界面底部背景
    }

    private ViewHolder holder;
    private Handler mHandler = new Handler();
    private final String FRAGMENT_SENTENCE = "fragment_sentence";
    private int backgroundNum;  //背景图片序号
    private OnToggleListner mOnToggleListner;  //监听例句显示状态
    private AlertDialog alertDialog;  //评写界面弹窗
    private String mWord = "target";  //显示的单词
    private String mPhonogram = "[ 'shabi: ]";  //显示的单词的音标
    private boolean havaing_comfirm = false;  //拼写是否已经和正确单词比较,默认为false

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

        holder.learn_sliding.getBackground().setAlpha(170);  //更改学习界面透明度
        holder.fl_example.getBackground().setAlpha(70);  //更改例句界面透明度

        //用Fragment替换帧布局来显示例句
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_example, new ExampleSentenceFragment("abroad"), FRAGMENT_SENTENCE);
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
        //监听例句是否显示
        holder.learn_sliding.setOnToggleListener(this);
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

                AlertDialog.Builder mLoadingBuilder=new AlertDialog.Builder(this);
                AlertDialog mLoadingAlertDialog = mLoadingBuilder.create();
                View mLoadingView = getLayoutInflater().inflate(R.layout.dialog_save_loading, null);
                mLoadingAlertDialog.setView(mLoadingView);
                mLoadingAlertDialog.show();
               // finish();
                break;
            case R.id.tv_spell:
                //打开拼写界面
                AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog_Fullscreen);

                this.alertDialog = builder.create();
                View view = getLayoutInflater().inflate(R.layout.dialog_spell, null);
                this.alertDialog.setView(view, 0, 0, 0, 0);
                holder.spell_tv_close = UIUtil.findViewByIds(view, R.id.spell_tv_close);
                holder.spell_rl_root = UIUtil.findViewByIds(view, R.id.spell_rl_root);
                holder.spell_et_input = UIUtil.findViewByIds(view, R.id.spell_et_input);
                holder.spell_tv_correct = UIUtil.findViewByIds(view, R.id.spell_tv_correct);
                holder.spell_tv_confirm = UIUtil.findViewByIds(view, R.id.spell_tv_confirm);
                holder.spell_tv_prompt = UIUtil.findViewByIds(view, R.id.spell_tv_prompt);
                holder.spell_rl_back = UIUtil.findViewByIds(view, R.id.spell_rl_back);
                holder.spell_rl_bottom = UIUtil.findViewByIds(view, R.id.spell_rl_bottom);

                holder.spell_rl_root.setBackgroundResource(images[backgroundNum]);
                //设置黑色背景透明度，模糊化背景图片
//                holder.spell_rl_back.getBackground().setAlpha(210);
                //底部暗灰色导航条，使背景模糊化
                holder.spell_rl_bottom.getBackground().setAlpha(170);

                holder.spell_tv_close.setOnClickListener(this);  //监听拼写关闭按钮
                holder.spell_tv_prompt.setOnClickListener(this);  //监听评写提示按钮
                holder.spell_tv_confirm.setOnClickListener(this);  //监听评写确认按钮
                holder.spell_et_input.addTextChangedListener(this);  //监听文本框内容变化
                this.alertDialog.show();
                break;
            case R.id.tv_delete:
                //从学习单词表中删除单词，表示已经掌握
                Toast.makeText(LearnActivity.this, "点击删除按钮", Toast.LENGTH_LONG).show();
                break;
            case R.id.spell_tv_close:
                //关闭拼写界面
                this.alertDialog.dismiss();
                break;
            case R.id.spell_tv_prompt:
                //提示用户的单词信息，显示音标和读音
                MediaUtils.playWord(this, "abroad");  //播放单词
                //获取底部导航栏高度，让土司显示在中间
                int height = holder.spell_rl_bottom.getHeight();
                int time=3000;  //提示时间3秒
                //自定义土司，设置土司显示位置
                UIUtil.toast(this,mPhonogram,time, Gravity.BOTTOM,0,height/2-20);
                //改变背景图片
                holder.spell_tv_prompt.setBackgroundResource(R.mipmap.ic_spell_prompt_highlight);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.spell_tv_prompt.setBackgroundResource(R.mipmap.ic_spell_prompt);
                    }
                },time);

                break;
            case R.id.spell_tv_confirm:
                //比较用户输入单词和正确单词
                String word = holder.spell_et_input.getText().toString();  //获取用户输入的单词
                if (word.equals(mWord)) {
                    //改变文字颜色--淡黄
                    holder.spell_et_input.setTextColor(Color.parseColor("#D1F57F"));
                    MediaUtils.playWord(this, "abroad");  //播放单词
                } else {
                    //改变文字颜色--红色
                    holder.spell_et_input.setTextColor(Color.parseColor("#FF4444"));
                    MediaUtils.playWord(this, "abroad");  //播放单词
                    holder.spell_tv_correct.setText(mWord);  //显示正确的单词
                    holder.spell_tv_correct.setVisibility(View.VISIBLE);
                }
                havaing_comfirm = true;  //改变是否比较状态
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

    //监听例句是否显示
    @Override
    public void onToggleChange(SlidingUpMenu view, boolean isOpen) {
        if (mOnToggleListner != null) {
            mOnToggleListner.onToggleChange(view, isOpen);
        }
    }
    String input="";
    //文本框变化前
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        input=s.toString();  //内容默认为
    }

    //文本框变化中
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (havaing_comfirm) {
            input = s.toString().substring(start, (start + count));  //获取用户重新输入的值
        } else {
            input=s.toString();
        }
    }

    //文本框变化后
    @Override
    public void afterTextChanged(Editable s) {
        if (havaing_comfirm){
            havaing_comfirm=false;
            //隐藏正确单词
            holder.spell_tv_correct.setVisibility(View.INVISIBLE);
            //用户在比较单词后会改变输入文字颜色，在此处确保用户再次输入的文字为白色
            int color = Color.parseColor("#FFFFFF");
            holder.spell_et_input.setTextColor(color);
            holder.spell_et_input.setText(input);
            holder.spell_et_input.setSelection(input.length());  //设置光标位置
        }

    }

    /**
     * 监听例句是否显示
     * @param listener  监听者
     */
    public void setOnToggleListener(OnToggleListner listener) {
        this.mOnToggleListner = listener;
    }

    public interface OnToggleListner {
        void onToggleChange(SlidingUpMenu view, boolean isOpen);
    }


}
