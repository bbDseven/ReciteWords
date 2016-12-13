package recitewords.apj.com.recitewords.activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.bean.Book;
import recitewords.apj.com.recitewords.bean.WordReview;
import recitewords.apj.com.recitewords.db.dao.BookDao;
import recitewords.apj.com.recitewords.db.dao.LexiconDao;
import recitewords.apj.com.recitewords.db.dao.WordReviewDao;
import recitewords.apj.com.recitewords.fragment.ExampleSentenceFragment_review;
import recitewords.apj.com.recitewords.globle.Review;
import recitewords.apj.com.recitewords.util.DateUtil;
import recitewords.apj.com.recitewords.util.MediaUtils;
import recitewords.apj.com.recitewords.util.NumUtil;
import recitewords.apj.com.recitewords.util.PrefUtils;
import recitewords.apj.com.recitewords.util.UIUtil;
import recitewords.apj.com.recitewords.view.CircleProgressView;
import recitewords.apj.com.recitewords.view.SlidingUpMenu;


public class ReviewActivity extends BaseActivity implements View.OnClickListener,
        ViewTreeObserver.OnGlobalLayoutListener, SlidingUpMenu.OnToggleListener {
    //定义好的6张背景图id数组
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
    private String mWord = "abroad";  //显示的单词
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
    private float mModeScore = 0;  //模式切换的分值
    private int mGraspValues;   //熟悉程度
    private List<Integer> mGraspValuesList;  //熟悉程度集合
    private int mCircleIndex = 1;  //学习模式切换圈数


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 3:
                    //设置进度条进度
                    if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                        holder.progress.setProgress(25);
                        holder.fl_progress_click.setOnClickListener(ReviewActivity.this);
                        setProgress();
                    } else if (review_mode.equals(Mode.MODE_MEMORY_WORD)) {
                        holder.word_progress.setProgress(25);
                        holder.ll_word_progress.setOnClickListener(ReviewActivity.this);
                        setProgress();
                    }
                    break;
                case 2:

                    if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                        holder.progress.setProgress(50);
                        setProgress();
                    } else if (review_mode.equals(Mode.MODE_MEMORY_WORD)) {
                        holder.word_progress.setProgress(50);
                        setProgress();
                    }
                    break;
                case 1:
                    if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                        holder.progress.setProgress(75);
                        setProgress();
                    } else if (review_mode.equals(Mode.MODE_MEMORY_WORD)) {
                        holder.word_progress.setProgress(75);
                        setProgress();
                    }
                    break;
                case 0:
                    if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                        holder.progress.setProgress(100);
                        holder.fl_progress_click.setOnClickListener(null);
                        holder.progress.setVisibility(View.GONE);   //隐藏进度条
                        holder.ll_information.setVisibility(View.VISIBLE);  //显示单词信息
                    } else if (review_mode.equals(Mode.MODE_MEMORY_WORD)) {
                        holder.word_progress.setProgress(100);
                        holder.ll_word_progress.setOnClickListener(null);
                        holder.word_progress.setVisibility(View.GONE);   //隐藏进度条
                        holder.ll_word_root.setVisibility(View.VISIBLE);  //显示单词
                    }
                    num = 4;    //进度条显示完num重新设置回4秒
                    break;
                default:
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

        TextView tv_complete;  //头部显示已完成单词
        TextView tv_need_complete;  //头部显示未完成单词
        TextView learn_tv_word;  //单词
        TextView learn_tv_soundmark; //音标
        TextView learn_tv_changesound;  //切换音标按钮
        TextView tv_word_information;  //显示词性词义
        CircleProgressView word_progress;  //单词回忆模式--加载单词进度
        LinearLayout ll_word_progress;  //单词回忆模式--加载单词进度父布局
        LinearLayout ll_word_root;  //显示单词的父布局

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
        holder.word_progress = findViewByIds(R.id.word_progress);
        holder.ll_word_progress = findViewByIds(R.id.ll_word_progress);
        holder.ll_word_root = findViewByIds(R.id.ll_word_root);
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
        holder.tv_complete.setOnClickListener(this);
        holder.tv_need_complete.setOnClickListener(this);
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
        //获取复习表中本组复习的单词
        wordReviews = new ArrayList<>();
        wordReviews = wordReviewDao.queryAll(book_name);

        complete_review_word = getNeed_review_word();      //已复习单词
        need_review_word = wordReviews.size() - complete_review_word;     //未复习单词（20个剩下的）

        if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {  //回忆词义模式
            showNextWord();
            setProgress();        //设置进度条进度
        } else if (review_mode.equals(Mode.MODE_CHOICE)) {  //选择题模式
            showNextWord();
        } else if (review_mode.equals(Mode.MODE_MEMORY_WORD)) {  //单词回忆模式
            showNextWord();
            setProgress();
        }

        init_fragment(mWord);     //例句Fragment替换例句布局文件
    }

    //判断是否需要把从字库表中获取到需要复习的单词添加到复习表中
    public void getDataForBook() {
        wordReviewDao = new WordReviewDao(this);
        SharedPreferences pref = PrefUtils.getPref(this);
        review_is_complete = PrefUtils.getDBFlag(pref, "review_is_complete", true);
        if (review_is_complete) {
            BookDao bookDao = new BookDao(this);
            List<Book> books = bookDao.queryReviewWOrd(book_name);
            for (Book book : books) {
                //把从字库表中获取到需要复习的单词添加到复习表中
                wordReviewDao.addWord(book.getWord(), "", "", "", "", book.getSoundmark_american(),
                        book.getSoundmark_british(), book.getWord_mean(), "", 0,
                        book.getGrasp_values(), book.getDate(), book.getBook_name(), book.getUserID());
            }
            //当前复习的单词没有完成，暂时不需要重新获取复习单词
            PrefUtils.setDBFlag(pref, "review_is_complete", false);
        }
    }

    //获取已经完成的复习的单词数量
    public int getNeed_review_word() {
        mGraspValuesList = new ArrayList<>();
        completeIndex = new ArrayList<>();
        int mCompleteNum = 0;
        int i = 0;
        for (WordReview wr : wordReviews) {
            if (wr.getWord_is_review() == 1) {
                mCompleteNum++;
                completeIndex.add(i);
            }
            i++;
            mGraspValuesList.add(0);   //初始化晋级值
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

    //取消handler处理，并设置进度条为0，时间重置为4秒
    private void reset() {
        if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
            holder.progress.setProgress(0);
        } else if (review_mode.equals(Mode.MODE_MEMORY_WORD)) {
            holder.word_progress.setProgress(0);
        }
        mHandler.removeMessages(msg.what);
        num = 4;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_complete:
                UIUtil.toast(this, "复习完成" + complete_review_word + "个", 3000, Gravity.TOP, 0, 0, 16);
                startActivity(new Intent(this, SpellTestActivity.class));
                break;
            case R.id.tv_need_complete:
                UIUtil.toast(this, "本组剩余" + need_review_word + "个", 3000, Gravity.TOP, 0, 0, 16);
                break;
            case R.id.fl_progress_click:
                if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                    holder.progress.setVisibility(View.GONE);   //隐藏进度条
                    holder.ll_information.setVisibility(View.VISIBLE);  //显示单词信息
                }
                break;
            case R.id.ll_word_progress:
                holder.word_progress.setVisibility(View.GONE);   //隐藏进度条
                holder.ll_word_root.setVisibility(View.VISIBLE);  //显示单词
                break;
            case R.id.tv_know:   //认识
                if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                    mModeScore += 1;  //更改模式切换值
                    if (num < 4) {
                        mGraspValues += 100;  ///更改晋级
                    } else {
                        mGraspValues += 50;  ///更改晋级
                    }

                } else if (review_mode.equals(Mode.MODE_MEMORY_WORD)) {
                    mModeScore += 2;  //更改模式切换值
                    mGraspValues += 15;//更改晋级值
                }
                saveGraspValues();
                updateGrasp();
                //显示下一个单词
                reset();
                setProgress();
                showNextWord();
                break;
            case R.id.tv_dim:  //模糊
                holder.ll_memory.setVisibility(View.GONE);  //隐藏认识布局
                holder.ll_incognizance.setVisibility(View.VISIBLE); //显示不认识布局
                mModeScore += 1.5;  //更改模式切换值
                mGraspValues += 10;//更改晋级值
                if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {   //显示词义
                    holder.progress.setVisibility(View.GONE);   //隐藏进度条
                    holder.ll_information.setVisibility(View.VISIBLE);  //显示单词信息
                    reset();
                } else if (review_mode.equals(Mode.MODE_MEMORY_WORD)) {  //显示单词
                    holder.word_progress.setVisibility(View.INVISIBLE);  //隐藏进度条
                    holder.ll_word_root.setVisibility(View.VISIBLE);  //显示单词
                    reset();
                }
                saveGraspValues();
                updateGrasp();
                break;
            case R.id.tv_unknow:  //不认识
                //显示下一个词义
                holder.ll_memory.setVisibility(View.GONE);  //隐藏认识布局
                holder.ll_incognizance.setVisibility(View.VISIBLE); //显示不认识布局
                mGraspValues += 5;//更改晋级值
                if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                    mModeScore += 2;  //更改模式切换值
                    holder.progress.setVisibility(View.GONE);   //隐藏进度条
                    holder.ll_information.setVisibility(View.VISIBLE);  //显示单词信息
                    reset();
                } else if (review_mode.equals(Mode.MODE_MEMORY_WORD)) {
                    mModeScore += 1;  //更改模式切换值
                    holder.word_progress.setVisibility(View.INVISIBLE);  //隐藏进度条
                    holder.ll_word_root.setVisibility(View.VISIBLE);  //显示单词
                    reset();
                }
                saveGraspValues();
                updateGrasp();
                break;
            case R.id.tv_incognizance_next:  //不认识--》》下一个
                showNextWord();    //显示下一个单词
                //如果例句代开，则关闭例句
                if (holder.review_sliding.getMenuState()) {
                    holder.review_sliding.closeMenu();
                }
                if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                    reset();
                    setProgress();
                    holder.ll_information.setVisibility(View.GONE);  //隐藏单词信息
                    holder.progress.setVisibility(View.VISIBLE);   //显示进度条
                    holder.ll_memory.setVisibility(View.VISIBLE);  //显示记忆模式属下的中间布局
                    holder.ll_incognizance.setVisibility(View.GONE); //隐藏不认识的中间布局
                } else if (review_mode.equals(Mode.MODE_MEMORY_WORD)) {
                    holder.ll_word_root.setVisibility(View.INVISIBLE);  //隐藏单词
                    holder.word_progress.setVisibility(View.VISIBLE);//显示进度条
                    holder.ll_memory.setVisibility(View.VISIBLE);  //显示记忆模式属下的中间布局
                    holder.ll_incognizance.setVisibility(View.GONE); //隐藏不认识的中间布局
                    reset();
                    setProgress();
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
                mModeScore += 1;  //更改模式切换值
                mGraspValues += 5;//更改晋级值
                holder.ll_abcd.setVisibility(View.INVISIBLE);
                holder.ll_information.setVisibility(View.VISIBLE);
                holder.ll_choice.setVisibility(View.INVISIBLE);
                holder.ll_incognizance.setVisibility(View.VISIBLE);
                saveGraspValues();
                updateGrasp();
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
                Intent intent = new Intent(this, SpellTestActivity.class);
                intent.putExtra("Word", mWord);
                intent.putExtra("Phonogram", mPhonogram);
                intent.putExtra("BanckgroundIndex", backgroundNum);
                intent.putExtra("Mode", "spell");
                //学完一组单词后，拼写测试
//                intent.putExtra("BanckgroundIndex", backgroundNum);
//                intent.putExtra("words", (Serializable) wordReviews);
//                intent.putExtra("Mode", "spellTest");
                startActivity(intent);
                break;
            case R.id.tv_delete:
                //Toast.makeText(this, "点击了删除按钮，功能还没写", Toast.LENGTH_SHORT).show();
                if (complete_review_word<20){
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
                }
                break;
            default:
                break;
        }
    }

    /**
     * 显示下一个单词
     */
    public void showNextWord() {
        Log.e("ha", "当前单词：" + mWord + " 晋级值：" + mGraspValues + " 单词位置为: " + review_word_index +
                " mGraspValues: " + mGraspValues);

        if (completeIndex.size() == wordReviews.size()) {  //已完成学复习
            for (int i = 0; i < completeIndex.size(); i++) {
            }
            completeDialog();  //询问用户是否拼写测试
        }
        if (wordReviews.size() == review_word_index) {
            mCircleIndex++;   //单词循环圈加一
        }
        while (completeIndex.contains(review_word_index)) {  //已复习的不再出现
            review_word_index++;
        }
        if (review_word_index < wordReviews.size()) {  //还没有完成这一轮学习
            mWord = wordReviews.get(review_word_index).getWord(); //获取单词
            mPhonogram = wordReviews.get(review_word_index).getSoundmark_american();  //获取音标
            mWordMean = wordReviews.get(review_word_index).getAnswer_right();  //获取词性词义

            //根据复习单词的位置，取出他的晋级值
            mGraspValues = mGraspValuesList.get(review_word_index);
        } else {
            review_word_index = 0;
            while (completeIndex.contains(review_word_index)) {  //已复习的不再出现
                review_word_index++;
            }

            if (review_word_index < wordReviews.size()) {
                mWord = wordReviews.get(review_word_index).getWord(); //获取单词
                mPhonogram = wordReviews.get(review_word_index).getSoundmark_american();  //获取音标
                mWordMean = wordReviews.get(review_word_index).getAnswer_right();  //获取词性词义
                //根据复习单词的位置，取出他的晋级值
                mGraspValues = mGraspValuesList.get(review_word_index);
            }
        }
        changeReviewMode();  //切换复习模式
        holder.tv_complete.setText(complete_review_word + "");
        holder.tv_need_complete.setText(need_review_word + "");
        holder.learn_tv_word.setText(mWord);  //单词
        holder.learn_tv_soundmark.setText(mPhonogram);  //音标
        holder.tv_word_information.setText(mWordMean);  //设置单词详细信息
        review_word_index++;  //学习单词的位置增加1

        init_fragment(mWord);  //切换例句
    }

    /**
     * 保存单词的晋级值
     */
    public void saveGraspValues() {
        //保存当前单词的晋级值
        if (review_word_index <= wordReviews.size()) {
            mGraspValuesList.set(review_word_index - 1, mGraspValues);
        } else {
            Log.e(TAG, "下标越界了");
            Log.e("saveGraspValues", "下标值：" + (review_word_index - 1));
        }
    }

    /**
     * 更新数据库中单词的熟悉级别
     */
    public void updateGrasp() {
        //熟悉程度晋级，更新数据库
//        Log.e(TAG, "当前单词：" + mWord + " 熟悉程度晋级值：" + mGraspValues + " 模式:" + review_mode);
        if (mGraspValues >= 100) {
            //当前显示的单词已经掌握，不再在这次复习中出现，把下标位置添加到已掌握的集合中
            completeIndex.add(review_word_index - 1);
            BookDao bookDao = new BookDao(this);
            String mWordGrasp = bookDao.queryWordGrasp(book_name, mWord);   //单词熟悉程度
            //从复习表中标记为已复习,在这次复习中不再出现
            WordReviewDao dao = new WordReviewDao(this);
            dao.updateReviewState(mWord, book_name);
            //更新头部学习情况信息
            need_review_word--;
            complete_review_word++;

            if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
                if (mCircleIndex == 1) {
                    Log.e(TAG, "词义回想模式--晋级：C");
                    setDbGraspAndDate(mWordGrasp, Review.GRASP_C, bookDao);
                } else if (mCircleIndex == 2) {
                    Log.e(TAG, "词义回想模式--晋级：D");
                    setDbGraspAndDate(mWordGrasp, Review.GRASP_D, bookDao);
                } else if (mCircleIndex == 3) {
                    Log.e(TAG, "词义回想模式--晋级：E");
                    setDbGraspAndDate(mWordGrasp, Review.GRASP_E, bookDao);
                } else if (mCircleIndex >= 4) {
                    Log.e(TAG, "词义回想模式--晋级：F");
                    setDbGraspAndDate(mWordGrasp, Review.GRASP_F, bookDao);
                }
            } else if (review_mode.equals(Mode.MODE_CHOICE)) {
                if (mCircleIndex == 1) {
                    Log.e(TAG, "选择题模式--晋级：E");
                    setDbGraspAndDate(mWordGrasp, Review.GRASP_E, bookDao);
                } else if (mCircleIndex >= 2) {
                    Log.e(TAG, "选择题模式--晋级：F");
                    setDbGraspAndDate(mWordGrasp, Review.GRASP_F, bookDao);
                }

            } else if (review_mode.equals(Mode.MODE_MEMORY_WORD)) {
                if (mCircleIndex == 1) {
                    Log.e(TAG, "单词回想模式--晋级：E");
                    setDbGraspAndDate(mWordGrasp, Review.GRASP_E, bookDao);
                } else if (mCircleIndex >= 2) {
                    Log.e(TAG, "单词回想模式--晋级：F");
                    setDbGraspAndDate(mWordGrasp, Review.GRASP_F, bookDao);
                }
            }
        }

    }

    /**
     * 设置熟悉程度和日期
     *
     * @param mWordGrasp 数据库中熟悉程度
     * @param grasp      新的熟悉程度
     * @param bookDao    对象
     */
    public void setDbGraspAndDate(String mWordGrasp, String grasp, BookDao bookDao) {
        if (mWordGrasp.equals(Review.GRASP_A)) {
            bookDao.updateGraspWord(book_name, mWord);  //升一个等级级
        } else if (mWordGrasp.equals(Review.GRASP_B)) {
            bookDao.updateGraspValues(book_name, mWord, Review.GRASP_A);
        } else if (mWordGrasp.equals(Review.GRASP_C)) {
            bookDao.updateGraspValues(book_name, mWord, Review.GRASP_B);
        } else if (mWordGrasp.equals(Review.GRASP_D)) {
            bookDao.updateGraspValues(book_name, mWord, Review.GRASP_C);
        } else if (mWordGrasp.equals(Review.GRASP_E)) {
            bookDao.updateGraspValues(book_name, mWord, Review.GRASP_D);
        } else if (mWordGrasp.equals(Review.GRASP_F)) {
            bookDao.updateGraspValues(book_name, mWord, Review.GRASP_E);
        } else {
            bookDao.updateGraspValues(book_name, mWord, grasp);
        }
        bookDao.updateDate(book_name,mWord, DateUtil.getNowDate("yyyy-MM-dd"));  //更新日期
    }

    /**
     * 切换复习模式
     */
    public void changeReviewMode() {
        if (mModeScore >= 10) {
            if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) { //模式切换值达到10后,切换复习模式
                review_mode = Mode.MODE_CHOICE;
                reset();
            } else if (review_mode.equals(Mode.MODE_CHOICE)) {
                review_mode = Mode.MODE_MEMORY_WORD;
                setProgress();        //设置进度条进度
            } else if (review_mode.equals(Mode.MODE_MEMORY_WORD)) {
                review_mode = Mode.MODE_MEMORY_MEAN;
                setProgress();        //设置进度条进度
            }
            mModeScore = 0;  //模式切换值清零
        }

        //词义回忆模式
        if (review_mode.equals(Mode.MODE_MEMORY_MEAN)) {
            holder.ll_information.setVisibility(View.GONE);  //隐藏单词信息
            holder.progress.setVisibility(View.VISIBLE);  //显示加载进度条
            holder.ll_choice.setVisibility(View.INVISIBLE);  //隐藏下一个按钮
            holder.ll_memory.setVisibility(View.VISIBLE);  //显示回忆模式下的中间选项
            holder.ll_abcd.setVisibility(View.INVISIBLE);        //隐藏abcd选项模式
            holder.word_progress.setVisibility(View.INVISIBLE);  //隐藏单词加载进度
            holder.ll_word_root.setVisibility(View.VISIBLE);
        } else if (review_mode.equals(Mode.MODE_CHOICE)) {
            holder.ll_information.setVisibility(View.INVISIBLE);  //隐藏词义
            holder.progress.setVisibility(View.INVISIBLE);  //隐藏加载进度条
            holder.ll_abcd.setVisibility(View.VISIBLE);  //显示选项
            holder.ll_word_root.setVisibility(View.VISIBLE);  //显示单词
            holder.ll_memory.setVisibility(View.INVISIBLE);  //隐藏回忆模式下的中间选项
            holder.ll_incognizance.setVisibility(View.INVISIBLE);  //隐藏不认识下的选项
            holder.ll_choice.setVisibility(View.VISIBLE);  //显示下一个按钮
            holder.word_progress.setVisibility(View.INVISIBLE);  //隐藏单词加载进度

            int[] ints = NumUtil.random3(wordReviews.size(), review_word_index);
            answer_right = NumUtil.random(4);

            //动态显示选项
            if (answer_right == 0 && review_word_index < wordReviews.size()) {
                holder.tv_A.setText(wordReviews.get(review_word_index).getAnswer_right());
                holder.tv_B.setText(wordReviews.get(ints[0]).getAnswer_right());
                holder.tv_C.setText(wordReviews.get(ints[1]).getAnswer_right());
                holder.tv_D.setText(wordReviews.get(ints[2]).getAnswer_right());
            } else if (answer_right == 1 && review_word_index < wordReviews.size()) {
                holder.tv_B.setText(wordReviews.get(review_word_index).getAnswer_right());
                holder.tv_A.setText(wordReviews.get(ints[0]).getAnswer_right());
                holder.tv_C.setText(wordReviews.get(ints[1]).getAnswer_right());
                holder.tv_D.setText(wordReviews.get(ints[2]).getAnswer_right());
            } else if (answer_right == 2 && review_word_index < wordReviews.size()) {
                holder.tv_C.setText(wordReviews.get(review_word_index).getAnswer_right());
                holder.tv_B.setText(wordReviews.get(ints[0]).getAnswer_right());
                holder.tv_A.setText(wordReviews.get(ints[1]).getAnswer_right());
                holder.tv_D.setText(wordReviews.get(ints[2]).getAnswer_right());
            } else if (answer_right == 3 && review_word_index < wordReviews.size()) {
                holder.tv_D.setText(wordReviews.get(review_word_index).getAnswer_right());
                holder.tv_B.setText(wordReviews.get(ints[0]).getAnswer_right());
                holder.tv_C.setText(wordReviews.get(ints[1]).getAnswer_right());
                holder.tv_A.setText(wordReviews.get(ints[2]).getAnswer_right());
            }
        } else if (review_mode.equals(Mode.MODE_MEMORY_WORD)) {  //单词回想模式
            holder.progress.setVisibility(View.INVISIBLE);  //显示加载进度条
            holder.ll_choice.setVisibility(View.INVISIBLE);  //隐藏下一个按钮
            holder.ll_memory.setVisibility(View.VISIBLE);  //显示回忆模式下的中间选项
            holder.ll_abcd.setVisibility(View.INVISIBLE);        //隐藏abcd选项模式
            holder.word_progress.setVisibility(View.VISIBLE);  //隐藏单词加载进度
            holder.ll_word_root.setVisibility(View.INVISIBLE);  //隐藏单词
            holder.ll_information.setVisibility(View.VISIBLE);  //显示单词词义
        }
    }

    /**
     * 完成本轮复习后，弹窗提示是否拼写测试
     */
    public void completeDialog() {
        holder.tv_complete.setText(wordReviews.size() + "");
        holder.tv_need_complete.setText(0 + "");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("恭喜你");
        builder.setMessage("你已经复习完了本次的任务，立即去拼写测试吧！");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LexiconDao lexiconDao = new LexiconDao(ReviewActivity.this);
                int graspWord = lexiconDao.getGraspWord(book_name);  //原已掌握单词
                int reviewWord = lexiconDao.getReviewWord(book_name);  //原需复习单词
                lexiconDao.updateGraspWord(graspWord + mGraspWord, book_name);  //更新已掌握单词
                lexiconDao.updateReviewWord(reviewWord - mGraspWord, book_name);  //更新需学习的单词数

                Intent intent = new Intent(ReviewActivity.this, SpellTestActivity.class);
                //学完一组单词后，拼写测试
                intent.putExtra("BanckgroundIndex", backgroundNum);
                intent.putExtra("words", (Serializable) wordReviews);
                intent.putExtra("Mode", "spellTest");
                startActivity(intent);
                finish();
            }
        });

        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LexiconDao lexiconDao = new LexiconDao(ReviewActivity.this);
                int graspWord = lexiconDao.getGraspWord(book_name);  //原已掌握单词
                int reviewWord = lexiconDao.getReviewWord(book_name);  //原需复习单词
                lexiconDao.updateGraspWord(graspWord + mGraspWord, book_name);  //更新已掌握单词
                lexiconDao.updateReviewWord(reviewWord - mGraspWord, book_name);  //更新需学习的单词数
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * 选择题模式下选择ABCD
     *
     * @param index 点击位置
     */
    public void choice(int index) {
        if (answer_right == index) {
            mModeScore += 2;  //更改模式切换值
            mGraspValues += 15;//更改晋级值
            saveGraspValues();
            updateGrasp();
            showNextWord();
        } else {
            mModeScore += 1;  //更改模式切换值
            mGraspValues += 5;//更改晋级值
            saveGraspValues();
            updateGrasp();
            holder.ll_abcd.setVisibility(View.INVISIBLE);
            holder.ll_information.setVisibility(View.VISIBLE);
            holder.ll_choice.setVisibility(View.INVISIBLE);
            holder.ll_incognizance.setVisibility(View.VISIBLE);
        }
    }


    //用Fragment替换帧布局来显示例句
    private void init_fragment(String word) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_example, new ExampleSentenceFragment_review(word), FRAGMENT_SENTENCE);
        transaction.commit();
    }

    //视图树的回调方法
    @Override
    public void onGlobalLayout() {
        int height = holder.ll_show_word.getHeight();  //获取例句显示的高度
        holder.fl_example.setMinimumHeight(height);
        holder.ll_show_word.getViewTreeObserver().removeOnGlobalLayoutListener(this);//取消视图树监听
    }


    //监听例句显示状态
    @Override
    public void onToggleChange(SlidingUpMenu view,boolean oldMenuState, boolean isOpen) {

        if (mOnToggleListener != null) {
            mOnToggleListener.onmToggleChange(view,oldMenuState, isOpen);
        }
    }

    public void setmOnToggleListener(OnmToggleListener listener) {
        this.mOnToggleListener = listener;
    }

    public interface OnmToggleListener {
        void onmToggleChange(SlidingUpMenu view,boolean oldMenuState, boolean isOpen);
    }


    @Override
    public void onBackPressed() {
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
    }
}
