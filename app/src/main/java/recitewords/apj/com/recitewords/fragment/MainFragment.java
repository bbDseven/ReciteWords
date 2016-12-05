package recitewords.apj.com.recitewords.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.activity.LearnActivity;
import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.activity.ReviewActivity;
import recitewords.apj.com.recitewords.db.dao.BookDao;
import recitewords.apj.com.recitewords.db.dao.ExampleSentenceDao;
import recitewords.apj.com.recitewords.db.dao.LexiconDao;
import recitewords.apj.com.recitewords.db.dao.WordStudyDao;
import recitewords.apj.com.recitewords.util.DateUtil;
import recitewords.apj.com.recitewords.util.PrefUtils;

/**
 * Created by CGT on 2016/11/22.
 * <p/>
 * 主页面Fragment
 */
public class MainFragment extends BaseFragment implements View.OnClickListener {

    //主页面背景图片对应单词数组
    private String[] img_words = new String[]{"antler", "ferry", "lotus pond", "mist", "moon", "reading time", "scarecrow", "twinkling",};
    //定义好的6张背景图id数组
    private int[] imgs = new int[]{R.mipmap.haixin_bg_01, R.mipmap.haixin_bg_02, R.mipmap.haixin_bg_03,
            R.mipmap.haixin_bg_04, R.mipmap.haixin_bg_05, R.mipmap.haixin_bg_06};

    private class ViewHolder {
        RelativeLayout activity_main;
        TextView tv_word;
        LinearLayout linearLayout;
        ImageView img_sign;
        ImageView main_img_sign;
        TextView tv_date;
        ImageView iv_menu;
        RelativeLayout main_rl_learn;
        RelativeLayout main_rl_review;
    }

    private ViewHolder holder;
    private Context mContext;
    private boolean show_navigate_state = false;
    private MainActivity mainActivity;
    private int num;  //背景图片的序号

    //带参构造方法
    public MainFragment(Context context) {
        this.mContext = context;
    }

    @Override
    public View initView() {
        holder = new ViewHolder();
        mainActivity = (MainActivity) mActivity;
        View view = LayoutInflater.from(mContext).inflate(R.layout.fragment_main, null);
        holder.activity_main = findViewByIds(view, R.id.activity_main);
        holder.tv_word = findViewByIds(view, R.id.main_tv_word);
        holder.linearLayout = findViewByIds(view, R.id.main_ll);
        holder.img_sign = findViewByIds(view, R.id.main_img_sign);
        holder.main_img_sign = findViewByIds(view, R.id.main_img_sign);
        holder.tv_date = findViewByIds(view, R.id.main_tv_date);
        holder.iv_menu = findViewByIds(view, R.id.main_img_menu);
        holder.main_rl_learn = findViewByIds(view, R.id.main_rl_learn);
        holder.main_rl_review = findViewByIds(view, R.id.main_rl_review);
        return view;
    }

    @Override
    public void initEvent() {
        holder.iv_menu.setOnClickListener(this);
        holder.main_rl_learn.setOnClickListener(this);
        holder.main_rl_review.setOnClickListener(this);
    }

    @Override
    public void initData() {
        num = randomNum();
        //为主界面设置随机图片和图片对应单词
        holder.activity_main.setBackgroundResource(imgs[num]);

        holder.tv_word.setText(img_words[num]);
        //设置签到里的日期和星期
        String date = DateUtil.getMonthAndDay() + "" + DateUtil.getWeek();
        holder.tv_date.setText(date);

        holder.main_img_sign.setAlpha(150);//主界面签到那里设置透明度
        holder.linearLayout.getBackground().setAlpha(150);  //主界面学习复习按钮设置透明度

        SharedPreferences sp = PrefUtils.getPref(mainActivity);//获取sp
        boolean dbFlag = PrefUtils.getDBFlag(sp, "dbFlag", true);//获取sp中dbFlag的标记
        if (dbFlag) {
            insertExampleSentence();//插入词书表book
            insertBook();//插入词书表book
            insertLexicon();//插入词库表lexicon*/
            insertWordStudy();//插入学习单词
            PrefUtils.setDBFlag(sp, "dbFlag", false);//插入完数据将标记设置为false，下次则不会再插入数据
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

    /*
    * 插入词书表book
    * */
    private void insertBook() {
        BookDao bookDao = new BookDao(mContext);
        bookDao.addWord("economic", "[i:kəˈnɑ:mɪk]", "[ˌi:kəˈnɒmɪk]", "adj.经济的;经济学的;合算的;有经济效益的", 0, 0, "CET4", 0);
        bookDao.addWord("election", "[ɪˈlɛkʃən]", "[ɪˈlekʃn]", "n.选举，当选;选举权;[神]神的选择", 0, 0, "CET4", 0);
        bookDao.addWord("murder", "[ˈmɜ:rdə(r)]", "[ˈmɜ:də(r)]", "n.谋杀;杀戮;极艰难[令人沮丧]的经历\n" +
                "vt.凶杀;糟蹋;打垮\n" +
                "vi.杀人", 0, 0, "CET4", 0);
        bookDao.addWord("progress", "[ˈprɑ:gres]", "[ˈprəʊgres]", "n.进步;前进;[生物学]进化;（向更高方向）增长\n" +
                "v.发展;（使）进步，（使）进行;促进\n" +
                "vi.发展;（向更高方向）增进", 0, 0, "CET4", 0);
        bookDao.addWord("religious", "[rɪˈlɪdʒəs]", "[rɪˈlɪdʒəs]", "adj.宗教的;虔诚的;笃信宗教的;谨慎的\n" +
                "n.修士，修女，出家人", 0, 0, "CET4", 0);
        bookDao.addWord("smart", "[smɑ:rt]", "[smɑ:t]", "adj.聪明的;敏捷的;漂亮的;整齐的\n" +
                "vi.疼痛;感到刺痛;难过\n" +
                "n.创伤;刺痛;疼痛;痛苦\n" +
                "vt.引起…的疼痛（或痛苦、苦恼等）\n" +
                "adv.聪明伶俐地，轻快地，漂亮地", 0, 0, "CET4", 0);
        bookDao.addWord("barber", "[ˈbɑ:rbə(r)]", "[ˈbɑ:bə(r)]", "n.理发师;理发店(= barber's shop)\n" +
                "vt.为…理发剃须;修整\n" +
                "vi.当理发师;给人理发", 0, 0, "CET4", 0);
        bookDao.addWord("animal", "[ˈænəməl]", "[ˈænɪml]", "n.动物，兽，牲畜;<俚>家畜，牲口;<俚>畜生（一般的人）;兽性\n" +
                "adj.动物的;肉体的;肉欲的", 0, 0, "CET4", 0);
        bookDao.addWord("abandon", "[əˈbændən]", "[əˈbændən]", "vt.放弃，抛弃;离弃，丢弃;使屈从;停止进行，终止\n" +
                "n.放任，放纵;完全屈从于压制\n", 0, 0, "CET4", 0);
        bookDao.addWord("ability", "[əˈbɪlɪti]", "[əˈbɪləti]", "n.能力，资格;能耐，才能", 0, 0, "CET4", 0);
        bookDao.addWord("able", "[ˈebəl]", "[ˈeɪbl]", "adj.能够的;有能力的;有才干的;干练的", 0, 0, "CET4", 0);
        bookDao.addWord("aboard", "[əˈbɔ:rd]", "[əˈbɔ:d]", "prep.上车;在（船、飞机、车）上，上（船、飞机、车）\n" +
                "adv.在船（或飞机、车）上，上船（或飞机、车）;靠船边;在船上;在火车上", 0, 0, "CET4", 0);
        bookDao.addWord("about", "[əˈbaʊt]", "[əˈbaʊt]", "prep.关于;大约;在…周围\n" +
                "adv.大约;在附近;在四周;几乎\n" +
                "adj.在附近的;四处走动的;在起作用的;在流行中的", 0, 0, "CET4", 0);
        bookDao.addWord("abroad", "[əˈbrɔd]", "[əˈbrɔ:d]", "adv.到国外，在海外;广为流传地\n" +
                "adj.往国外的\n" +
                "n.海外，异国", 0, 0, "CET4", 0);
        bookDao.addWord("absorb", "[əbˈsɔ:rb]", "[əbˈsɔ:b]", "vt.吸收（液体、气体等）;吸引（注意）;吞并，合并;忍受，承担（费用）", 0, 0, "CET4", 0);
        bookDao.addWord("angry", "[ˈæŋɡri]", "[ˈæŋgri]", "adj.生气的;愤怒的，发怒的;（颜色等）刺目的;（伤口等）发炎的", 0, 0, "CET4", 0);
        bookDao.addWord("anniversary", "[ˌænɪˈvɜ:rsəri]", "[ˌænɪˈvɜ:səri]", "n.周年纪念日\n" +
                "adj.周年的;周年纪念的;年年的;每年的", 0, 0, "CET4", 0);
        bookDao.addWord("announce", "[əˈnaʊns]", "[əˈnaʊns]", "vi.宣布参加竞选;当播音员\n" +
                "vt.宣布;述说;声称;预告", 0, 0, "CET4", 0);
        bookDao.addWord("bankrupt", "[ˈbæŋkˌrʌpt, -rəpt]", "[ˈbæŋkrʌpt]", "adj.破产的，倒闭的;完全缺乏的;（名誉）扫地的，（智力等）完全丧失的;垮了的，枯竭的\n" +
                "n.破产者;无力偿还债务者;丧失（名誉，智力等）的人\n" +
                "vt.使破产，使枯竭，使极端贫困", 0, 0, "CET4", 0);
        bookDao.addWord("computer", "[kəmˈpjutɚ]", "[kəmˈpju:tə(r)]", "n.（电子）计算机，电脑", 0, 0, "CET4", 0);
    }

    /*
    * 插入词库表lexicon
    * */
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
        dao.addWord("economic", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("election", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("murder", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("progress", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("religious", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("smart", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("barber", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("animal", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("abandon", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("ability", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("able", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("aboard", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("about", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("abroad", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("absorb", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("angry", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("anniversary", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("announce", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("bankrupt", "", "", "", "", "", "", 0, "", "CET4", 0);
        dao.addWord("computer", "", "", "", "", "", "", 0, "", "CET4", 0);
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
                Log.e("ha", "点击了，状态后为：" + navigateShowState);
                break;
            case R.id.main_rl_learn:
                //跳转到学习界面
                Intent intent = new Intent(mActivity, LearnActivity.class);
                intent.putExtra("backgroundNum", num);
                startActivity(intent);
                break;
            case R.id.main_rl_review:
                //跳转到复习界面
                Intent intent_review = new Intent(mActivity, ReviewActivity.class);
                intent_review.putExtra("backgroundNum", num);
                startActivity(intent_review);
                break;
            default:
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
