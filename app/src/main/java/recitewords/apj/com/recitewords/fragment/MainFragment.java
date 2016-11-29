package recitewords.apj.com.recitewords.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.activity.MainActivity;
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
    //定义好的8张背景图id数组
    private int[] imgs = new int[]{R.mipmap.login_background_define01, R.mipmap.login_background_define02, R.mipmap.login_background_define03,
            R.mipmap.login_background_define04, R.mipmap.login_background_define05, R.mipmap.login_background_define06,
            R.mipmap.login_background_define07, R.mipmap.login_background_define08,};

    private class ViewHolder {
        RelativeLayout activity_main;
        TextView tv_word;
        LinearLayout linearLayout;
        ImageView img_sign;
        TextView tv_date;
        ImageView iv_menu;
    }

    private ViewHolder holder;
    private Context mContext;
    private boolean show_navigate_state = false;
    private MainActivity mainActivity;

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
        holder.tv_date = findViewByIds(view, R.id.main_tv_date);
        holder.iv_menu = findViewByIds(view, R.id.main_img_menu);
        return view;
    }

    @Override
    public void initEvent() {
        holder.iv_menu.setOnClickListener(this);
    }

    @Override
    public void initData() {
        //生成一个随机数
        int num = randomNum();
        //为主界面设置随机图片和图片对应单词
        holder.activity_main.setBackgroundResource(imgs[num]);
        holder.tv_word.setText(img_words[num]);
        //设置签到里的日期和星期
        String date = DateUtil.getMonthAndDay() + "" + DateUtil.getWeek();
        holder.tv_date.setText(date);

        SharedPreferences sp = PrefUtils.getPref(mainActivity);//获取sp
        boolean dbFlag = PrefUtils.getDBFlag(sp, "dbFlag", true);//获取sp中dbFlag的标记
        if (1==1){

            insertWordStudy();

            PrefUtils.setDBFlag(sp, "dbFlag", false);//插入完数据将标记设置为false，下次则不会再插入数据
        }
    }

    /**
     * 插入学习单词
     */
    public void insertWordStudy(){
        WordStudyDao dao = new WordStudyDao(mContext);
        dao.addWord("economic","","","","","","",0,"","CET4",0);
        dao.addWord("election","","","","","","",0,"","CET4",0);
        dao.addWord("murder","","","","","","",0,"","CET4",0);
        dao.addWord("progress","","","","","","",0,"","CET4",0);
        dao.addWord("religious","","","","","","",0,"","CET4",0);
        dao.addWord("smart","","","","","","",0,"","CET4",0);
        dao.addWord("barber","","","","","","",0,"","CET4",0);
        dao.addWord("animal","","","","","","",0,"","CET4",0);
        dao.addWord("abandon","","","","","","",0,"","CET4",0);
        dao.addWord("ability","","","","","","",0,"","CET4",0);
        dao.addWord("able","","","","","","",0,"","CET4",0);
        dao.addWord("aboard","","","","","","",0,"","CET4",0);
        dao.addWord("about","","","","","","",0,"","CET4",0);
        dao.addWord("abroad","","","","","","",0,"","CET4",0);
        dao.addWord("absorb","","","","","","",0,"","CET4",0);
        dao.addWord("angry","","","","","","",0,"","CET4",0);
        dao.addWord("anniversary","","","","","","",0,"","CET4",0);
        dao.addWord("announce","","","","","","",0,"","CET4",0);
        dao.addWord("bankrupt","","","","","","",0,"","CET4",0);
        dao.addWord("computer","","","","","","",0,"","CET4",0);
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
            default:
                break;
        }
    }

    /**
     * 生成0 - 7的随机数
     *
     * @return 随机数
     */
    private int randomNum() {
        int num = (int) (Math.random() * 8);
        return num;
    }

}
