package recitewords.apj.com.recitewords.activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.bean.Book;
import recitewords.apj.com.recitewords.bean.WordReview;
import recitewords.apj.com.recitewords.db.dao.BookDao;
import recitewords.apj.com.recitewords.db.dao.LexiconDao;
import recitewords.apj.com.recitewords.db.dao.WordReviewDao;
import recitewords.apj.com.recitewords.fragment.ExampleSentenceFragment;
import recitewords.apj.com.recitewords.fragment.ExampleSentenceFragment_review;
import recitewords.apj.com.recitewords.util.MediaUtils;
import recitewords.apj.com.recitewords.util.NumUtil;
import recitewords.apj.com.recitewords.util.PrefUtils;
import recitewords.apj.com.recitewords.util.UIUtil;
import recitewords.apj.com.recitewords.view.CircleProgressView;
import recitewords.apj.com.recitewords.view.SlidingUpMenu;

public class ReviewActivity extends BaseActivity implements View.OnClickListener,
        ViewTreeObserver.OnGlobalLayoutListener, SlidingUpMenu.OnToggleListener, TextWatcher {
    //定义好的8张背景图id数组
    private int[] images = new int[]{R.mipmap.haixin_bg_dim_01, R.mipmap.haixin_bg_dim_02,
            R.mipmap.haixin_bg_dim_03, R.mipmap.haixin_bg_dim_04,
            R.mipmap.haixin_bg_dim_05, R.mipmap.haixin_bg_dim_06};
    private int backgroundNum;
    private final String TAG = "ReviewActivity";
    private ViewHolder holder;
    private int num = 4; //定义4秒
    private Message msg;
    private final String FRAGMENT_SENTENCE = "fragment_sentence_review";
    private OnmToggleListener mOnToggleListener;   //监听例句显示状态
    private AlertDialog alertDialog;  //评写界面弹窗
    private boolean havaing_comfirm = false;  //拼写确认比较单词
    private String mWord = "target";  //显示的单词
    private String book_name = "CET4";  //词书名字
    private String mPhonogram = "[ 'shabi: ]";  //显示的单词的音标
    private String mWordMean;  //单词词性词义
    private boolean is_US_Phonogram = true;  //英式音标或者美式音标
    private int complete_review_word;  //已完成复习的单词个数
    private List<Integer> completeIndex;  //已完成复习的单词的位置
    private int need_review_word;  //需要复习的单词个数
    private boolean review_is_complete;   //是否已经完成了本次20个单词的复习
    private int review_word_index = 0;  //当前学习单词位置
    private String review_mode = Mode.MODE_MEMORY_MEAN;  //选择题模式
    private WordReviewDao wordReviewDao;
    private List<WordReview> wordReviews;  //当前复习的20个单词
    private int answer_right;  //正确答案的位置
    private int mGraspWord = 0;  //这次学习中，掌握的的单词


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
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

    //复习模式
    public static class Mode {
        //复习模式,三种模式，选择题模式（Choice）,词义回想模式（Memory_Mean）,单词回想模式（Memory_word）
        static String MODE_CHOICE = "choice";
        static String MODE_MEMORY_MEAN = "memory_mean";
        static String MODE_MEMORY_WORD = "memory_word";
    }

    private class ViewHolder {
        RelativeLayout rl_review;   //复习页面根布局
        SlidingUpMenu review_sliding;   //sliding
        LinearLayout ll_information;    //展示单词信息布局
        LinearLayout ll_abcd;   //展示单词选项布局
        LinearLayout ll_star;   //学习页星星的布局
        CircleProgressView progress; //圆形进度条
        FrameLayout fl_progress_click;  //圆形进度条的点击
        LinearLayout ll_memory;     //认识的父布局
        LinearLayout ll_incognizance;   //看例句的父布局
        LinearLayout ll_choice;  //下一个的父布局
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

        TextView spell_tv_close;  //拼写关闭
        RelativeLayout spell_rl_root;  //拼写根布局
        EditText spell_et_input;  //拼写用户输入
        TextView spell_tv_correct;  //拼写显示正确单词
        TextView spell_tv_confirm;  //拼写确认单词
        TextView spell_tv_prompt;  //拼写单词提示
        RelativeLayout spell_rl_back;  //评写界面黑色背景
        RelativeLayout spell_rl_bottom;  //评写界面底部背景

        TextView tv_complete;  //头部显示已完成单词
        TextView tv_need_complete;  //头部显示未完成单词
        TextView learn_tv_word;  //单词
        TextView learn_tv_soundmark; //音标
        TextView learn_tv_changesound;  //切换音标按钮
        TextView tv_word_information;  //显示词性词义

        TextView tv_A;  //A选项
        TextView tv_B;  //B选项
        TextView tv_C;  //C选项
        TextView tv_D;  //D选项
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        init_view();
        init_event();
        init_data();
    }

    private void init_view() {
        holder = new ViewHolder();
        holder.rl_review = findViewByIds(R.id.activity_review);
        holder.review_sliding = findViewByIds(R.id.review_sliding);
        holder.ll_information = findViewByIds(R.id.ll_information);
        holder.ll_abcd = findViewByIds(R.id.ll_abcd);
        holder.ll_star = findViewByIds(R.id.ll_star);
        holder.progress = findViewByIds(R.id.review_progress);
        holder.fl_progress_click = findViewByIds(R.id.fl_progress_click);
        holder.ll_memory = findViewByIds(R.id.ll_memory);
        holder.ll_incognizance = findViewByIds(R.id.ll_incognizance);
        holder.ll_choice = findViewByIds(R.id.ll_choice);
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
        holder.tv_complete = findViewByIds(R.id.tv_complete);
        holder.tv_need_complete = findViewByIds(R.id.tv_need_complete);
        holder.learn_tv_word = findViewByIds(R.id.learn_tv_word);
        holder.learn_tv_soundmark = findViewByIds(R.id.learn_tv_soundmark);
        holder.learn_tv_changesound = findViewByIds(R.id.learn_tv_changesound);
        holder.tv_word_information = findViewByIds(R.id.tv_word_information);
        holder.tv_A = findViewByIds(R.id.tv_A);
        holder.tv_B = findViewByIds(R.id.tv_B);
        holder.tv_C = findViewByIds(R.id.tv_C);
        holder.tv_D = findViewByIds(R.id.tv_D);

    }

    private void init_event() {
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
        holder.review_sliding.setOnToggleListener(this);
        holder.learn_tv_changesound.setOnClickListener(this);
        holder.ll_choice.setOnClickListener(this);
        holder.tv_A.setOnClickListener(this);
        holder.tv_B.setOnClickListener(this);
        holder.tv_C.setOnClickListener(this);
        holder.tv_D.setOnClickListener(this);
    }

    private void init_data() {
        Intent intent = getIntent();
        backgroundNum = intent.getIntExtra("backgroundNum", 0);
        holder.rl_review.setBackgroundResource(images[backgroundNum]);//获取随机数设置复习页面背景图片
        holder.review_sliding.getBackground().setAlpha(170);        //设置SlidingUpMenu的透明度
        holder.fl_example.getBackground().setAlpha(70);  //更改例句界面透明度
        holder.ll_star.setVisibility(View.GONE);        //隐藏学习页的星星

        wordReviewDao = new WordReviewDao(this);
        getDataForBook();  //从词书表获取数据插入复习表中
        //获取复习表中单词
        wordReviews = wordReviewDao.queryAll(book_name);

        complete_review_word = getNeed_review_word();      //已复习单词
        need_review_word = wordReviews.size() - complete_review_word;     //未复习单词（20个剩下的）

        if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {  //回忆词义模式
            showNextWord();
            setProgress();        //设置进度条进度
        } else if (review_mode.equals(Mode.MODE_CHOICE)) {  //选择题模式
            showNextWord();
        } else if (review_mode.equals(Mode.MODE_MEMORY_WORD)) {  //单词回忆模式

        }

        init_fragment();     //例句Fragment替换例句布局文件
    }

    //判断是否需要把从字库表中获取到需要复习的单词添加到复习表中
    public void getDataForBook() {
        wordReviewDao = new WordReviewDao(this);
        SharedPreferences pref = PrefUtils.getPref(this);
        review_is_complete = PrefUtils.getDBFlag(pref, "review_is_complete", true);
        if (review_is_complete) {
            BookDao bookDao = new BookDao(this);
            List<Book> books = bookDao.queryReviewWOrd();
            for (Book book : books) {
                //把从字库表中获取到需要复习的单词添加到复习表中
                wordReviewDao.addWord(book.getWord(), "", "", "", "", book.getSoundmark_american(),
                        book.getSoundmark_british(), book.getWord_mean(), "", 0, "",
                        book.getBook_name(), book.getUserID());
            }
            //当前复习的单词没有完成，暂时不需要重新获取复习单词
            PrefUtils.setDBFlag(pref, "review_is_complete", false);
        }
    }

    //获取已经完成的复习的单词数量
    public int getNeed_review_word() {
        completeIndex = new ArrayList<>();
        int mCompleteNum = 0;
        int i = 0;
        for (WordReview wr : wordReviews) {
            if (wr.getWord_is_review() == 1) {
                mCompleteNum++;
                completeIndex.add(i);
            }
            i++;
        }
        return mCompleteNum;
    }

    //发送handler消息倒数4秒显示单词信息
    private void setProgress() {
        msg = mHandler.obtainMessage();
        if (--num >= 0) {
            msg.what = num;
            mHandler.sendMessageDelayed(msg, 1000);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_progress_click:
                if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                    holder.progress.setVisibility(View.GONE);   //隐藏进度条
                    holder.ll_information.setVisibility(View.VISIBLE);  //显示单词信息
                }
                break;
            case R.id.tv_know:   //认识
                //当前显示的单词已经掌握，不再在这次复习中出现，把下标位置添加到已掌握的集合中
                completeIndex.add(review_word_index - 1);
                if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                    if (num < 4) {
                        //更新词书表为已掌握
//                        BookDao bookDao = new BookDao(this);
//                         bookDao.updateGraspWord(book_name,mWord);

                        //从复习表中标记为已复习,在这次复习中不再出现
                        WordReviewDao dao = new WordReviewDao(this);
                        int i = dao.updateReviewState(mWord, book_name);
                        //更新头部学习情况信息
                        need_review_word--;
                        complete_review_word++;
                    }
                    //显示下一个单词
                    showNextWord();
                    if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                        holder.progress.setVisibility(View.VISIBLE);   //显示进度条
                        holder.ll_information.setVisibility(View.GONE);  //隐藏单词信息
                        reset();
                        setProgress();
                    }
                }
                break;
            case R.id.tv_dim:  //模糊
                //显示下一个单词
                showNextWord();
                if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                    holder.progress.setVisibility(View.GONE);   //隐藏进度条
                    holder.ll_information.setVisibility(View.VISIBLE);  //显示单词信息
                    holder.ll_memory.setVisibility(View.GONE);  //隐藏认识布局
                    holder.ll_incognizance.setVisibility(View.VISIBLE); //显示例句布局
                    reset();
                }
                break;
            case R.id.tv_unknow:  //不认识
                //显示下一个单词
                showNextWord();
                if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                    holder.progress.setVisibility(View.GONE);   //隐藏进度条
                    holder.ll_information.setVisibility(View.VISIBLE);  //显示单词信息
                    holder.ll_memory.setVisibility(View.GONE);  //隐藏认识布局
                    holder.ll_incognizance.setVisibility(View.VISIBLE); //显示例句布局
                    reset();
                }
                break;
            case R.id.tv_incognizance_next:  //不认识--》》下一个
                showNextWord();
                if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                    //显示下一个单词
                    if (holder.review_sliding.getMenuState()) {
                        holder.review_sliding.closeMenu();
                    }
                    reset();
                    setProgress();
                    holder.ll_memory.setVisibility(View.VISIBLE);  //隐藏认识布局
                    holder.ll_incognizance.setVisibility(View.GONE); //显示例句布局
                    holder.ll_information.setVisibility(View.GONE);  //隐藏单词信息
                    holder.progress.setVisibility(View.VISIBLE);   //显示进度条
                }
                break;
            case R.id.tv_incognizance_example:  //看例句
                if (holder.review_sliding.getMenuState()) {
                    holder.review_sliding.closeMenu();
                } else {
                    holder.review_sliding.openMenu();
                }
                break;
            case R.id.ll_choice:  //选择题模式--》》不认识
                showNextWord();
                break;

            case R.id.tv_A:
                choice(0);
                break;
            case R.id.tv_B:
                choice(1);
                break;
            case R.id.tv_C:
                choice(2);
                break;
            case R.id.tv_D:
                choice(3);
                break;
            case R.id.tv_back:
                //制造假的保存数据
                AlertDialog.Builder mLoadingBuilder = new AlertDialog.Builder(this);
                final AlertDialog mLoadingAlertDialog = mLoadingBuilder.create();
                View mLoadingView = getLayoutInflater().inflate(R.layout.dialog_save_loading, null);
                mLoadingAlertDialog.setView(mLoadingView);
                mLoadingAlertDialog.show();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LexiconDao lexiconDao = new LexiconDao(ReviewActivity.this);
                        int graspWord = lexiconDao.getGraspWord(book_name);  //原已掌握单词
                        int reviewWord = lexiconDao.getReviewWord(book_name);  //原需复习单词

                        lexiconDao.updateGraspWord(graspWord + mGraspWord, book_name);  //更新已掌握单词
                        lexiconDao.updateReviewWord(reviewWord - mGraspWord, book_name);  //更新需学习的单词数

                        mLoadingAlertDialog.dismiss();
                        finish();
                    }
                }, 1000);
                break;
            case R.id.learn_tv_changesound:  //切换音标
                if (is_US_Phonogram) {//英式
                    mPhonogram = wordReviews.get(review_word_index - 1).getSoundmark_british();
                    holder.learn_tv_soundmark.setText(mPhonogram);
                    holder.learn_tv_changesound.setText("US");
                    MediaUtils.playWord(this, mWord);  //读单词
                    is_US_Phonogram = false;
                } else {//美式
                    mPhonogram = wordReviews.get(review_word_index - 1).getSoundmark_american();
                    holder.learn_tv_soundmark.setText(mPhonogram);
                    holder.learn_tv_changesound.setText("UK");
                    MediaUtils.playWord(this, mWord);  //读单词
                    is_US_Phonogram = true;
                }
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

                //底部暗灰色导航条，使背景模糊化
                holder.spell_rl_bottom.getBackground().setAlpha(170);

                holder.spell_tv_close.setOnClickListener(this);  //监听拼写关闭按钮
                holder.spell_tv_prompt.setOnClickListener(this);  //监听评写提示按钮
                holder.spell_tv_confirm.setOnClickListener(this);  //监听评写确认按钮
                holder.spell_et_input.addTextChangedListener(this);  //监听文本框内容变化
                this.alertDialog.show();
                break;
            case R.id.tv_delete:
                //Toast.makeText(this, "点击了删除按钮，功能还没写", Toast.LENGTH_SHORT).show();
                mGraspWord++;
                completeIndex.add(review_word_index - 1);  //增加到已复习中
                //从复习表中标记为已复习,在这次复习中不再出现
                WordReviewDao dao = new WordReviewDao(this);
                int i = dao.updateReviewState(mWord, book_name);
                BookDao bookDao = new BookDao(this);  //更新词书表，已掌握
                bookDao.updateGraspWord(book_name, mWord);

                //更新头部学习情况信息
                need_review_word--;
                complete_review_word++;
                //显示下一个单词
                showNextWord();
                if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                    holder.progress.setVisibility(View.VISIBLE);   //显示进度条
                    holder.ll_information.setVisibility(View.GONE);  //隐藏单词信息
                    reset();
                    setProgress();
                }


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
                int time = 3000;  //提示时间3秒
                //自定义土司，设置土司显示位置
                UIUtil.toast(this, mPhonogram, time, Gravity.BOTTOM, 0, height / 2 - 20);
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
        }
    }

    /**
     * 显示下一个单词
     */
    public void showNextWord() {
        getWordData();  //重新获取数据
        holder.tv_complete.setText(complete_review_word + "");
        holder.tv_need_complete.setText(need_review_word + "");
        holder.learn_tv_word.setText(mWord);  //单词
        holder.learn_tv_soundmark.setText(mPhonogram);  //音标
        holder.tv_word_information.setText(mWordMean);  //设置单词详细信息
        //词义回忆模式
        if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
            holder.progress.setVisibility(View.VISIBLE);  //显示加载进度条
            holder.ll_choice.setVisibility(View.INVISIBLE);  //隐藏下一个按钮
            holder.ll_memory.setVisibility(View.VISIBLE);  //显示回忆模式下的中间选项
            holder.ll_abcd.setVisibility(View.INVISIBLE);        //隐藏abcd选项模式
        } else if (review_mode.equals(Mode.MODE_CHOICE)) {

            holder.ll_information.setVisibility(View.INVISIBLE);  //隐藏词义
            holder.progress.setVisibility(View.INVISIBLE);  //隐藏加载进度条
            holder.ll_abcd.setVisibility(View.VISIBLE);  //显示选项
            holder.ll_memory.setVisibility(View.INVISIBLE);  //隐藏回忆模式下的中间选项
            holder.ll_incognizance.setVisibility(View.INVISIBLE);  //隐藏不认识下的选项
            holder.ll_choice.setVisibility(View.VISIBLE);  //显示下一个按钮

            int[] ints = NumUtil.random3(wordReviews.size(), review_word_index);
            answer_right = NumUtil.random(4);

            //动态显示选项
            if (answer_right == 0) {
                holder.tv_A.setText(wordReviews.get(review_word_index).getAnswer_right());
                holder.tv_B.setText(wordReviews.get(ints[0]).getAnswer_right());
                holder.tv_C.setText(wordReviews.get(ints[1]).getAnswer_right());
                holder.tv_D.setText(wordReviews.get(ints[2]).getAnswer_right());
            } else if (answer_right == 1) {
                holder.tv_B.setText(wordReviews.get(review_word_index).getAnswer_right());
                holder.tv_A.setText(wordReviews.get(ints[0]).getAnswer_right());
                holder.tv_C.setText(wordReviews.get(ints[1]).getAnswer_right());
                holder.tv_D.setText(wordReviews.get(ints[2]).getAnswer_right());
            } else if (answer_right == 2) {
                holder.tv_C.setText(wordReviews.get(review_word_index).getAnswer_right());
                holder.tv_B.setText(wordReviews.get(ints[0]).getAnswer_right());
                holder.tv_A.setText(wordReviews.get(ints[1]).getAnswer_right());
                holder.tv_D.setText(wordReviews.get(ints[2]).getAnswer_right());
            } else if (answer_right == 3) {
                holder.tv_D.setText(wordReviews.get(review_word_index).getAnswer_right());
                holder.tv_B.setText(wordReviews.get(ints[0]).getAnswer_right());
                holder.tv_C.setText(wordReviews.get(ints[1]).getAnswer_right());
                holder.tv_A.setText(wordReviews.get(ints[2]).getAnswer_right());
            }
        }
        review_word_index++;  //学习单词的位置增加1
    }

    //获取单词详细信息
    public void getWordData() {

        while (completeIndex.contains(review_word_index)) {  //已复习的不再出现
            review_word_index++;
        }
        if (completeIndex.size() == 20) {  //已完成学复习
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("恭喜你");
            builder.setMessage("你已经复习完了本次的任务，赶快去玩吧");
            builder.setPositiveButton("确定", null);
            builder.show();
        } else if (review_word_index < wordReviews.size()) {  //还没有完成一轮学习
            mWord = wordReviews.get(review_word_index).getWord(); //获取单词
            mPhonogram = wordReviews.get(review_word_index).getSoundmark_american();  //获取音标
            mWordMean = wordReviews.get(review_word_index).getAnswer_right();  //获取词性词义
        } else {  //完成一轮学习，切换学习模式
            review_word_index = 0;
            while (completeIndex.contains(review_word_index)) {  //已复习的不再出现
                review_word_index++;
            }
            if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) { //选择完一篇后，改变复习模式
                review_mode = Mode.MODE_CHOICE;
                reset();
            } else if (review_mode.equals(Mode.MODE_CHOICE)) {
                review_mode = Mode.MODE_MEMORY_MEAN;
                setProgress();        //设置进度条进度
            }
            if (review_word_index < wordReviews.size()) {
                mWord = wordReviews.get(review_word_index).getWord(); //获取单词
                mPhonogram = wordReviews.get(review_word_index).getSoundmark_american();  //获取音标
                mWordMean = wordReviews.get(review_word_index).getAnswer_right();  //获取词性词义
            }

        }
    }

    /**
     * 选择题模式下选择ABCD
     *
     * @param index 点击位置
     */
    public void choice(int index) {
        if (answer_right == index) {
            showNextWord();
        } else {
            holder.ll_abcd.setVisibility(View.INVISIBLE);
            holder.ll_information.setVisibility(View.VISIBLE);
            holder.ll_choice.setVisibility(View.INVISIBLE);
            holder.ll_incognizance.setVisibility(View.VISIBLE);
        }
    }

    //取消handler处理，并设置进度条为0，时间重置为4秒
    private void reset() {
        holder.progress.setProgress(0);
        mHandler.removeMessages(msg.what);
        num = 4;
    }

    //用Fragment替换帧布局来显示例句
    private void init_fragment() {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_example, new ExampleSentenceFragment_review("abroad"), FRAGMENT_SENTENCE);
        transaction.commit();
    }

    //视图树的回调方法
    @Override
    public void onGlobalLayout() {
        int height = holder.ll_show_word.getHeight();  //获取例句显示的高度
        holder.fl_example.setMinimumHeight(height);
        holder.ll_show_word.getViewTreeObserver().removeOnGlobalLayoutListener(this);//取消视图树监听
    }


    String input = "";

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
            //隐藏正确单词
            holder.spell_tv_correct.setVisibility(View.INVISIBLE);
            //用户在比较单词后会改变输入文字颜色，在此处确保用户再次输入的文字为白色
            int color = Color.parseColor("#FFFFFF");
            holder.spell_et_input.setTextColor(color);
            holder.spell_et_input.setText(input);
            holder.spell_et_input.setSelection(input.length());  //设置光标位置
        }

    }


    //监听例句显示状态
    @Override
    public void onToggleChange(SlidingUpMenu view, boolean isOpen) {
        if (mOnToggleListener != null) {
            mOnToggleListener.onmToggleChange(view, isOpen);
        }
    }

    public void setmOnToggleListener(OnmToggleListener listener) {
        this.mOnToggleListener = listener;
    }

    public interface OnmToggleListener {
        void onmToggleChange(SlidingUpMenu view, boolean isOpen);
    }

}
