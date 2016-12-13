package recitewords.apj.com.recitewords.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.activity.LearnActivity;
import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.activity.ReviewActivity;
import recitewords.apj.com.recitewords.bean.Book;
import recitewords.apj.com.recitewords.bean.User;
import recitewords.apj.com.recitewords.bean.WordExampleSentence;
import recitewords.apj.com.recitewords.db.dao.BookDao;
import recitewords.apj.com.recitewords.db.dao.ExampleSentenceDao;
import recitewords.apj.com.recitewords.db.dao.LexiconDao;
import recitewords.apj.com.recitewords.db.dao.UserDao;
import recitewords.apj.com.recitewords.db.dao.WordReviewDao;
import recitewords.apj.com.recitewords.db.dao.WordStudyDao;
import recitewords.apj.com.recitewords.globle.AppConfig;
import recitewords.apj.com.recitewords.util.DateUtil;
import recitewords.apj.com.recitewords.util.MediaUtils;
import recitewords.apj.com.recitewords.util.PrefUtils;
import recitewords.apj.com.recitewords.util.UIUtil;
import recitewords.apj.com.recitewords.view.CircleImageView;

/**
 * Created by CGT on 2016/11/22.
 * <p/>
 * 主页面Fragment
 */
public class MainFragment extends BaseFragment implements View.OnClickListener {

    //主页面背景图片对应单词数组
//    private String[] img_words = new String[]{"antler", "ferry", "lotus pond", "mist", "moon", "reading time", "scarecrow", "twinkling",};
    //定义好的6张背景图id数组
    private int[] imgs = new int[]{R.mipmap.haixin_bg_01, R.mipmap.haixin_bg_02, R.mipmap.haixin_bg_03,
            R.mipmap.haixin_bg_04, R.mipmap.haixin_bg_05, R.mipmap.haixin_bg_06};
    private int[] images = new int[]{R.mipmap.haixin_bg_dim_01, R.mipmap.haixin_bg_dim_02,
            R.mipmap.haixin_bg_dim_03, R.mipmap.haixin_bg_dim_04,
            R.mipmap.haixin_bg_dim_05, R.mipmap.haixin_bg_dim_06};
    //单词集合
    private static List<String> list = new ArrayList() {{
        add("abandon");
        add("ability");
        add("able");
        add("aboard");
        add("about");
        add("abroad");
        add("absorb");
        add("angry");
        add("animal");
        add("anniversary");
        add("announce");
        add("bankrupt");
        add("barber");
        add("computer");
        add("economic");
        add("election");
        add("murder");
        add("progress");
        add("religious");
        add("smart");
    }};

    private class ViewHolder {
        RelativeLayout activity_main;
        LinearLayout linearLayout;
        ImageView img_sign;
        TextView tv_date;
        ImageView iv_menu;
        RelativeLayout main_rl_learn;
        RelativeLayout main_rl_review;
        CircleImageView main_img_circle;
        ImageView main_img_dict;
        TextView main_tv_review_num;  //需复习单词总数

        ImageView iv_sign;  //签到的圆圈
        LinearLayout ll_sign;   //签到的布局
        LinearLayout ll_sign_succeed; //签到成功的布局
        TextView tv_sign_money; //签到获得的酷币
        TextView tv_sign_day;   //连续签到的天数
    }

    private ViewHolder holder;
    private Context mContext;
    private boolean show_navigate_state = false;
    private MainActivity mainActivity;
    private int num;  //背景图片的序号
    private static final int TAKE_PHOTO = 1;    //打开相机的请求码
    private static final int CROP_PHOTO = 2;    //裁剪图片的请求码
    private static final int CHOICE_PHOTO = 3;    //选择图片的请求码
    private static final int CHOICE_CROP = 4;    //选择图片后裁剪的请求码
    private Uri selectedImage; //获取系统返回的照片的Uri
    private Uri selectedImage_choice;   //选择图片裁剪后的Uri
    private Uri imageUri;   //图片uri地址
    private AlertDialog dialog; //查询单词的对话框
    private EditText et_query;  //查询单词的输入框
    private MyAdapter mAdapter; //lv的适配器
    private ImageView iv_query_delete;//输入框的叉叉按钮
    private TextView add_new_word;  //添加到生词本
    private List<String> list_word = new ArrayList<>(); //lv的数据源
    private ListView lv_query;  //listview
    private LinearLayout ll_query_wordInfo;    //单词信息的布局
    private String data;    //输入框单词
    private ImageView iv_word_voice; //单词的发音图标
    private TextView tv_word_soundmark;    //音标
    private TextView tv_word_mean;  //单词意思
    private TextView tv_word_sentence;  //例句
    private TextView tv_word_sentence_mean;//例句意思
    private TextView tv_word_sentence1;    //例句2
    private TextView tv_word_sentence_mean1;  //例句2意思
    private String book_name = "CET4";
    private Book book;
    private WordExampleSentence bean_sentence;


    private int mReviewWordSum;  //需要复习的单词总数
    private UserDao userDao;

    //带参构造方法
    public MainFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public View initView() {
        holder = new ViewHolder();
        mainActivity = (MainActivity) mActivity;
        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_main, null);
        holder.activity_main = findViewByIds(view, R.id.activity_main);
        holder.linearLayout = findViewByIds(view, R.id.main_ll);
        holder.img_sign = findViewByIds(view, R.id.main_img_sign);
        holder.tv_date = findViewByIds(view, R.id.main_tv_date);
        holder.iv_menu = findViewByIds(view, R.id.main_img_menu);
        holder.main_rl_learn = findViewByIds(view, R.id.main_rl_learn);
        holder.main_rl_review = findViewByIds(view, R.id.main_rl_review);
        holder.main_img_circle = findViewByIds(view, R.id.main_img_circle);
        holder.main_img_dict = findViewByIds(view, R.id.main_img_dict);
        holder.main_tv_review_num = findViewByIds(view, R.id.main_tv_review_num);

        holder.iv_sign = findViewByIds(view, R.id.main_img_sign);
        holder.ll_sign = findViewByIds(view, R.id.main_ll_sign);
        holder.ll_sign_succeed = findViewByIds(view, R.id.main_ll_sign_succeed);
        holder.tv_sign_money = findViewByIds(view, R.id.tv_sign_money);
        holder.tv_sign_day = findViewByIds(view, R.id.tv_sign_day);
        return view;
    }

    @Override
    public void initEvent() {
        holder.iv_menu.setOnClickListener(this);
        holder.main_rl_learn.setOnClickListener(this);
        holder.main_rl_review.setOnClickListener(this);
        holder.main_img_circle.setOnClickListener(this);
        holder.main_img_dict.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //每次进入页面时，刷新数据
        BookDao bookDao = new BookDao(mActivity);
        mReviewWordSum = bookDao.queryALLReviewWords(book_name).size();
        //显示需要复习的单词总数
        holder.main_tv_review_num.setText(mReviewWordSum + "");
    }

    @Override
    public void initData() {
        num = randomNum();
        //为主界面设置随机图片和图片对应单词
        holder.activity_main.setBackgroundResource(imgs[num]);
        //设置签到里的日期和星期
        String date = DateUtil.getMonthAndDay() + "" + DateUtil.getWeek();
        holder.tv_date.setText(date);

        holder.iv_sign.setAlpha(150);//主界面签到那里设置透明度
        holder.linearLayout.getBackground().setAlpha(150);  //主界面学习复习按钮设置透明度

        SharedPreferences sp = PrefUtils.getPref(mainActivity);//获取sp
        boolean dbFlag = PrefUtils.getDBFlag(sp, "dbFlag", true);//获取sp中dbFlag的标记
        if (dbFlag) {
            insertExampleSentence();//插入词书表book
            insertBook();//插入词书表book
            insertLexicon();//插入词库表lexicon*/
            insertUser();//插入用户信息表
            //insertWordStudy();//插入学习单词
            BookDao bookDao = new BookDao(mContext);
            bookDao.insertWord_study();     //从Book里获取到20个单词，再插入到word_study 表
            // insertWordReview();  //插入复习单词，模拟数据


            PrefUtils.setDBFlag(sp, "dbFlag", false);//插入完数据将标记设置为false，下次则不会再插入数据
        }

        String sp_imageUri = PrefUtils.getImage(sp, "imageUri", "");
        if (!TextUtils.isEmpty(sp_imageUri)) {
            holder.main_img_circle.setImageURI(Uri.parse(sp_imageUri));
        }
        //获取今天是否已签到
        userDao = new UserDao(mActivity);
        User bean_user = userDao.query();
        String sign_in = bean_user.getSign_in();    //获取签到信息
        int day = bean_user.getSign_in_continue();
        if (!sign_in.equals(DateUtil.getYMD())) {    //判断今天是否签到
            holder.ll_sign.setVisibility(View.VISIBLE); //显示签到布局
            holder.ll_sign_succeed.setVisibility(View.GONE);    //隐藏签到成功布局
        } else {
            holder.ll_sign.setVisibility(View.GONE);    //隐藏签到布局
            holder.ll_sign_succeed.setVisibility(View.VISIBLE); //显示签到成功布局
            //设置酷币和连续签到天数  数字的颜色
            setMoneyColor();
            setDayColor(day);
        }
        if (holder.ll_sign.getVisibility() == View.VISIBLE) {    //如果还没签到
            holder.iv_sign.setOnClickListener(this);    //设置签到圆圈的点击事件
        }
    }


    //例句表插入数据
    private void insertExampleSentence() {
        ExampleSentenceDao dao_example_sentence = new ExampleSentenceDao(mContext);
        dao_example_sentence.insert("abandon", "The cruel man abandoned his wife and child.They had abandoned all hope"
                , "那个狠心的男人抛弃了他的妻儿。他们已经放弃了一切希望", "[əˈbændən]", "网上");
        dao_example_sentence.insert("ability", "She has the ability to keep calm in an emergency.This is a task well within your ability"
                , "她有处变不惊的本事。这完全是你力所能及的工作", "[əˈbɪlɪti]", "网上");
        dao_example_sentence.insert("able", "We shall be able to deal with all sorts of problem.She bet me 20 that I wouldn't be able to give up smoking"
                , "我们应该能够应付各种困难。她和我打20英镑的赌，说我戒不了烟", "[ˈebəl]", "网上");
        dao_example_sentence.insert("aboard", "Little Tom and the sailors spent two months aboard.All passengers aboard fell into the river"
                , "小汤姆和水手们在船上过了两个月。船上所有乘客皆落入河中", "[əˈbɔ:rd]", "网上");
        dao_example_sentence.insert("about", "If you're not satisfied with the life you're living, don't just complain,Do something about it.Instead of complaining about what's wrong, be grateful for what's right"
                , "对于现况的不满，不能只是抱怨，要有勇气作出改变。别抱怨不好的事，要对好的事心存感恩", "[əˈbaʊt]", "网上");
        dao_example_sentence.insert("abroad", "He is travelling abroad.The news spread abroad"
                , "他要到国外旅行。那消息四处传播", "[əˈbrɔd]", "网上");
        dao_example_sentence.insert("absorb", "The material can absorb outward-going radiation from the Earth. The banks would be forced to absorb large losses"
                , "该物质可以吸收地球向外辐射的能量。银行将被迫承受巨大的损失", "[əbˈsɔ:rb]", "网上");
        dao_example_sentence.insert("angry", "They get angry if they think they are being treated disrespectfully.Sarah came forward with a tight and angry face"
                , "他们要是觉得受到了怠慢，就会大动肝火。萨拉走上前来，紧绷着脸，怒气冲冲", "[ˈæŋɡri]", "网上");
        dao_example_sentence.insert("animal", "The jackal is a wild animal in Africa and Asia.This small animal has a pair of brown eyes"
                , "豺狼是产于亚非的一种野生动物。这只小动物有一双棕色的眼睛", "[ˈænəməl]", "网上");
        dao_example_sentence.insert("anniversary", "He gave me a necklace as an anniversary gift.How did you celebrate your wedding anniversary?"
                , "他给我一条项链作为周年纪念礼物。你是怎样庆祝结婚周年纪念日的？", "[ˌænɪˈvɜ:rsəri]", "网上");
        dao_example_sentence.insert("announce", "She was planning to announce her engagement to Peter.She was bursting to announce the news but was sworn to secrecy"
                , "她正计划宣布她和彼得订婚一事。她急不可待想宣布这个消息，但却发过誓要守口如瓶", "[əˈnaʊns]", "网上");
        dao_example_sentence.insert("bankrupt", "If your liabilities exceed your assets, you may go bankrupt.I was bankrupt and unable to pay his debts"
                , "如果你所负的债超过你的资产，你就会破产。我破产了，不能偿还他的债务", "[ˈbæŋkˌrʌpt, -rəpt]", "网上");
        dao_example_sentence.insert("barber", "She had to call a barber to shave him.She asked the barber to crop her hair short"
                , "她不得不叫个理发师来给他刮脸。她叫理发师把她的头发剪短了", "[ˈbɑ:rbə(r)]", "网上");
        dao_example_sentence.insert("computer", "I'm getting a new computer for birthday present.This computer company was established last year"
                , "我得到一台电脑作生日礼物。这家电脑公司是去年成立的", "[kəmˈpjutɚ]", "网上");
        dao_example_sentence.insert("economic", "Economic analysis displays its effectiveness in tackling pollution.They had led the country into economic disaster"
                , "通过对污染税的经济学分析可以看出其较强的治理效果。他们把国家带入了经济灾难中", "[i:kəˈnɑ:mɪk]", "网上");
        dao_example_sentence.insert("election", "Our party was routed at the election.I may vote for her at the next election"
                , "我们党在竞选中被彻底击败了。下届选举我可能选她", "[ɪˈlɛkʃən]", "网上");
        dao_example_sentence.insert("murder", "He did a long stretch for attempted murder.The law arrived on the scene of murder"
                , "他因谋杀未遂罪坐了很长时间的牢。警察来到谋杀现场", "[ˈmɜ:rdə(r)]", "网上");
        dao_example_sentence.insert("progress", "The student is showing rapid progress in his studies.Physics has made enormous progress in this century"
                , "这个学生学习上进步很快。本世纪物理学的发展突飞猛进", "[ˈprɑ:gres]", "网上");
        dao_example_sentence.insert("religious", "She was a fairly rigid person who had strong religious views.The teenager may have been abducted by a religious cult"
                , "她相当顽固，宗教观念极强。这个少年可能被一个异教组织绑架了", "[rɪˈlɪdʒəs]", "网上");
        dao_example_sentence.insert("smart", "I'm a smart girl that speaks with a smart accent.A few months ago they opened a new shop catering exclusively to the smart set"
                , "我是个机灵的姑娘，说话带有时髦的腔调。几个月前他们新开了一家商店，专门向最时髦的人士出售物品", "[smɑ:rt]", "网上");
    }

    /**
     * 插入词书表book
     */
    private void insertBook() {
        BookDao bookDao = new BookDao(mContext);
        bookDao.addWord("economic", "[i:kəˈnɑ:mɪk]", "[ˌi:kəˈnɒmɪk]", "adj.经济的;经济学的;合算的;有经济效益的", 1, 0, "F", DateUtil.getNowDate("yyyy-MM-dd"), "CET4", 0);
        bookDao.addWord("election", "[ɪˈlɛkʃən]", "[ɪˈlekʃn]", "n.选举，当选;选举权;[神]神的选择", 1, 0, "F", DateUtil.getNowDate("yyyy-MM-dd"), "CET4", 0);
        bookDao.addWord("murder", "[ˈmɜ:rdə(r)]", "[ˈmɜ:də(r)]", "n.谋杀;杀戮;极艰难[令人沮丧]的经历 vt.凶杀;糟蹋;打垮 vi.杀人", 1, 0, "F", "2001-11-11", "CET4", 0);
        bookDao.addWord("progress", "[ˈprɑ:gres]", "[ˈprəʊgres]", "n.进步;前进;[生物学]进化;（向更高方向）增长 v.发展;（使）进步，（使）进行;促进 vi.发展;（向更高方向）增进", 1, 0, "F", DateUtil.getNowDate("yyyy-MM-dd"), "CET4", 0);
        bookDao.addWord("religious", "[rɪˈlɪdʒəs]", "[rɪˈlɪdʒəs]", "adj.宗教的;虔诚的;笃信宗教的;谨慎的 n.修士，修女，出家人", 1, 0, "F", "2016-01-28", "CET4", 0);
        bookDao.addWord("smart", "[smɑ:rt]", "[smɑ:t]", "adj.聪明的;敏捷的;漂亮的;整齐的 vi.疼痛;感到刺痛;难过 n.创伤;刺痛;疼痛;痛苦 vt.引起…的疼痛（或痛苦、苦恼等） adv.聪明伶俐地，轻快地，漂亮地", 1, 0, "F", DateUtil.getNowDate("yyyy-MM-dd"), "CET4", 0);
        bookDao.addWord("barber", "[ˈbɑ:rbə(r)]", "[ˈbɑ:bə(r)]", "n.理发师;理发店(= barber's shop) vt.为…理发剃须;修整 vi.当理发师;给人理发", 1, 0, "F", "2016-01-28", "CET4", 0);
        bookDao.addWord("animal", "[ˈænəməl]", "[ˈænɪml]", "n.动物，兽，牲畜;<俚>家畜，牲口;<俚>畜生（一般的人）;兽性 adj.动物的;肉体的;肉欲的", 1, 0, "F", DateUtil.getNowDate("yyyy-MM-dd"), "CET4", 0);
        bookDao.addWord("abandon", "[əˈbændən]", "[əˈbændən]", "vt.放弃，抛弃;离弃，丢弃;使屈从;停止进行，终止 n.放任，放纵;完全屈从于压制", 1, 0, "F", DateUtil.getNowDate("yyyy-MM-dd"), "CET4", 0);
        bookDao.addWord("ability", "[əˈbɪlɪti]", "[əˈbɪləti]", "n.能力，资格;能耐，才能", 1, 0, "F", "2016-10-28", "CET4", 0);
        bookDao.addWord("able", "[ˈebəl]", "[ˈeɪbl]", "adj.能够的;有能力的;有才干的;干练的", 1, 0, "F", "2016-10-28", "CET4", 0);
        bookDao.addWord("aboard", "[əˈbɔ:rd]", "[əˈbɔ:d]", "prep.上车;在（船、飞机、车）上，上（船、飞机、车） adv.在船（或飞机、车）上，上船（或飞机、车）;靠船边;在船上;在火车上", 1, 0, "F", DateUtil.getNowDate("yyyy-MM-dd"), "CET4", 0);
        bookDao.addWord("about", "[əˈbaʊt]", "[əˈbaʊt]", "prep.关于;大约;在…周围 adv.大约;在附近;在四周;几乎 adj.在附近的;四处走动的;在起作用的;在流行中的", 1, 0, "F", DateUtil.getNowDate("yyyy-MM-dd"), "CET4", 0);
        bookDao.addWord("abroad", "[əˈbrɔd]", "[əˈbrɔ:d]", "adv.到国外，在海外;广为流传地 adj.往国外的 n.海外，异国", 1, 0, "F", "2016-10-28", "CET4", 0);
        bookDao.addWord("absorb", "[əbˈsɔ:rb]", "[əbˈsɔ:b]", "vt.吸收（液体、气体等）;吸引（注意）;吞并，合并;忍受，承担（费用）", 1, 0, "F", DateUtil.getNowDate("yyyy-MM-dd"), "CET4", 0);
        bookDao.addWord("angry", "[ˈæŋɡri]", "[ˈæŋgri]", "adj.生气的;愤怒的，发怒的;（颜色等）刺目的;（伤口等）发炎的", 1, 0, "F", "2016-05-01", "CET4", 0);
        bookDao.addWord("anniversary", "[ˌænɪˈvɜ:rsəri]", "[ˌænɪˈvɜ:səri]", "n.周年纪念日 adj.周年的;周年纪念的;年年的;每年的", 1, 0, "F", "2016-05-01", "CET4", 0);
        bookDao.addWord("announce", "[əˈnaʊns]", "[əˈnaʊns]", "vi.宣布参加竞选;当播音员 vt.宣布;述说;声称;预告", 1, 0, "F", "2016-12-01", "CET4", 0);
        bookDao.addWord("bankrupt", "[ˈbæŋkˌrʌpt, -rəpt]", "[ˈbæŋkrʌpt]", "adj.破产的，倒闭的;完全缺乏的;（名誉）扫地的，（智力等）完全丧失的;垮了的，枯竭的 n.破产者;无力偿还债务者;丧失（名誉，智力等）的人  vt.使破产，使枯竭，使极端贫困", 1, 0, "F", DateUtil.getNowDate("yyyy-MM-dd"), "CET4", 0);
        bookDao.addWord("computer", "[kəmˈpjutɚ]", "[kəmˈpju:tə(r)]", "n.（电子）计算机，电脑", 1, 0, "F", "2016-12-01", "CET4", 0);
    }

    /**
     * 插入复习单词
     * <p/>
     * 模拟需要复习的单词
     */
    public void insertWordReview() {
        WordReviewDao dao = new WordReviewDao(mContext);

        dao.addWord("economic", "", "", "", "", "[i:kəˈnɑ:mɪk]", "[ˌi:kəˈnɒmɪk]",
                "adj.经济的;经济学的;合算的;有经济效益的", "", 0, "", "", "CET4", 0);

        dao.addWord("election", "", "", "", "", "[ɪˈlɛkʃən]", "[ɪˈlekʃn]",
                "n.选举，当选;选举权;[神]神的选择", "", 0, "", "", "CET4", 0);

        dao.addWord("murder", "", "", "", "", "[ˈmɜ:rdə(r)]", "[ˈmɜ:də(r)]",
                "n.谋杀;杀戮;极艰难[令人沮丧]的经历\n" +
                        "vt.凶杀;糟蹋;打垮\n" +
                        "vi.杀人\n", "", 0, "", "", "CET4", 0);

        dao.addWord("progress", "", "", "", "", "[ˈprɑ:gres]", "[ˈprəʊgres]",
                "n.进步;前进;[生物学]进化;（向更高方向）增长\n" +
                        "v.发展;（使）进步，（使）进行;促进\n" +
                        "vi.发展;（向更高方向）增进\n", "", 0, "", "", "CET4", 0);

        dao.addWord("religious", "", "", "", "", "[rɪˈlɪdʒəs]", "[rɪˈlɪdʒəs]",
                "adj.宗教的;虔诚的;笃信宗教的;谨慎的\n" +
                        "n.修士，修女，出家人\n", "", 0, "", "", "CET4", 0);

        dao.addWord("religious", "", "", "", "", "[smɑ:rt]", "[smɑ:t]",
                "adj.聪明的;敏捷的;漂亮的;整齐的\n" +
                        "vi.疼痛;感到刺痛;难过\n" +
                        "n.创伤;刺痛;疼痛;痛苦\n" +
                        "vt.引起…的疼痛（或痛苦、苦恼等）\n" +
                        "adv.聪明伶俐地，轻快地，漂亮地\n", "", 0, "", "", "CET4", 0);
//        dao.addWord("religious", "", "", "", "", "", "", "","", "", "CET4", 0);
    }

    /**
     * 插入词库表lexicon
     */
    private void insertLexicon() {
        LexiconDao lexiconDao = new LexiconDao(mContext);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
        lexiconDao.addWord("CET4", "英语四级词汇 单词数4114", 20, 0, 0, 0, 0);
    }

    /**
     * 插入学习单词
     */
    public void insertWordStudy() {
        WordStudyDao dao = new WordStudyDao(mContext);
        dao.addWord("economic", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("election", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("murder", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("progress", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("religious", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("smart", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("barber", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("animal", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("abandon", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("ability", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("able", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("aboard", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("about", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("abroad", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("absorb", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("angry", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("anniversary", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("announce", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("bankrupt", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("computer", "", "", "", "", "", "", "", "", 0, "", "CET4", 0);
    }

    //添加用户信息表信息
    private void insertUser() {
        UserDao dao = new UserDao(mContext);
        dao.add("11", 0, 0);
    }


    //setOnClickListener监听点击的回调方法
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_img_menu:
                boolean navigateShowState = mainActivity.getNavigateShowState();
                if (navigateShowState) {
                    mainActivity.setNavigateHide();
                } else {
                    mainActivity.setNavigateShow();
                }
                mainActivity.setNavigateShowState(!navigateShowState);
                // Log.e("ha", "点击了，状态后为：" + navigateShowState);
                break;
            case R.id.main_rl_learn:
                //跳转到学习界面
                Intent intent = new Intent(mActivity, LearnActivity.class);
                intent.putExtra("backgroundNum", num);
                startActivity(intent);
                break;
            case R.id.main_rl_review:
                //判断是否跳转到复习界面
                if (mReviewWordSum > 0) {
                    Intent intent_review = new Intent(mActivity, ReviewActivity.class);
                    intent_review.putExtra("backgroundNum", num);
                    startActivity(intent_review);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("温馨提醒：");
                    builder.setMessage("暂时还没有复习任务，你任性去吧");
                    builder.setPositiveButton("确定", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }


                break;
            case R.id.main_img_circle:
                //头像的点击事件、弹出对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                AlertDialog dialog = builder.create();
                dialog.setTitle("更换头像");
                dialog.setButton("选择照片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        choicePhoto();  //调用选择照片方法
                    }
                });
                dialog.setButton2("拍摄", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        takePhoto();    //调用拍摄方法
                    }
                });
                dialog.show();
                break;
            case R.id.main_img_dict:
                //查询单词
                AlertDialog.Builder builder_query = new AlertDialog.Builder(mActivity, R.style.Dialog_Fullscreen);
                this.dialog = builder_query.create();
                View view = View.inflate(mActivity, R.layout.dialog_query_word, null);
                this.dialog.setView(view, 0, 0, 0, 0);
                RelativeLayout ll_query_word = UIUtil.findViewByIds(view, R.id.rl_query_word);
                RelativeLayout ll_query_bg = UIUtil.findViewByIds(view, R.id.rl_query_bg);
                et_query = UIUtil.findViewByIds(view, R.id.et_query);
                iv_query_delete = UIUtil.findViewByIds(view, R.id.iv_query_delete);//输入框的叉叉按钮
                Button btn_cancel_query = UIUtil.findViewByIds(view, R.id.btn_cancel_query);//取消按钮
                add_new_word = UIUtil.findViewByIds(view, R.id.add_new_word);  //添加到生词本
                lv_query = UIUtil.findViewByIds(view, R.id.lv_query);//listview
                ll_query_wordInfo = UIUtil.findViewByIds(view, R.id.ll_query_wordInfo);    //单词信息的布局
                iv_word_voice = UIUtil.findViewByIds(view, R.id.iv_word_voice); //单词的发音图标
                tv_word_soundmark = UIUtil.findViewByIds(view, R.id.tv_word_soundmark);    //音标
                tv_word_mean = UIUtil.findViewByIds(view, R.id.tv_word_mean);  //单词意思
                tv_word_sentence = UIUtil.findViewByIds(view, R.id.tv_word_sentence);  //例句
                tv_word_sentence_mean = UIUtil.findViewByIds(view, R.id.tv_word_sentence_mean);//例句意思
                tv_word_sentence1 = UIUtil.findViewByIds(view, R.id.tv_word_sentence1);    //例句2
                tv_word_sentence_mean1 = UIUtil.findViewByIds(view, R.id.tv_word_sentence_mean1);  //例句2意思

                ll_query_bg.getBackground().setAlpha(100);  //设置输入框所在布局透明度
                et_query.getBackground().setAlpha(70);  //设置输入框背景透明度
                ll_query_word.setBackgroundResource(images[num]);//设置对话框背景
                mAdapter = new MyAdapter();
                lv_query.setAdapter(mAdapter);  //listview设置适配器
                setListener();//搜索框EditText和ListView设置监听

                iv_query_delete.setOnClickListener(this);
                btn_cancel_query.setOnClickListener(this);
                iv_word_voice.setOnClickListener(this);
                add_new_word.setOnClickListener(this);
                this.dialog.show();
                break;
            case R.id.iv_query_delete:
                //查询的叉叉的点击事件
                et_query.setText("");
                break;
            case R.id.btn_cancel_query:
                //取消查询的点击事件
                this.dialog.dismiss();
                list_word.clear();
                break;
            case R.id.add_new_word:
                //创建生词本数据库
                BookDao bookDao = new BookDao(mActivity);
                ExampleSentenceDao exampleDao = new ExampleSentenceDao(mActivity);
                bookDao.addWord(book.getWord(), book.getSoundmark_american(),
                        book.getSoundmark_british(), book.getWord_mean(),
                        0, 0, "", book.getDate(), AppConfig.BOOK_NEW_WORDS, book.getUserID());
                exampleDao.insert(bean_sentence.getWord(), bean_sentence.getExample_sentence(),
                        bean_sentence.getExample_sentence_mean(),
                        bean_sentence.getExample_sentence_pronounce(),
                        bean_sentence.getExample_sentence_resource());

                Toast.makeText(mActivity, "成功添加到生词本", Toast.LENGTH_SHORT).show();

                break;
            case R.id.iv_word_voice:
                //查询单词界面发音
                MediaUtils.playWord(mActivity, data);
                break;
            case R.id.main_img_sign:
                //签到
                holder.iv_sign.setOnClickListener(null);    //取消签到的监听事件
                holder.ll_sign.setVisibility(View.GONE);    //隐藏要签到的布局
                holder.ll_sign_succeed.setVisibility(View.VISIBLE); //显示签到成功的布局
                User bean_user = userDao.query();   //查询信息
                String sign_in = bean_user.getSign_in();    //获取签到
                int sign_in_continue = bean_user.getSign_in_continue(); //获取连续签到天数
                int cool_money = bean_user.getCool_money(); //获取酷币
                int sign_in_continue1 = 1;
                int cool_money1 = cool_money + 20;
                if (Integer.parseInt(DateUtil.getYMD()) - Integer.parseInt(sign_in) == 1) {
                    sign_in_continue1 = sign_in_continue + 1;
                }
                userDao.update(DateUtil.getYMD(), sign_in_continue1, cool_money1);  //更新用户信息
                //设置酷币和连续签到天数  数字的颜色
                setMoneyColor();
                setDayColor(sign_in_continue1);
                break;
            default:
                break;
        }
    }

    //设置获取酷币个数的数字颜色
    private void setMoneyColor() {
        String sign_money = holder.tv_sign_money.getText().toString();
        SpannableStringBuilder style = new SpannableStringBuilder(sign_money);
        int start = sign_money.indexOf("20");
        style.setSpan(new ForegroundColorSpan(Color.parseColor("#d1f57f")), start, start + 2, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        holder.tv_sign_money.setText(style);
    }

    //设置连续签到天数的颜色
    private void setDayColor(int day) {
        String sign_day = "连续签到" + day + "天";
        SpannableStringBuilder style1 = new SpannableStringBuilder(sign_day);
        int start1 = sign_day.indexOf(String.valueOf(day));
        int end1 = String.valueOf(day).length() + start1;
        style1.setSpan(new ForegroundColorSpan(Color.parseColor("#d1f57f")), start1, end1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        holder.tv_sign_day.setText(style1);
    }

    //搜索框ext和ListView设置监听
    private void setListener() {
        et_query.setOnKeyListener(onKeyListener);   //设置输入法软键盘右下角按钮监听器
        et_query.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    data = et_query.getText().toString();
                    if (list.contains(data)) {
                        //设置单词信息
                        setWordInfo();
                        return false;
                    } else {
                        ll_query_wordInfo.setVisibility(View.GONE);//隐藏单词信息
                        lv_query.setVisibility(View.VISIBLE);   //显示listView
                        return true;//这里返回true表示方法回调完系统不需再处理。
                    }
                }
                return false;
            }
        });
        //输入框添加文本变化监听器
        et_query.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //文本变化后
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {    //文本内容大于0
                    iv_query_delete.setVisibility(View.VISIBLE);    //显示删除按钮
                    list_word.clear();  //先清空lv集合数据
                    showListViewData(); //再添加数据
                    mAdapter.notifyDataSetChanged();    //通知lv更新
                    lv_query.setVisibility(View.VISIBLE);   //显示listview
                    ll_query_wordInfo.setVisibility(View.GONE); //隐藏单词信息
                } else {
                    iv_query_delete.setVisibility(View.GONE);   //隐藏删除按钮
                    list_word.clear();  //清空集合数据
                    mAdapter.notifyDataSetChanged();    //通知lv更新
                    lv_query.setVisibility(View.VISIBLE);   //显示listview
                    ll_query_wordInfo.setVisibility(View.GONE); //隐藏单词信息
                }
            }
        });

        lv_query.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                et_query.setText(list_word.get(position));  //设置文本框
                list_word.clear();  //清空单词集合
                mAdapter.notifyDataSetChanged();    //通知lv更新
                data = et_query.getText().toString();   //获取文本框内容
                setWordInfo();  //设置单词信息
                //隐藏软键盘
                InputMethodManager manager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (manager.isActive()) {
                    manager.hideSoftInputFromWindow(et_query.getWindowToken(), 0);
                }
            }
        });
    }

    //设置单词信息
    private void setWordInfo() {
        lv_query.setVisibility(View.GONE);//隐藏lv
        ll_query_wordInfo.setVisibility(View.VISIBLE);//显示单词信息布局

        book = queryBook(data); //查询单词信息
        tv_word_soundmark.setText(book.getSoundmark_american());//设置音标
        tv_word_mean.setText(book.getWord_mean());//设置单词意思

        WordExampleSentence bean_sentence = querySentence(data);//查询例句信息
        String sentence = bean_sentence.getExample_sentence();//获取例句
        String sentence_mean = bean_sentence.getExample_sentence_mean();//获取例句意思
        String[] sentences = sentence.split("\\.");
        String[] sentence_means = sentence_mean.split("。");
        tv_word_sentence.setText(sentences[0]);
        tv_word_sentence1.setText(sentences[1]);
        tv_word_sentence_mean.setText(sentence_means[0]);
        tv_word_sentence_mean1.setText(sentence_means[1]);
    }

    //获取词书表的查询单词信息方法返回book的bean对象
    private Book queryBook(String word) {
        BookDao bookDao = new BookDao(mActivity);
        Book book = bookDao.query_wordInfo(word);
        return book;
    }

    //获取例句表的查询例句信息方法返回WordExampleSentence的bean对象
    private WordExampleSentence querySentence(String word) {
        ExampleSentenceDao sentenceDao = new ExampleSentenceDao(mActivity);
        bean_sentence = sentenceDao.query(word);  //查询例句
        return bean_sentence;
    }

    //将总集合里的数据里包含文本框的数据添加到ListView数据集合
    private void showListViewData() {
        String data = et_query.getText().toString();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).contains(data)) {
                list_word.add(list.get(i));
            }
        }
    }

    //输入法软键盘返回键的监听器
    private View.OnKeyListener onKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK
                    && event.getAction() == KeyEvent.ACTION_DOWN) {
                list_word.clear();
                Log.d("seven", "清空了哈哈哈哈");
                return false;   //这里返回的boolean类型表示是否还需要系统处理，false表示方法回调完系统再处理返回。
            }
            return false;
        }
    };

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list_word.size();
        }

        @Override
        public Object getItem(int position) {
            return list_word.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView != null) {
                view = convertView;
            } else {
                view = View.inflate(mActivity, R.layout.listview_query, null);
            }
            TextView tv_lv = UIUtil.findViewByIds(view, R.id.tv_lv);
            tv_lv.setText(list_word.get(position));
            return view;
        }
    }

    //拍摄方法
    private void takePhoto() {
        File outputImage = new File(Environment.getExternalStorageDirectory(), "tempImage.jpg"); //创建文件在sd卡并命名
        try {
            if (outputImage.exists()) {
                outputImage.delete();   //如果文件存在则删除文件
            }
            outputImage.createNewFile();    //创建文件
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageUri = Uri.fromFile(outputImage);   //将文件转化为Uri地址
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");   //设置Intent的action
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //跳转到相机拍摄
        startActivityForResult(intent, TAKE_PHOTO); //相机执行完回调到onActivityResult方法
    }

    //选择照片方法
    private void choicePhoto() {
        File outputImage_choice = new File(Environment.getExternalStorageDirectory(), "tempImage_choice.jpg"); //创建文件在sd卡并命名
        try {
            if (outputImage_choice.exists()){
                outputImage_choice.delete();   //如果文件存在则删除文件
            }
            outputImage_choice.createNewFile();    //创建文件
        } catch (IOException e) {
            e.printStackTrace();
        }
        selectedImage_choice = Uri.fromFile(outputImage_choice);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);   //设置intent的action
        intent.setType("image/*");  //设置类型
        startActivityForResult(intent, CHOICE_PHOTO);   //照片选择完回调到onActivityResult方法
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:    //相机执行完的回调
                if (resultCode == mActivity.RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");   //跳转到图片裁剪
                    intent.setDataAndType(imageUri, "image/*"); //设置数据和类型
                    intent.putExtra("scale", true);     //设置可缩放
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //设置位置
                    startActivityForResult(intent, CROP_PHOTO); //裁剪执行完同样回调到onActivityResult方法
                }
                break;
            case CROP_PHOTO:    //裁剪执行完的回调
                if (resultCode == mActivity.RESULT_OK) {
                    holder.main_img_circle.setImageURI(null);   //先设置为null，防止第二次点击拍摄时头像不更新
                    holder.main_img_circle.setImageURI(imageUri);   //设置头像
                    SharedPreferences sp = PrefUtils.getPref(mActivity);
                    sp.edit().remove("imageUri").commit();
                    PrefUtils.setImage(sp, "imageUri", imageUri.toString());
                }
                break;
            case CHOICE_PHOTO:      //选择完图片的回调
                if (resultCode == mActivity.RESULT_OK) {     //从相册选择照片不裁切
                    try {
                        selectedImage = data.getData();//获取系统返回的照片的Uri
                        Intent intent = new Intent("com.android.camera.action.CROP");   //跳转到图片裁剪
                        intent.setDataAndType(selectedImage, "image/*"); //设置数据和类型
                        intent.putExtra("scale", true);     //设置可缩放
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImage_choice); //设置位置
                        startActivityForResult(intent, CHOICE_CROP); //裁剪执行完同样回调到onActivityResult方法
//                        holder.main_img_circle.setImageURI(selectedImage);
//                        SharedPreferences sp = PrefUtils.getPref(mActivity);
//                        sp.edit().remove("imageUri").commit();
//                        PrefUtils.setImage(sp, "imageUri", selectedImage.toString());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOICE_CROP:
                holder.main_img_circle.setImageURI(null);   //先设置为null，防止第二次点击拍摄时头像不更新
                holder.main_img_circle.setImageURI(selectedImage_choice);   //设置头像
                SharedPreferences sp = PrefUtils.getPref(mActivity);
                sp.edit().remove("imageUri").commit();
                PrefUtils.setImage(sp, "imageUri", selectedImage_choice.toString());
                break;
        }
    }

    /**
     * 生成0 - 5的随机数
     *
     * @return 随机数
     */
    private int randomNum() {
        int num = (int) (Math.random() * 6);
        return num;
    }

}
