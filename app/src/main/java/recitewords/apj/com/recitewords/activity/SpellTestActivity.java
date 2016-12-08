package recitewords.apj.com.recitewords.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.bean.WordReview;
import recitewords.apj.com.recitewords.util.MediaUtils;
import recitewords.apj.com.recitewords.util.UIUtil;


/**
 * Created by CGT on 2016/12/7.
 * <p/>
 * 拼写测试
 *
 * 默认为不管是不是一次性通过，学完一篇后就不再复习
 */
public class SpellTestActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    //定义好的8张背景图id数组
    private int[] images = new int[]{R.mipmap.haixin_bg_dim_01, R.mipmap.haixin_bg_dim_02,
            R.mipmap.haixin_bg_dim_03, R.mipmap.haixin_bg_dim_04,
            R.mipmap.haixin_bg_dim_05, R.mipmap.haixin_bg_dim_06};

    public class ViewHolder {
        public TextView spell_tv_situation;
        public LinearLayout spell_Dot;
        TextView spell_tv_close;  //拼写关闭
        RelativeLayout spell_rl_root;  //拼写根布局
        EditText spell_et_input;  //拼写用户输入
        TextView spell_tv_correct;  //拼写显示正确单词
        TextView spell_tv_confirm;  //拼写确认单词
        TextView spell_tv_prompt;  //拼写单词提示
        RelativeLayout spell_rl_back;  //评写界面黑色背景
        RelativeLayout spell_rl_bottom;  //评写界面底部背景
        TextView spell_tv_spell_next;
    }

    private final String TAG = "SpellTestActivity";
    private ViewHolder holder;
    private Handler mHandler = new Handler();
    private String mPhonogram;  //音标
    private String mWord;  //单词
    private String mWordMean;  //词义
    private boolean havaing_comfirm = false;  //是否已经比较过
    private int banckgroundIndex;  //背景图片位置
    private String SpellMode;  //拼写模式，是单词拼写（学习单词时）还是拼写测试（学完一组后）
    private List<WordReview> mReviewWords;  //拼写测试下的单词数据
    private String input = "";  //文本框内容
    private int mSpellIndex = 0;  //当前拼写单词的位置
    private boolean is_Pass = true;  //一次拼写通过
    List<Integer> passIndexs;  //一次性通过的集合
    private int height;  //底部导航栏高度


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_spell);
        initView();
        initEvent();
        initData();
    }

    private void initView() {
        holder = new ViewHolder();
        holder.spell_tv_situation = findViewByIds(R.id.spell_tv_situation);
        holder.spell_Dot = findViewByIds(R.id.spell_Dot);
        holder.spell_tv_close = findViewByIds(R.id.spell_tv_close);
        holder.spell_rl_root = findViewByIds(R.id.spell_rl_root);
        holder.spell_et_input = findViewByIds(R.id.spell_et_input);
        holder.spell_tv_correct = findViewByIds(R.id.spell_tv_correct);
        holder.spell_tv_confirm = findViewByIds(R.id.spell_tv_confirm);
        holder.spell_tv_prompt = findViewByIds(R.id.spell_tv_prompt);
        holder.spell_rl_back = findViewByIds(R.id.spell_rl_back);
        holder.spell_rl_bottom = findViewByIds(R.id.spell_rl_bottom);
        holder.spell_tv_spell_next = findViewByIds(R.id.spell_tv_spell_next);
    }

    private void initEvent() {
        holder.spell_tv_close.setOnClickListener(this);  //监听拼写关闭按钮
        holder.spell_tv_prompt.setOnClickListener(this);  //监听评写提示按钮
        holder.spell_tv_confirm.setOnClickListener(this);  //监听评写确认按钮
        holder.spell_et_input.addTextChangedListener(this);  //监听文本框内容变化
        holder.spell_tv_spell_next.setOnClickListener(this);
    }

    private void initData() {
        passIndexs=new ArrayList<>();

        Intent intent = getIntent();
        banckgroundIndex = intent.getIntExtra("BanckgroundIndex", 0);
        SpellMode = intent.getStringExtra("Mode");

        //设置背景
        holder.spell_rl_root.setBackgroundResource(images[banckgroundIndex]);
        holder.spell_rl_back.getBackground().setAlpha(170);
        holder.spell_rl_bottom.getBackground().setAlpha(150);
        holder.spell_tv_spell_next.setVisibility(View.GONE);  //隐藏下一个

        if (SpellMode.equals("spell")) {  //拼写
            mWord = intent.getStringExtra("Word");
            mPhonogram = intent.getStringExtra("Phonogram");
            holder.spell_tv_situation.setVisibility(View.INVISIBLE);
            holder.spell_Dot.setVisibility(View.INVISIBLE);
            holder.spell_tv_spell_next.setVisibility(View.INVISIBLE);
        } else if (SpellMode.equals("spellTest")) {  //拼写测试
            holder.spell_tv_situation.setVisibility(View.VISIBLE);  //显示拼写情况
            holder.spell_Dot.setVisibility(View.VISIBLE);  //显示圆点
            holder.spell_tv_correct.setVisibility(View.VISIBLE);  //显示词义

            mReviewWords = (List<WordReview>) intent.getSerializableExtra("words");
            //动态设置圆点个数
            for (int i = 0; i < mReviewWords.size(); i++) {
                View pointView = new View(this);
                pointView.setBackgroundResource(R.drawable.circle_gray_shape);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, 10);
                params.rightMargin = 12;
                pointView.setLayoutParams(params);
                holder.spell_Dot.addView(pointView);
            }
            spellNextWOrd();
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.spell_tv_close:
                //关闭拼写界面
                finish();
                break;
            case R.id.spell_tv_prompt:
                //提示用户的单词信息，显示音标和读音
                MediaUtils.playWord(this, mWord);  //播放单词
                //获取底部导航栏高度，让土司显示在中间
                height = holder.spell_rl_bottom.getHeight();
                int time = 3000;  //提示时间3秒
                //自定义土司，设置土司显示位置
                UIUtil.toast(this, mPhonogram, time, Gravity.BOTTOM, 0, height / 2 - 20, 16);
                //改变背景图片
                holder.spell_tv_prompt.setBackgroundResource(R.mipmap.ic_spell_prompt_highlight);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.spell_tv_prompt.setBackgroundResource(R.mipmap.ic_spell_prompt);
                    }
                }, time);

                break;
            case R.id.spell_tv_confirm:
                //比较用户输入单词和正确单词
                input = holder.spell_et_input.getText().toString();  //获取用户输入的单词
                if (input.equals(mWord)) {
                    //改变文字颜色--淡黄
                    holder.spell_et_input.setTextColor(Color.parseColor("#D1F57F"));
                    MediaUtils.playWord(this, mWord);  //播放单词
                    //拼写测试的模式下,隐藏比较图标，显示下一个图标
                    if (SpellMode.equals("spell")) {

                    } else if (SpellMode.equals("spellTest")) {
                        holder.spell_tv_confirm.setVisibility(View.GONE);
                        holder.spell_tv_spell_next.setVisibility(View.VISIBLE);
                        if (is_Pass) {
                            //一次通过，改变圆点颜色
                            passIndexs.add(mSpellIndex);  //增加到一次性通过的集合中去
                            holder.spell_Dot.getChildAt(mSpellIndex-1).setBackgroundResource(R.drawable.circle_white_shape);
                        }
                    }

                } else {
                    //改变文字颜色--红色
                    holder.spell_et_input.setTextColor(Color.parseColor("#FF4444"));
                    MediaUtils.playWord(this, "abroad");  //播放单词
                    holder.spell_tv_correct.setText(mWord);  //显示正确的单词
                    if (SpellMode.equals("spell")) {

                    } else if (SpellMode.equals("spellTest")) {
                        UIUtil.toast(this, mPhonogram, 2000,Gravity.BOTTOM ,0,height / 2-20,16);
                        holder.spell_tv_prompt.setBackgroundResource(R.mipmap.ic_spell_prompt_highlight);
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder.spell_tv_prompt.setBackgroundResource(R.mipmap.ic_spell_prompt);
                            }
                        }, 2000);
                    }

                    holder.spell_tv_correct.setVisibility(View.VISIBLE);
                    is_Pass = false;  //错误一次，不是一次通过
                }
                havaing_comfirm = true;  //改变是否比较状态
                break;
            case R.id.spell_tv_spell_next:
                spellNextWOrd();
                is_Pass=true;
                break;
            default:
                break;
        }
    }


    /**
     * 拼写下一个单词
     */
    public void spellNextWOrd() {

        if (mSpellIndex<mReviewWords.size()){
        mWord = mReviewWords.get(mSpellIndex).getWord();
        mPhonogram = mReviewWords.get(mSpellIndex).getSoundmark_british();
        mWordMean = mReviewWords.get(mSpellIndex).getAnswer_right();
        //当题目，圆点变大
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
        if (mSpellIndex==0){
            params.rightMargin=20;
        }else {
            params.leftMargin=5;
            params.rightMargin=15;
        }
        holder.spell_Dot.getChildAt(mSpellIndex).setLayoutParams(params);

        //上一题圆点大小还原
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(10, 10);
        params1.rightMargin=10;
        if (mSpellIndex==0){
            holder.spell_Dot.getChildAt(mReviewWords.size()-1).setLayoutParams(params1);
        }else {
            holder.spell_Dot.getChildAt(mSpellIndex-1).setLayoutParams(params1);
        }

        holder.spell_tv_correct.setText(mWordMean);  //显示词义
        holder.spell_tv_confirm.setVisibility(View.VISIBLE);  //显示比较按钮
        holder.spell_tv_spell_next.setVisibility(View.GONE);  //隐藏下一个
        holder.spell_et_input.setText("");
        mSpellIndex++;
        }else {//学完一遍后
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("温馨提醒：");
            builder.setMessage("你已完成本组单词复习，赶快去领取酷币吧！");
            AlertDialog alertDialog = builder.create();
            builder.setPositiveButton("确定",null);
            alertDialog.show();
        }
    }

    //文本框变化前
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        input = s.toString();  //内容默认为
    }

    //文本框变化中
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (havaing_comfirm) {
            input = s.toString().substring(start, (start + count));  //获取用户重新输入的值
        } else {
            input = s.toString();
        }
    }

    //文本框变化后
    @Override
    public void afterTextChanged(Editable s) {
        if (havaing_comfirm) {
            havaing_comfirm = false;

            if (SpellMode.equals("spell")) {
                //隐藏正确单词
                holder.spell_tv_correct.setVisibility(View.INVISIBLE);
            } else if (SpellMode.equals("spellTest")) {
                holder.spell_tv_correct.setText(mWordMean);  //显示词义
            }

            //用户在比较单词后会改变输入文字颜色，在此处确保用户再次输入的文字为白色
            int color = Color.parseColor("#FFFFFF");
            holder.spell_et_input.setTextColor(color);
            holder.spell_et_input.setText(input);
            holder.spell_et_input.setSelection(input.length());  //设置光标位置
        }

    }

}
