package recitewords.apj.com.recitewords.activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.bean.WordStudy;
import recitewords.apj.com.recitewords.db.dao.BookDao;
import recitewords.apj.com.recitewords.db.dao.WordStudyDao;
import recitewords.apj.com.recitewords.fragment.ExampleSentenceFragment;
import recitewords.apj.com.recitewords.util.LearnWordsUtil;
import recitewords.apj.com.recitewords.util.MediaUtils;
import recitewords.apj.com.recitewords.util.NumUtil;
import recitewords.apj.com.recitewords.util.UIUtil;
import recitewords.apj.com.recitewords.view.CircleProgressView;
import recitewords.apj.com.recitewords.view.SlidingUpMenu;

/**
 * Created by CGT and CJZ on 2016/11/31.
 * <p/>
 * 学习界面的Activity
 */

public class LearnActivity extends BaseActivity implements View.OnClickListener,
        ViewTreeObserver.OnGlobalLayoutListener, SlidingUpMenu.OnToggleListener{

    //定义好的8张背景图id数组
    private int[] images = new int[]{R.mipmap.haixin_bg_dim_01, R.mipmap.haixin_bg_dim_02,
            R.mipmap.haixin_bg_dim_03, R.mipmap.haixin_bg_dim_04,
            R.mipmap.haixin_bg_dim_05, R.mipmap.haixin_bg_dim_06};

    private ViewHolder holder;
    //消息机制，用来显示圆圈滚动
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case 3:
                    holder.progress.setProgress(25);
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
                    holder.progress.setVisibility(View.INVISIBLE);   //隐藏进度条
                    holder.tv_word_information.setVisibility(View.VISIBLE);//显示单词词义
                    num = 4;    //进度条显示完num重新设置回4秒
                    break;
                default:
                    break;
            }
            return false;
        }
    });
    private int num = 4; //定义4秒
    private final String FRAGMENT_SENTENCE = "fragment_sentence";
    private int backgroundNum;  //背景图片序号
    private OnToggleListner mOnToggleListner;  //监听例句显示状态
    private AlertDialog alertDialog;  //评写界面弹窗
    private boolean havaing_comfirm = false;  //拼写是否已经和正确单词比较,默认为false
    private Message msg;
    private int order = 0; // 定义一个全局变量，用于显示要学习的第几个单词 刚开始是0
    private ArrayList<WordStudy> studyWords = new ArrayList<>();// 存放20个单词的内容，包括：单词，美式音标，英式音标，单词含义的集合

    public static class ViewHolder {
        public LinearLayout ll_choice;  //选择题模式--中间按钮--不认识
        public LinearLayout ll_incognizance;   //不认识--中间按钮
        public LinearLayout ll_memory;   //回忆模式--中间按钮

        public LinearLayout ll_abcd;   //选择题模式--显示ABCD选项
        public LinearLayout ll_information;   //不认识-，回忆模式-显示词性词义
        public CircleProgressView progress; //圆形进度条

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

        public TextView learn_tv_word;           //要学习的单词
        public TextView learn_tv_soundMark;     // 音标
        public TextView learn_tv_changesound;   //音标按钮
        public TextView tv_A, tv_B, tv_C, tv_D; //A B C D 选项
        public TextView tv_word_information;    //单词含义答案

        public ImageView asterisk_one, asterisk_two, asterisk_there, asterisk_four; // 4个星号

        public TextView completed_words,no_completed_words;    //已学习的单词数量，剩余单词数量
    }

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
        holder.tv_word_information = findViewByIds(R.id.tv_word_information);
        holder.ll_show_word = findViewByIds(R.id.ll_show_word);
        holder.learn_sliding = findViewByIds(R.id.learn_sliding);
        holder.tv_back = findViewByIds(R.id.tv_back);
        holder.tv_spell = findViewByIds(R.id.tv_spell);
        holder.tv_delete = findViewByIds(R.id.tv_delete);

        holder.tv_incognizance_example = findViewByIds(R.id.tv_incognizance_example);//不认识--看例句
        holder.tv_incognizance_next = findViewByIds(R.id.tv_incognizance_next);//不认识--下一个
        holder.tv_memory_cognize = findViewByIds(R.id.tv_memory_cognize);//回忆模式--认识
        holder.tv_memory_incognizance = findViewByIds(R.id.tv_memory_incognizance);//回忆模式--不认识
        holder.progress = (CircleProgressView)holder.ll_information.findViewById(R.id.learn_progress);// 定义圆形控件
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

        studyWords = LearnWordsUtil.getWords(this);//初始化20个单词数据
        isWordAsteriskFour();                //判断单词的星号是否为4,如果为4，则不显示,显示下一个单词
        showWordsInformation(order);        //显示 要学习单词各个信息
    }

    private void initEvent() {
        //监听不认识点击事件
        holder.ll_choice.setOnClickListener(this);
        // 监听“下一个” 的点击事件
        holder.tv_incognizance_next.setOnClickListener(this);
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
        //监听看例句
        holder.tv_incognizance_example.setOnClickListener(this);
        //监听 回忆模式--认识
        holder.tv_memory_cognize.setOnClickListener(this);
        //监听 回忆模式--不认识
        holder.tv_memory_incognizance.setOnClickListener(this);

        holder.learn_tv_word = (TextView) holder.ll_show_word.findViewById(R.id.learn_tv_word);//获取要学习单词的控件
        holder.learn_tv_soundMark = (TextView) holder.ll_show_word.findViewById(R.id.learn_tv_soundmark);//获取音标控件
        holder.learn_tv_changesound = (TextView) holder.ll_show_word.findViewById(R.id.learn_tv_changesound);//获取音标按钮控件

        holder.learn_tv_changesound.setOnClickListener(this);//监听音标按钮控件

        holder.tv_A = (TextView) holder.ll_abcd.findViewById(R.id.tv_A); //获取选项A控件
        holder.tv_B = (TextView) holder.ll_abcd.findViewById(R.id.tv_B);//获取选项B控件
        holder.tv_C = (TextView) holder.ll_abcd.findViewById(R.id.tv_C);//获取选项C控件
        holder.tv_D = (TextView) holder.ll_abcd.findViewById(R.id.tv_D);//获取选项D控件

        holder.tv_A.setOnClickListener(this);//监听A选项
        holder.tv_B.setOnClickListener(this);//监听B选项
        holder.tv_C.setOnClickListener(this);//监听C选项
        holder.tv_D.setOnClickListener(this);//监听D选项

        holder.asterisk_one = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_one);//获取星号1控件
        holder.asterisk_two = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_two);//获取星号2控件
        holder.asterisk_there = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_there);//获取星号3控件
        holder.asterisk_four = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_four);//获取星号4控件

        holder.completed_words = (TextView)holder.ll_show_word.findViewById(R.id.tv_complete);//获取左上角已学习单词
        holder.no_completed_words = (TextView)holder.ll_show_word.findViewById(R.id.tv_need_complete);//获取右上角剩余单词

        holder.completed_words.setOnClickListener(this);      //监听左上角已学习单词
        holder.no_completed_words.setOnClickListener(this);   //监听右上角剩余单词

    }

    //点击事件回调方法
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_complete:
                UIUtil.toast(this, "已学习完成" + holder.completed_words.getText().toString() + "个", 3000, Gravity.TOP, 0, 0, 16);
                break;
            case R.id.tv_need_complete:
                UIUtil.toast(this, "本组剩余" + holder.completed_words.getText().toString() + "个", 3000, Gravity.TOP, 0, 0, 16);
                break;
            case R.id.ll_choice:                    //点击不认识
                reduceWordAsterisk(order);           //点击不认识，该单词的星号归零
                showWordsInformation(order);         //显示单词 第一部分的 信息
                incognizance();                      //显示该单词词义模式
                showWordAnswer(order);              //显示该单词答案
                break;
            case R.id.tv_incognizance_next:       //点击下一个
                isShowLearnWord();                  //判断单词下标是否大于19，然后进行一次循环
                if (studyWords.get(order).getAsterisk()==1||studyWords.get(order).getAsterisk()==3){
                    memory();
                    setProgress();
                    showWordAnswer(order);
                }else {
                    choice();                            //显示选择题模式
                    showWordsInformation(order);        //显示单词信息
                    showWordOption(order);              //显示ABCD选项
                }
                break;
            case R.id.tv_memory_cognize:          // 点击记忆模式的 认识
                addWordAsterisk(order);                //用户 认识 单词，星号加1
                isShowLearnWord();                  //判断单词下标是否大于19，然后进行一次循环
                if (studyWords.get(order).getAsterisk()==1||studyWords.get(order).getAsterisk()==3){
                    memory();
                    setProgress();
                    showWordAnswer(order);
                }else {
                    choice();                               // 显示选择题模式
                    showWordsInformation(order);           //显示单词信息
                    showWordOption(order);                 //显示ABCD选项
                }
                break;
            case R.id.tv_memory_incognizance:    //点击记忆模式的不认识
                reduceWordAsterisk(order);              //点击不认识, 该单词星号归零
                incognizance();                         //用户不认识该单词，显示单词词义
                showWordsInformation(order);           // 显示单词信息
                showWordAnswer(order);                  //显示单词答案
                break;
            case R.id.tv_A:                                 //点击A选项
                String A_option = holder.tv_A.getText().toString().trim();//获取A选项词义
                Log.e("用户选择的是",""+A_option);
                if (check(A_option)){                       //判断是否选择正确
                    Log.e("该单词是：",""+studyWords.get(order).getWord());
                    addWordAsterisk(order);                //答对 该 单词，星号加1
                    isShowLearnWord();                  //判断单词下标是否大于19，然后进行一次循环
                    if (studyWords.get(order).getAsterisk()==1||studyWords.get(order).getAsterisk()==3){
                        memory();
                        setProgress();
                        showWordAnswer(order);
                    }else {
                        choice();                               // 显示选择题模式
                        showWordsInformation(order);           //显示单词信息
                        showWordOption(order);                 //显示ABCD选项
                    }
                }else {
                    reduceWordAsterisk(order);              //答错 该 单词，星号归零
                    Log.e("答错了,单词的星号是;",""+getWordAsterisk(order)+"单词位置"+order+"该单词"+studyWords.get(order).getWord());
                    showWordsInformation(order);           // 显示单词信息
                    incognizance();                         //用户选择错误，显示单词词义
                    showWordAnswer(order);                  //显示单词答案
                }
                break;
            case R.id.tv_B:                                //点击B选项
                String B_option = holder.tv_B.getText().toString().trim();//获取B选项词义
                Log.e("用户选择的是",""+B_option);
                if (check(B_option)){                        //判断是否选择正确
                    Log.e("该单词是：",""+studyWords.get(order).getWord());
                    addWordAsterisk(order);                //答对 该 单词，星号加1
                    isShowLearnWord();                  //判断单词下标是否大于19，然后进行一次循环
                    if (studyWords.get(order).getAsterisk()==1||studyWords.get(order).getAsterisk()==3){
                        memory();
                        setProgress();
                        showWordAnswer(order);
                    }else {
                        choice();                               // 显示选择题模式
                        showWordsInformation(order);           //显示单词信息
                        showWordOption(order);                 //显示ABCD选项
                    }
                }else {
                    reduceWordAsterisk(order);              //答错 该 单词，星号归零
                    Log.e("答错了,单词的星号是;",""+getWordAsterisk(order)+"单词位置"+order+"该单词"+studyWords.get(order).getWord());
                    showWordsInformation(order);           // 显示单词信息
                    incognizance();                         //用户选择错误，显示单词词义
                    showWordAnswer(order);                  //显示单词答案
                }
                break;
            case R.id.tv_C:                                //点击B选项
                String C_option = holder.tv_C.getText().toString().trim();//获取B选项词义
                Log.e("用户选择的是",""+C_option);
                if (check(C_option)){                         //判断是否选择正确
                    Log.e("该单词是：",""+studyWords.get(order).getWord());
                    addWordAsterisk(order);                //答对 该 单词，星号加1
                    isShowLearnWord();                  //判断单词下标是否大于19，然后进行一次循环
                    if (studyWords.get(order).getAsterisk()==1||studyWords.get(order).getAsterisk()==3){
                        memory();
                        setProgress();
                        showWordAnswer(order);
                    }else {
                        choice();                               // 显示选择题模式
                        showWordsInformation(order);           //显示单词信息
                        showWordOption(order);                 //显示ABCD选项
                    }
                }else {
                    reduceWordAsterisk(order);              //答错 该 单词，星号归零
                    Log.e("答错了,单词的星号是;",""+getWordAsterisk(order)+"单词位置"+order+"该单词"+studyWords.get(order).getWord());
                    showWordsInformation(order);           // 显示单词信息
                    incognizance();                         //用户选择错误，显示单词词义
                    showWordAnswer(order);                  //显示单词答案
                }
                break;
            case R.id.tv_D:                                //点击B选项
                String D_option = holder.tv_D.getText().toString().trim();//获取B选项词义
                Log.e("用户选择的是",""+D_option);
                if (check(D_option)){                        //判断是否选择正确
                    Log.e("该单词是：",""+studyWords.get(order).getWord());
                    addWordAsterisk(order);                //答对 该 单词，星号加1
                    isShowLearnWord();                  //判断单词下标是否大于19，然后进行一次循环
                    if (studyWords.get(order).getAsterisk()==1||studyWords.get(order).getAsterisk()==3){
                        memory();
                        setProgress();
                        showWordAnswer(order);
                    }else {
                        choice();                               // 显示选择题模式
                        showWordsInformation(order);           //显示单词信息
                        showWordOption(order);                 //显示ABCD选项
                    }
                }else {
                    reduceWordAsterisk(order);              //答错 该 单词，星号归零
                    showWordsInformation(order);           // 显示单词信息
                    incognizance();                         //用户选择错误，显示单词词义
                    showWordAnswer(order);                  //显示单词答案
                }
                break;
            case R.id.tv_back:
                AlertDialog.Builder mLoadingBuilder=new AlertDialog.Builder(this);
                AlertDialog mLoadingAlertDialog = mLoadingBuilder.create();
                View mLoadingView = getLayoutInflater().inflate(R.layout.dialog_save_loading, null);
                mLoadingAlertDialog.setView(mLoadingView);
                mLoadingAlertDialog.show();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
                break;
            case R.id.tv_spell:
                //打开拼写界面
                Intent intent = new Intent(this, SpellTestActivity.class);
                intent.putExtra("Word", studyWords.get(order).getWord());
                intent.putExtra("Phonogram", studyWords.get(order).getSoundmark_american());
                intent.putExtra("BanckgroundIndex", backgroundNum);
                intent.putExtra("Mode", "spell");
                startActivity(intent);
                break;
            case R.id.tv_delete:
                //从学习单词表中删除单词，表示已经掌握
                Toast.makeText(LearnActivity.this, "点击删除按钮", Toast.LENGTH_LONG).show();
                break;
            case R.id.tv_incognizance_example:  //看例句
                if (holder.learn_sliding.getMenuState()) {
                    holder.learn_sliding.closeMenu();
                } else {
                    holder.learn_sliding.openMenu();
                }
                break;
            case R.id.learn_tv_changesound:     //点击音标按钮
                if (holder.learn_tv_changesound.getText().toString() == "US"){
                    holder.learn_tv_changesound.setText("UK");
                    holder.learn_tv_soundMark.setText(studyWords.get(order).getSoundmark_british());
                    MediaUtils.playWord(this,studyWords.get(order).getWord());
                }else {
                    holder.learn_tv_changesound.setText("US");
                    holder.learn_tv_soundMark.setText(studyWords.get(order).getSoundmark_american());
                    MediaUtils.playWord(this,studyWords.get(order).getWord());
                }
                break;
            default:
                break;
        }
    }

    /**
     * 答对单词星号加1
     * */
    private void addWordAsterisk(int order) {
        WordStudyDao wordStudyDao = new WordStudyDao(this);
        int asterisk = getAsterisk(order);
        if (asterisk>=0 && asterisk<=3){
            wordStudyDao.updateWordAsterisk(asterisk+1,studyWords.get(order).getWord());
        }
    }

    /**
     * 答错单词星号归零
     * */
    private void reduceWordAsterisk(int order) {
        WordStudyDao wordStudyDao = new WordStudyDao(this);
        int asterisk = getWordAsterisk(order);
        if (asterisk>=1 && asterisk<=3){
            wordStudyDao.updateWordAsterisk(0,studyWords.get(order).getWord());
        }
    }

    /**
     * 从数据库中获取单词的星号
     * */
    private int getWordAsterisk(int order){
        String word = studyWords.get(order).getWord();
        WordStudyDao wordStudyDao = new WordStudyDao(this);
        return wordStudyDao.getWordAsterisk(word);
    }

    /**
     * 显示 单词 各个信息
     * 包括：单词，音标，星号，已学习单词，剩余学习单词
     * */
    private void showWordsInformation(int order){
        holder.learn_tv_word = (TextView) holder.ll_show_word.findViewById(R.id.learn_tv_word);//获取要学习单词的控件
        holder.learn_tv_soundMark = (TextView) holder.ll_show_word.findViewById(R.id.learn_tv_soundmark);//获取音标控件

        Log.e("显示单词的下标",""+order);

        holder.learn_tv_word.setText(studyWords.get(order).getWord());        //显示要学习的单词
        MediaUtils.playWord(this,studyWords.get(order).getWord());          //播放当前单词
        holder.learn_tv_soundMark.setText(studyWords.get(order).getSoundmark_american());  //显示单词音标
        showAsterisk(getWordAsterisk(order));        //显示单词的星号
        showFinishWords();     //显示已学习单词，剩余学习单词
    }

    /**
     * 获取单词星号数量
     * */
    private int getAsterisk(int order){
        return studyWords.get(order).getAsterisk();
    }

    /**
     * 显示单词星号
     * */
    private void showAsterisk(int order){
        holder.asterisk_one = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_one);//获取星号1控件
        holder.asterisk_two = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_two);//获取星号2控件
        holder.asterisk_there = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_there);//获取星号3控件
        holder.asterisk_four = (ImageView) holder.ll_show_word.findViewById(R.id.asterisk_four);//获取星号4控件
        switch (order){
            case 0:
                holder.asterisk_one.setImageResource(R.mipmap.star_off);
                holder.asterisk_two.setImageResource(R.mipmap.star_off);
                holder.asterisk_there.setImageResource(R.mipmap.star_off);
                holder.asterisk_four.setImageResource(R.mipmap.star_off);
                break;
            case 1:
                holder.asterisk_one.setImageResource(R.mipmap.star_on);
                holder.asterisk_two.setImageResource(R.mipmap.star_off);
                holder.asterisk_there.setImageResource(R.mipmap.star_off);
                holder.asterisk_four.setImageResource(R.mipmap.star_off);
                break;
            case 2:
                holder.asterisk_one.setImageResource(R.mipmap.star_on);
                holder.asterisk_two.setImageResource(R.mipmap.star_on);
                holder.asterisk_there.setImageResource(R.mipmap.star_off);
                holder.asterisk_four.setImageResource(R.mipmap.star_off);
                break;
            case 3:
                holder.asterisk_one.setImageResource(R.mipmap.star_on);
                holder.asterisk_two.setImageResource(R.mipmap.star_on);
                holder.asterisk_there.setImageResource(R.mipmap.star_on);
                holder.asterisk_four.setImageResource(R.mipmap.star_off);
                break;
            case 4:
                holder.asterisk_one.setImageResource(R.mipmap.star_on);
                holder.asterisk_two.setImageResource(R.mipmap.star_on);
                holder.asterisk_there.setImageResource(R.mipmap.star_on);
                holder.asterisk_four.setImageResource(R.mipmap.star_on);
                break;
            default:
                break;
        }
    }

    /**
     * 显示已学习单词，剩余学习单词
     * 用for循环判断单词的星号是否为4个
     * 如果为4，左上角就加上1
     * 右上角就减1
     * （同时，单词的星号为4，就修改Book的 word_is_study 字段变成1）
     * */
    private void showFinishWords(){
        holder.completed_words = (TextView)holder.ll_show_word.findViewById(R.id.tv_complete);
        holder.no_completed_words = (TextView)holder.ll_show_word.findViewById(R.id.tv_need_complete);
        int completedNum = 0;
        for (int i=0;i<studyWords.size();i++){
            if (getWordAsterisk(i)==4){
                setWord_is_study(studyWords.get(i).getWord());//修改Book里面的 word_is_study 字段
                completedNum++;
            }
        }
        holder.completed_words.setText(completedNum+"");
        holder.no_completed_words.setText((20-completedNum)+"");
    }

    /**
     * 如果单词的星号字段等于4
     * 就修改Book里该单词的 word_is_study 字段变成 1
     * 表示已学习
     * */
    private void setWord_is_study(String word){
        BookDao bookDao = new BookDao(this);
        bookDao.updateWord_is_study(word);
    }

    /**
     * 显示 ABCD 四个选项（随机的）
     * */
    private void showWordOption(int order){
        int[] ints = NumUtil.random3(studyWords.size(), order);
        int answer_right = NumUtil.random(4);
        holder.tv_A = (TextView) holder.ll_abcd.findViewById(R.id.tv_A); //获取选项A控件
        holder.tv_B = (TextView) holder.ll_abcd.findViewById(R.id.tv_B);//获取选项B控件
        holder.tv_C = (TextView) holder.ll_abcd.findViewById(R.id.tv_C);//获取选项C控件
        holder.tv_D = (TextView) holder.ll_abcd.findViewById(R.id.tv_D);//获取选项D控件
        //动态显示选项
        if (answer_right == 0) {
            holder.tv_A.setText(studyWords.get(order).getAnswer_right());
            holder.tv_B.setText(studyWords.get(ints[0]).getAnswer_right());
            holder.tv_C.setText(studyWords.get(ints[1]).getAnswer_right());
            holder.tv_D.setText(studyWords.get(ints[2]).getAnswer_right());
        } else if (answer_right == 1) {
            holder.tv_B.setText(studyWords.get(order).getAnswer_right());
            holder.tv_A.setText(studyWords.get(ints[0]).getAnswer_right());
            holder.tv_C.setText(studyWords.get(ints[1]).getAnswer_right());
            holder.tv_D.setText(studyWords.get(ints[2]).getAnswer_right());
        } else if (answer_right == 2) {
            holder.tv_C.setText(studyWords.get(order).getAnswer_right());
            holder.tv_B.setText(studyWords.get(ints[0]).getAnswer_right());
            holder.tv_A.setText(studyWords.get(ints[1]).getAnswer_right());
            holder.tv_D.setText(studyWords.get(ints[2]).getAnswer_right());
        } else if (answer_right == 3) {
            holder.tv_D.setText(studyWords.get(order).getAnswer_right());
            holder.tv_B.setText(studyWords.get(ints[0]).getAnswer_right());
            holder.tv_C.setText(studyWords.get(ints[1]).getAnswer_right());
            holder.tv_A.setText(studyWords.get(ints[2]).getAnswer_right());
        }
    }

    /**
     * 判断用户所选的答案是否正确
     * */
    private boolean check(String word){
        String answer = studyWords.get(order).getAnswer_right();
        Log.e("当前单词答案：",""+answer);
        if(word.equals(answer)){
            Log.e("判断","是正确的");
            return true;
        }
        Log.e("判断","是错误的");
        return false;
    }

    /**
     * 显示单词的答案
     * */
    private void showWordAnswer(int order) {
        holder.tv_word_information = (TextView) holder.ll_information.findViewById(R.id.tv_word_information);
        holder.tv_word_information.setText(studyWords.get(order).getAnswer_right());
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

    /**
     * 显示 不认识，和 ABCD选项
     * */
    private void choice(){
        holder.ll_choice.setVisibility(View.VISIBLE);
        holder.ll_abcd.setVisibility(View.VISIBLE);

        holder.ll_incognizance.setVisibility(View.INVISIBLE);
        holder.ll_information.setVisibility(View.INVISIBLE);

        holder.ll_memory.setVisibility(View.INVISIBLE);
    }
    /**
     * 显示下一个，看例句
     * 单词含义
     * */
    private void incognizance(){
        holder.ll_choice.setVisibility(View.INVISIBLE);
        holder.ll_abcd.setVisibility(View.INVISIBLE);

        holder.ll_incognizance.setVisibility(View.VISIBLE);//显示下一个，看例句
        holder.ll_information.setVisibility(View.VISIBLE);//显示单词答案

        holder.ll_memory.setVisibility(View.INVISIBLE);
    }
    /**
     * 显示认识，不认识
     *  转圈
     * 单词词义
     * */
    private void memory(){
        holder.ll_choice.setVisibility(View.INVISIBLE);
        holder.ll_abcd.setVisibility(View.INVISIBLE);

        holder.ll_incognizance.setVisibility(View.INVISIBLE);

        holder.ll_memory.setVisibility(View.VISIBLE); // 显示 认识  不认识
        holder.ll_information.setVisibility(View.VISIBLE);// 先显示 转圈， 转完后显示词义
        holder.progress.setVisibility(View.VISIBLE);//显示 转圈
        holder.tv_word_information.setVisibility(View.INVISIBLE);//隐藏单词含义
    }

    /**
     * 显示下一个单词的时候
     * 进行一次判断，如果单词下标为19
     * */
    private void isShowLearnWord(){
        Log.e("进入isShowLearnWord判断","order为："+order);
        if (order >= studyWords.size()-1){
            Log.e("是否进入大于20","order为："+order);
            order = 0;
            while (studyWords.get(order).getAsterisk() == 4){
                order ++;
                if (order == (studyWords.size()-1)){
                    Log.e("20个单词已学习完","20个单词已学习完");
                    break;
                }
            }
        }else {
            order++;
        }
    }

    /**
     * 判断单词的星号是否为4
     * 如果为4，则不显示
     * 显示下一个单词
     * */
    private void isWordAsteriskFour(){
        while (getWordAsterisk(order) == 4){
            order ++;
        }
        if (studyWords.get(order).getAsterisk() == 1 || studyWords.get(order).getAsterisk() == 3){
            memory();
            setProgress();
            showWordAnswer(order);
        }else {
            choice();
            showWordOption(order);
        }
    }

    //发送handler消息倒数4秒显示单词信息
    private void setProgress() {
        msg = mHandler.obtainMessage();
        if (--num >= 0) {
            msg.what = num;
            mHandler.sendMessageDelayed(msg, 1000);
        }
    }

}
