package recitewords.apj.com.recitewords.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import net.youmi.android.normal.banner.BannerManager;
import net.youmi.android.normal.banner.BannerViewListener;
import net.youmi.android.offers.OffersManager;

import java.util.ArrayList;
import java.util.List;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.activity.LibraryAllGraspActivity;
import recitewords.apj.com.recitewords.activity.LibraryAllLearnActivity;
import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.activity.NewWordsActivity;
import recitewords.apj.com.recitewords.activity.ShowAllLearnActivity;
import recitewords.apj.com.recitewords.activity.ShowTodayLearnActivity;
import recitewords.apj.com.recitewords.adapter.MyViewPagerAdapter;
import recitewords.apj.com.recitewords.bean.Book;
import recitewords.apj.com.recitewords.bean.User;
import recitewords.apj.com.recitewords.db.dao.BookDao;
import recitewords.apj.com.recitewords.db.dao.UserDao;
import recitewords.apj.com.recitewords.globle.AppConfig;
import recitewords.apj.com.recitewords.util.DateUtil;
import recitewords.apj.com.recitewords.util.PrefUtils;
import recitewords.apj.com.recitewords.view.SlideSwitch;

/**
 * Created by CGT on 2016/11/22.
 * <p/>
 * 滑动页面Fragment
 */
public class SlidingFragment extends BaseFragment {

    private class holder {
        private ViewPager menu_viewPager;//用于显示4个子菜单界面的ViewPager
        private View view_settings, view_themes, view_library, view_statistics; //4个ViewPager里面的View
        private ImageView img_set, img_the, img_lib, img_sta, cursor; //4张图片，和一张显示游标的图片
        private TextView text_set, text_the, text_lib, text_sta;// 4个TextView菜单的名称
        private LinearLayout ll_setting, ll_theme, ll_library, ll_statistics;  //菜单导航栏
        private LinearLayout ll_top;  //导航栏父控件
        private LinearLayout sliding_layout_ll; //根布局用于设置背景颜色
        //-------------------- 设置
        TextView settings_download;  // 下载
        TextView settings_clean;  // 清除
        //拼写测试开关，锁屏学单词开关，自动发音开关
        SlideSwitch settings_spell_test, settings_lock_learn, settings_automatic_play;
        LinearLayout settings_Reset;  //重置当前词书
        //--------------------统计
        TextView statistics_tv_today_learn;  //查看今日已学单词
        TextView statistics_tv_today_sum;  //显示今日已学习个数
        TextView statistics_tv_all_sum;  //显示全部已学习个数
        TextView statistics_tv_all_learn; //查看全部已学单词
        TextView statistics_tv_sign_look;   //查看签到
        TextView statistics_tv_sign;    //显示签到天数
        TextView statistics_tv_money_add; //增加酷币
        TextView statistics_tv_money;   //显示剩余酷币

        //-----------------LIBRARY图书馆
        LinearLayout show_wordbook; //选中词书背景
        RelativeLayout library_have_learn;  //点击查看已学习
        LinearLayout library_have_grasp;  //点击查看已掌握
        TextView library_word_sum;  //词书单词总数
        TextView library_have_learn_sum;  //显示已学习总数
        TextView library_have_grasp_sum;  //显示已掌握总数
        ImageView library_add_words;   //生词本打勾
        ImageView library_add_words_tick;   //生词本打勾
        TextView library_new_words_sum;  //显示单词数
        TextView library_new_words_see;  //查看生词
        TextView library_book_name;   //选中的词书名字


    }

    private SignBroadcast receiver;//点击签到发出广播更新数据
    private static final String TAG = "SlidingFragment";
    private static final String FRAGMENT_MAIN = "fragment_main";  //主页面Fragment标识,Tag
    private TextView learn_num;//主界面学习数字
    private TextView review_num;//主界面复习数字
    public Context mContext;
    private int offset = 0;// 动画图片偏移量
    private int currIndex = 0;// 当前页卡编号
    private int bmpW;// 动画图片宽度

    private MainActivity mainActivity;
    private List<View> viewList;//用于存放4个View的集合
    private holder holder;
    private SlidingUpPanelLayout.PanelState panelState;  //滑动页面状态
    private int clickSlidinMenugSum = 1;  //一个菜单的点击次数
    private int oldClickIndex = -1;  //上一次点击的菜单位置
    private int NavigateHeight = 0;  //导航栏高度
    private RadioButton rb_gold;    //主题页面的金色按钮
    private RadioButton rb_night;
    private RadioButton rb_ocean;
    private RadioButton rb_green;   //主题页面的绿色按钮
    private RadioButton rb_pink;
    private RadioButton rb_purple;  //主题页面的紫色按钮
    private SharedPreferences sp;

    private List<Book> haveLearnList;  //当前词书已学习
    private List<Book> haveGraspList;  //当前词书已掌握
    private List<Book> newWordsList;  //生词本全部单词
    private List<Book> newWordHaveLearnList;  //生词本已学习
    private List<Book> newWordHaveGraspList;  //生词本已掌握

    public SlidingFragment(Context context) {
        this.mContext = context;
    }

    public SlidingFragment() {
    }

    @Override
    public View initView() {
        OffersManager.getInstance(mActivity).onAppExit();//广告积分墙资源释放
        final View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_sliding, null);
        holder = new holder();
        mainActivity = (MainActivity) mActivity;

        holder.menu_viewPager = findViewByIds(view, R.id.menu_viewpager);
        holder.img_set = findViewByIds(view, R.id.img_set);
        holder.img_the = findViewByIds(view, R.id.img_the);
        holder.img_lib = findViewByIds(view, R.id.img_lib);
        holder.img_sta = findViewByIds(view, R.id.img_sta);
        holder.text_set = findViewByIds(view, R.id.text_set);
        holder.text_the = findViewByIds(view, R.id.text_the);
        holder.text_lib = findViewByIds(view, R.id.text_lib);
        holder.text_sta = findViewByIds(view, R.id.text_sta);
        holder.cursor = findViewByIds(view, R.id.cursor);
        holder.ll_setting = findViewByIds(view, R.id.ll_setting);
        holder.ll_theme = findViewByIds(view, R.id.ll_theme);
        holder.ll_library = findViewByIds(view, R.id.ll_library);
        holder.ll_statistics = findViewByIds(view, R.id.ll_statistics);
        holder.sliding_layout_ll = findViewByIds(view, R.id.sliding_layout_ll);


        getHeight(view);
        InitImageView();
        InitViewPager();

        MainFragment mainFragment = (MainFragment) getFragmentManager().findFragmentByTag(FRAGMENT_MAIN);
        learn_num = mainFragment.getTV_learn_num();//主界面学习数字
        review_num = mainFragment.getTV_review_num();//主界面复习数字

        //通过用户选择的主题，重新进入应用时将背景设置为相应的颜色
        String theme = PrefUtils.getThemes(sp, "themes", "gold");
        if ("gold".equals(theme)) {
            holder.sliding_layout_ll.setBackgroundResource(R.color.Golden_Eye);
            setTextColor(learn_num);
            setTextColor(review_num);
            setGoldNum();
        } else if ("night".equals(theme)) {
            holder.sliding_layout_ll.setBackgroundResource(R.color.Night_Hawks);
            setTextColor(learn_num);
            setTextColor(review_num);
            setNightNum();
        } else if ("ocean".equals(theme)) {
            holder.sliding_layout_ll.setBackgroundResource(R.color.Ocean_Deep);
            setTextColor(learn_num);
            setTextColor(review_num);
            setOceanNum();
        } else if ("green".equals(theme)) {
            holder.sliding_layout_ll.setBackgroundResource(R.color.Green_Peace);
            setTextColor(learn_num);
            setTextColor(review_num);
            setGreenNum();
        } else if ("pink".equals(theme)) {
            holder.sliding_layout_ll.setBackgroundResource(R.color.Pinky_Girl);
            setTextColor(learn_num);
            setTextColor(review_num);
            setPinkNum();
        } else if ("purple".equals(theme)) {
            holder.sliding_layout_ll.setBackgroundResource(R.color.Purple_Viola);
            setTextColor(learn_num);
            setTextColor(review_num);
            setPurpleNum();
        }
        return view;
    }


    @Override
    public void initData() {
        super.initData();
        //注册一个内容观察者，监听数据库内容的改变
        ContentResolver cr = mActivity.getContentResolver();
        cr.registerContentObserver(Uri.parse("content://recitewords.apj.com.recitewords"),
                true, new MyObserver(new Handler()));
    }

    /**
     * 内容监听（监听数据库数据变化）
     */
    class MyObserver extends ContentObserver {

        public MyObserver(Handler handler) {
            super(handler);
        }

        //收到数据改变的通知，此方法调用
        @Override
        public void onChange(boolean selfChange) {
            // TODO Auto-generated method stub
            super.onChange(selfChange);
            BookDao bookDao = new BookDao(mActivity);
            newWordsList = bookDao.queryAllWOrd(AppConfig.BOOK_NEW_WORDS);  //生词本全部单词
            holder.library_new_words_sum.setText("单词数" + newWordsList.size());
        }

    }

    /**
     * 获取菜单导航栏高度，设置用户向上的最大高度
     *
     * @param view 所在的父布局
     */
    public void getHeight(final View view) {
        //获取菜单导航栏高度，设置用户向上的最大高度
        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                view.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        holder.ll_top = findViewByIds(view, R.id.ll_top);
                        NavigateHeight = holder.ll_top.getHeight();
                        mainActivity.setNavigateHeight(NavigateHeight);
                    }
                }, 300);
                view.getViewTreeObserver()
                        .removeOnGlobalLayoutListener(this);  //移除监听
            }
        });
    }


    /**
     * 初始化游标图片
     */
    private void InitImageView() {
        bmpW = BitmapFactory.decodeResource(getResources(), R.mipmap.cursor).getWidth();// 获取图片宽度
        DisplayMetrics dm = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenW = dm.widthPixels;// 获取分辨率宽度
        offset = (screenW / 4 - bmpW) / 2;// 计算偏移量
        Matrix matrix = new Matrix();
        matrix.postTranslate(offset, 0);
        holder.cursor.setImageMatrix(matrix);// 设置动画初始位置
    }

    /**
     * 设置ViewPager
     */
    private void InitViewPager() {
        viewList = new ArrayList<View>();
        LayoutInflater inflater = mActivity.getLayoutInflater();
        holder.view_settings = inflater.inflate(R.layout.viewpager_settings, null);
        holder.view_themes = inflater.inflate(R.layout.viewpager_themes, null);
        holder.view_library = inflater.inflate(R.layout.viewpager_library, null);
        holder.view_statistics = inflater.inflate(R.layout.viewpager_statistics, null);
        setAdv();        //设置广告
        setThemes();    //设置主题
        //把4个View都放在集合里
        viewList.add(holder.view_settings);
        viewList.add(holder.view_themes);
        viewList.add(holder.view_library);
        viewList.add(holder.view_statistics);
        MyViewPagerAdapter myViewPager = new MyViewPagerAdapter(mActivity, viewList);
        holder.menu_viewPager.setAdapter(myViewPager);
        holder.menu_viewPager.setCurrentItem(0);

        statistics(holder.view_statistics);  //统计
        library(holder.view_library);  //图书馆
        settings(holder.view_settings); //设置
        holder.menu_viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
    }

    /**
     * 设置SETTINGS
     *
     * @param view view
     */
    private void settings(View view) {
        init_SettView(view);//初始化 设置SETTINGS  的控件
        init_SettEvent();//设置SETTINGS 各个控件点击事件
    }

    /**
     * 初始化 设置SETTINGS  的控件
     */
    public void init_SettView(View view) {
        holder.settings_download = findViewByIds(view, R.id.settings_download);//下载
        holder.settings_clean = findViewByIds(view, R.id.settings_clean);//清除
        holder.settings_spell_test = findViewByIds(view, R.id.settings_spell_test);//拼写测试开关
        holder.settings_lock_learn = findViewByIds(view, R.id.settings_lock_learn);//锁屏学单词开关
        holder.settings_automatic_play = findViewByIds(view, R.id.settings_automatic_play);//自动发音开关
        holder.settings_Reset = findViewByIds(view, R.id.settings_Reset);  //重置当前词书
    }

    /**
     * 设置SETTINGS 各个控件点击事件
     */
    public void init_SettEvent() {
        holder.settings_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "下载的方法", Toast.LENGTH_SHORT).show();
            }
        });
        holder.settings_spell_test.setOnStateChangedListener(new SlideSwitch.OnStateChangedListener() {
            @Override
            public void onStateChanged(boolean state) {
                if (state == true) {
                    Toast.makeText(mContext, "拼写测试开关为开", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "拼写测试开关为关", Toast.LENGTH_LONG).show();
                }
            }
        });


        holder.settings_Reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences pref = PrefUtils.getPref(mActivity);
                final boolean new_words = PrefUtils.getDBFlag(pref, "NEW_WORDS", false);
                if (new_words) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("重置学习记录！");
                    builder.setMessage("[" + AppConfig.BOOK_NAME  +
                            "]中有" + haveGraspList.size() + "个已学的单词,有" + haveGraspList.size() +
                            "个已掌握单词;[生词本]中有" + newWordHaveLearnList.size() + "个已学的单词,有"
                            + newWordHaveGraspList.size() + "个已掌握单词。是否要重置这些单词的学习记录？");
                    builder.setPositiveButton("重置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //再次弹窗询问用户是否重置
                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                            builder.setTitle("温馨提示：");
                            builder.setMessage("此操作会将词书中所有的学习记录归零，你真的要这么做吗！！");
                            builder.setNegativeButton("取消", null);
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //重置当前词书
                                    BookDao bookDao = new BookDao(mActivity);
                                    int bookSum = bookDao.updateResetBook(AppConfig.BOOK_NAME);
                                    int newBookSum = bookDao.updateResetBook(AppConfig.BOOK_NEW_WORDS);
                                    if (bookSum > 0 && newBookSum > 0) {
                                        Toast.makeText(mActivity, "重置成功，请重新学习",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.create();
                            builder.show();
                        }
                    });
                    builder.setNegativeButton("不重置", null);
                    builder.create();
                    builder.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("重置学习记录！");
                    builder.setMessage("[" + AppConfig.BOOK_NAME  +
                            "]中有" + haveGraspList.size() + "个已学的单词,有" + haveGraspList.size() +
                            "个已掌握单词。是否要重置这些单词的学习记录？");
                    builder.setPositiveButton("重置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //再次弹窗询问用户是否重置
                            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                            builder.setTitle("温馨提示：");
                            builder.setMessage("此操作会将词书中所有的学习记录归零，你真的要这么做吗！！");
                            builder.setNegativeButton("取消", null);
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //重置当前词书
                                    BookDao bookDao = new BookDao(mActivity);
                                    int bookSum = bookDao.updateResetBook(AppConfig.BOOK_NAME);
                                    if (bookSum > 0) {
                                        Toast.makeText(mActivity, "重置成功，请重新学习",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.create();
                            builder.show();
                        }
                    });
                    builder.setNegativeButton("不重置", null);
                    builder.create();
                    builder.show();
                }

            }
        });
    }

    /**
     * 为了保证每次返回页面时刷新数据
     */
    @Override
    public void onResume() {
        super.onResume();
        init_StatData();
        init__LibraryData();
        IntentFilter filter = new IntentFilter();
        filter.addAction("recitewords.apj.com.recitewords.fragment.SlidingFragment.SignBroadcast");
        receiver = new SignBroadcast();//点击签到发出广播更新数据
        mActivity.registerReceiver(receiver, filter);
    }

    /**
     * 统计Viewpager
     *
     * @param view view
     */
    private void statistics(View view) {
        init_StatView(view);
//        init_StatData();
    }

    /**
     * 初始化统计的UI
     */
    public void init_StatView(View view) {
        holder.statistics_tv_today_learn = findViewByIds(view, R.id.statistics_tv_today_learn);
        holder.statistics_tv_today_sum = findViewByIds(view, R.id.statistics_tv_today_sum);
        holder.statistics_tv_all_learn = findViewByIds(view, R.id.statistics_tv_all_learn);
        holder.statistics_tv_all_sum = findViewByIds(view, R.id.statistics_tv_all_sum);
        holder.statistics_tv_sign_look = findViewByIds(view, R.id.statistics_tv_sign_look);
        holder.statistics_tv_sign = findViewByIds(view, R.id.statistics_tv_sign);
        holder.statistics_tv_money_add = findViewByIds(view, R.id.statistics_tv_money_add);
        holder.statistics_tv_money = findViewByIds(view, R.id.statistics_tv_money);
    }

    /**
     * 初始化统计的数据
     */
    public void init_StatData() {
        BookDao bookDao = new BookDao(mContext);
        final List<Book> mLearnList = bookDao.queryDayLearn(DateUtil.getNowDate("yyyy-MM-dd")); //今日已学
        final List<Book> mGraspList = bookDao.queryAllLearn("");  //全部已学
        holder.statistics_tv_today_sum.setText(mLearnList.size() + "个");  //今日已学单词总数
        holder.statistics_tv_all_sum.setText(mGraspList.size() + "个");  //全部已学单词总数

        UserDao userDao = new UserDao(mContext);
        User user = userDao.query();
        holder.statistics_tv_sign.setText(user.getSign_in_continue() + "天");    //设置连续签到天数
        holder.statistics_tv_money.setText(user.getCool_money() + "枚");     //设置剩余酷币枚数
        //点击查看签到
        holder.statistics_tv_sign_look.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.holder.mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });
        //点击增加酷币
        holder.statistics_tv_money_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("增加酷币");
                builder.setMessage("学习一个单词消耗一枚酷币，你可以采取以下两种方式增加酷币。");
                builder.setPositiveButton("支付宝购买", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mActivity, "加我微信985115104", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNeutralButton("免费获得", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(mActivity, "每天签到即可", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
        holder.statistics_tv_today_learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示今天学习单词
                Intent intent = new Intent(mActivity, ShowTodayLearnActivity.class);
                intent.putExtra("TodayLearnSum", mLearnList.size());
                startActivity(intent);
            }
        });

        holder.statistics_tv_all_learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //显示全部学习单词
//                Log.e("ha","全部已学习单词哈哈哈"+mGraspList.size());
                startActivity(new Intent(mActivity, ShowAllLearnActivity.class));
            }
        });
    }


    /**
     * 图书馆Viewpager
     *
     * @param view view
     */
    private void library(View view) {
        init_LibraryView(view);
//        init__LibraryData();
    }


    /**
     * 初始化图书馆的UI
     */
    public void init_LibraryView(View view) {
        holder.show_wordbook = findViewByIds(view, R.id.show_wordbook);
        holder.library_have_learn = findViewByIds(view, R.id.library_have_learn);
        holder.library_have_grasp = findViewByIds(view, R.id.library_have_grasp);
        holder.library_word_sum = findViewByIds(view, R.id.library_word_sum);
        holder.library_have_learn_sum = findViewByIds(view, R.id.library_have_learn_sum);
        holder.library_have_grasp_sum = findViewByIds(view, R.id.library_have_grasp_sum);
        holder.library_add_words = findViewByIds(view, R.id.library_add_words);
        holder.library_add_words_tick = findViewByIds(view, R.id.library_add_words_tick);
        holder.library_new_words_sum = findViewByIds(view, R.id.library_new_words_sum);
        holder.library_new_words_see = findViewByIds(view, R.id.library_new_words_see);
        holder.library_book_name = findViewByIds(view, R.id.library_book_name);
    }

    /**
     * 初始化图书馆的数据
     */
    public void init__LibraryData() {
        final BookDao bookDao = new BookDao(mActivity);
        haveLearnList = bookDao.queryAllLearn(AppConfig.BOOK_NAME);  //当前词书已学习
        haveGraspList = bookDao.queryAllGrasp(AppConfig.BOOK_NAME);  //当前词书已掌握
        newWordsList = bookDao.queryAllWOrd(AppConfig.BOOK_NEW_WORDS);  //生词本全部单词
        newWordHaveLearnList = bookDao.queryAllLearn(AppConfig.BOOK_NEW_WORDS);  //生词本已学习
        newWordHaveGraspList = bookDao.queryAllGrasp(AppConfig.BOOK_NEW_WORDS);  //生词本已掌握
        final SharedPreferences pref = PrefUtils.getPref(mActivity);
        boolean new_words = PrefUtils.getDBFlag(pref, "NEW_WORDS", false);
        if (newWordsList.size()==0){  //删除完单词后，进入没有选择状态
            PrefUtils.setDBFlag(pref,"NEW_WORDS",false);
            new_words=false;
        }

        //设置显示已学习已掌握总数
        holder.library_have_learn_sum.setText(haveLearnList.size() + "");
        holder.library_have_grasp_sum.setText(haveGraspList.size() + "");
        //显示生词总数
        holder.library_new_words_sum.setText("单词数" + newWordsList.size());
        //打开已学习
        holder.library_have_learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, LibraryAllLearnActivity.class));
            }
        });
        //打开已掌握
        holder.library_have_grasp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mActivity, LibraryAllGraspActivity.class));
            }
        });
        if (new_words) {
            holder.library_add_words_tick.setVisibility(View.VISIBLE);
            holder.library_book_name.setText(AppConfig.BOOK_NAME  + " + 生词本");
            //设置显示已学习已掌握总数
            holder.library_have_learn_sum.setText((haveLearnList.size() + newWordHaveLearnList.size()) + "");
            holder.library_have_grasp_sum.setText((haveGraspList.size() + newWordHaveGraspList.size()) + "");
            //显示生词总数
            holder.library_new_words_sum.setText("单词数"+newWordsList.size());
        }else {
            holder.library_add_words_tick.setVisibility(View.GONE);
            holder.library_new_words_sum.setText("单词数" + newWordsList.size());
            //设置显示已学习已掌握总数
            holder.library_book_name.setText(AppConfig.BOOK_NAME);
            holder.library_have_learn_sum.setText(haveLearnList.size() + "");
            holder.library_have_grasp_sum.setText(haveGraspList.size() + "");
            //显示生词总数
            holder.library_new_words_sum.setText("单词数" + newWordsList.size());
        }

        holder.library_add_words.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean new_words = PrefUtils.getDBFlag(pref, "NEW_WORDS", false);
                if (newWordsList.size() <= 0) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("温馨提醒：");
                    builder.setMessage("你的生词本还没生词，赶快去添加生词吧");
                    builder.setPositiveButton("确定", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else if (new_words) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("温馨提醒：");
                    builder.setMessage("是否取消学习生词本里的单词");
                    builder.setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PrefUtils.setDBFlag(pref, "NEW_WORDS", false);
                            holder.library_add_words_tick.setVisibility(View.GONE);
                            holder.library_book_name.setText(haveLearnList.get(0).getBook_name());
                            holder.library_book_name.setText(AppConfig.BOOK_NAME );
                            //设置显示已学习已掌握总数
                            holder.library_have_learn_sum.setText(haveLearnList.size() + "");
                            holder.library_have_grasp_sum.setText(haveGraspList.size() + "");
                            //显示生词总数
                            holder.library_new_words_sum.setText("单词数" + newWordsList.size());

                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("温馨提醒：");
                    builder.setMessage("是否学习生词本里的单词");
                    builder.setNegativeButton("取消", null);
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PrefUtils.setDBFlag(pref, "NEW_WORDS", true);
                            holder.library_add_words_tick.setVisibility(View.VISIBLE);
                            holder.library_book_name.setText(haveLearnList.get(0).getBook_name() + " + 生词本");
                            //设置显示已学习已掌握总数
                            holder.library_have_learn_sum.setText((haveLearnList.size() + newWordHaveLearnList.size()) + "");
                            holder.library_have_grasp_sum.setText((haveGraspList.size() + newWordHaveGraspList.size()) + "");
                            //显示生词总数
                            holder.library_new_words_sum.setText("单词数" + newWordsList.size());
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });

        holder.library_new_words_see.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newWordsList.size() <= 0) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    builder.setTitle("温馨提醒：");
                    builder.setMessage("你的生词本还没生词，赶快去添加生词吧");
                    builder.setPositiveButton("确定", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    startActivity(new Intent(mActivity, NewWordsActivity.class));
                }
            }
        });

    }


    @Override
    public void initEvent() {
        holder.ll_setting.setOnClickListener(new MyOnClickListener(0));
        holder.ll_theme.setOnClickListener(new MyOnClickListener(1));
        holder.ll_library.setOnClickListener(new MyOnClickListener(2));
        holder.ll_statistics.setOnClickListener(new MyOnClickListener(3));
    }

    /**
     * 头部点击监听
     */
    public class MyOnClickListener implements View.OnClickListener {
        private int index = 0;

        public MyOnClickListener(int i) {
            index = i;
        }

        @Override
        public void onClick(View v) {
            panelState = mainActivity.holder.mLayout.getPanelState();
            if (panelState == SlidingUpPanelLayout.PanelState.COLLAPSED ||
                    panelState == SlidingUpPanelLayout.PanelState.ANCHORED) {
                mainActivity.holder.mLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                //当用户点击返回首页后，再次点击导航栏，进入滑动菜单页面，
                // 返回时和再次进去同一个页面就需要设置字体颜色
                switch (v.getId()) {
                    case R.id.ll_setting:
//                        holder.text_set.setTextColor(Color.CYAN);
                        setTextColor(holder.text_set);
                        break;
                    case R.id.ll_theme:
//                        holder.text_the.setTextColor(Color.CYAN);
                        setTextColor(holder.text_the);
                        break;
                    case R.id.ll_library:
//                        holder.text_lib.setTextColor(Color.CYAN);
                        setTextColor(holder.text_lib);
                        break;
                    case R.id.ll_statistics:
//                        holder.text_sta.setTextColor(Color.CYAN);
                        setTextColor(holder.text_sta);
                        break;
                }
            } else if (panelState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                if (oldClickIndex == index) {
                    mainActivity.holder.mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    oldClickIndex = -1;
                    clickSlidinMenugSum = 1;
                }
            }
            holder.menu_viewPager.setCurrentItem(index);
            oldClickIndex = index;
            clickSlidinMenugSum++;
        }


    }

    /**
     * 页卡切换监听
     */
    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            TextColorChange(position);
            cursor_move(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }


    /**
     * 文字颜色改变的方法
     */
    public void TextColorChange(int position) {
        switch (position) {
            case 0:
                setTextColor(holder.text_set);
                holder.text_the.setTextColor(Color.WHITE);
                holder.text_lib.setTextColor(Color.WHITE);
                holder.text_sta.setTextColor(Color.WHITE);
                break;
            case 1:
                holder.text_set.setTextColor(Color.WHITE);
                setTextColor(holder.text_the);
                holder.text_lib.setTextColor(Color.WHITE);
                holder.text_sta.setTextColor(Color.WHITE);
                break;
            case 2:
                holder.text_set.setTextColor(Color.WHITE);
                holder.text_the.setTextColor(Color.WHITE);
                setTextColor(holder.text_lib);
                holder.text_sta.setTextColor(Color.WHITE);
                break;
            case 3:
                holder.text_set.setTextColor(Color.WHITE);
                holder.text_the.setTextColor(Color.WHITE);
                holder.text_lib.setTextColor(Color.WHITE);
                setTextColor(holder.text_sta);
                break;
        }
    }

    /**
     * 设置文字全部为白色
     */
    public void setTextWhite() {
        holder.text_set.setTextColor(Color.WHITE);
        holder.text_the.setTextColor(Color.WHITE);
        holder.text_lib.setTextColor(Color.WHITE);
        holder.text_sta.setTextColor(Color.WHITE);
    }

    /**
     * 游标移动的方法
     */
    public void cursor_move(int position) {
        int one = offset * 2 + bmpW;   // 1 个页卡的偏移量
        int two = one * 2;              // 2 个页卡的偏移量
        int there = one * 3;            // 3 个页卡的偏移量

        Animation animation = null;
        switch (position) {
            case 0:
                if (currIndex == 1) {
                    animation = new TranslateAnimation(one, 0, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, 0, 0, 0);
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(there, 0, 0, 0);
                }
                break;
            case 1:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, one, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, one, 0, 0);
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(there, one, 0, 0);
                }
                break;
            case 2:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, two, 0, 0);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(one, two, 0, 0);
                } else if (currIndex == 3) {
                    animation = new TranslateAnimation(there, two, 0, 0);
                }
                break;
            case 3:
                if (currIndex == 0) {
                    animation = new TranslateAnimation(offset, there, 0, 0);
                } else if (currIndex == 1) {
                    animation = new TranslateAnimation(one, there, 0, 0);
                } else if (currIndex == 2) {
                    animation = new TranslateAnimation(two, there, 0, 0);
                }
                break;
        }
        currIndex = position;
        oldClickIndex = position;  //更新当前显示的index
        animation.setFillAfter(true);// True:图片停在动画结束位置
        animation.setDuration(300);
        holder.cursor.startAnimation(animation);
    }

    /**
     * 获取菜单导航栏高度
     *
     * @return
     */
    public int getNavigateHeight() {
        return NavigateHeight;
    }

    //设置广告条
    private void setAdv() {
        // 获取广告条
        View bannerView = BannerManager.getInstance(mActivity)
                .getBannerView(new BannerViewListener() {
                    @Override
                    public void onRequestSuccess() {

                    }

                    @Override
                    public void onSwitchBanner() {

                    }

                    @Override
                    public void onRequestFailed() {

                    }
                });
        LinearLayout ll_banner_settings = findViewByIds(holder.view_settings, R.id.ll_banner_settings);
        TextView app_recommend = findViewByIds(holder.view_statistics, R.id.app_recommend);
        app_recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OffersManager.getInstance(mActivity).showOffersWall();
            }
        });

        // 将广告条加入到布局中
        ll_banner_settings.addView(bannerView);
    }

    //设置主题页面的按钮事件
    private void setThemes() {
        RadioGroup rg = findViewByIds(holder.view_themes, R.id.themes_rg);
        rb_gold = findViewByIds(holder.view_themes, R.id.themes_rb_gold);
        rb_night = findViewByIds(holder.view_themes, R.id.themes_rb_night);
        rb_ocean = findViewByIds(holder.view_themes, R.id.themes_rb_ocean);
        rb_green = findViewByIds(holder.view_themes, R.id.themes_rb_green);
        rb_pink = findViewByIds(holder.view_themes, R.id.themes_rb_pink);
        rb_purple = findViewByIds(holder.view_themes, R.id.themes_rb_purple);
        final ImageView iv_gold = findViewByIds(holder.view_themes, R.id.themes_iv_gold);
        final ImageView iv_night = findViewByIds(holder.view_themes, R.id.themes_iv_night);
        final ImageView iv_ocean = findViewByIds(holder.view_themes, R.id.themes_iv_ocean);
        final ImageView iv_green = findViewByIds(holder.view_themes, R.id.themes_iv_green);
        final ImageView iv_pink = findViewByIds(holder.view_themes, R.id.themes_iv_pink);
        final ImageView iv_purple = findViewByIds(holder.view_themes, R.id.themes_iv_purple);

        sp = PrefUtils.getPref(mActivity);
        //通过用户选择的主题，重新进入应用时将主题的默认选中设置为相应的
        String theme = PrefUtils.getThemes(sp, "themes", "gold");
        if ("gold".equals(theme)) {
            rb_gold.setChecked(true);
            iv_gold.setVisibility(View.VISIBLE);
        } else if ("night".equals(theme)) {
            rb_night.setChecked(true);
            iv_night.setVisibility(View.VISIBLE);
        } else if ("ocean".equals(theme)) {
            rb_ocean.setChecked(true);
            iv_ocean.setVisibility(View.VISIBLE);
        } else if ("green".equals(theme)) {
            rb_green.setChecked(true);
            iv_green.setVisibility(View.VISIBLE);
        } else if ("pink".equals(theme)) {
            rb_pink.setChecked(true);
            iv_pink.setVisibility(View.VISIBLE);
        } else if ("purple".equals(theme)) {
            rb_purple.setChecked(true);
            iv_purple.setVisibility(View.VISIBLE);
        }

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.themes_rb_gold:
                        holder.sliding_layout_ll.setBackgroundResource(R.color.Golden_Eye);
                        iv_gold.setVisibility(View.VISIBLE);
                        iv_night.setVisibility(View.GONE);
                        iv_ocean.setVisibility(View.GONE);
                        iv_green.setVisibility(View.GONE);
                        iv_pink.setVisibility(View.GONE);
                        iv_purple.setVisibility(View.GONE);
                        holder.text_the.setTextColor(mActivity.getResources().getColor(R.color.gold));
                        setNumColor();
                        PrefUtils.setThemes(sp, "themes", "gold");
                        break;
                    case R.id.themes_rb_night:
                        holder.sliding_layout_ll.setBackgroundResource(R.color.Night_Hawks);
                        iv_gold.setVisibility(View.GONE);
                        iv_night.setVisibility(View.VISIBLE);
                        iv_ocean.setVisibility(View.GONE);
                        iv_green.setVisibility(View.GONE);
                        iv_pink.setVisibility(View.GONE);
                        iv_purple.setVisibility(View.GONE);
                        holder.text_the.setTextColor(mActivity.getResources().getColor(R.color.night));
                        setNumColor();
                        PrefUtils.setThemes(sp, "themes", "night");
                        break;
                    case R.id.themes_rb_ocean:
                        holder.sliding_layout_ll.setBackgroundResource(R.color.Ocean_Deep);
                        iv_gold.setVisibility(View.GONE);
                        iv_night.setVisibility(View.GONE);
                        iv_ocean.setVisibility(View.VISIBLE);
                        iv_green.setVisibility(View.GONE);
                        iv_pink.setVisibility(View.GONE);
                        iv_purple.setVisibility(View.GONE);
                        holder.text_the.setTextColor(mActivity.getResources().getColor(R.color.ocean));
                        setNumColor();
                        PrefUtils.setThemes(sp, "themes", "ocean");
                        break;
                    case R.id.themes_rb_green:
                        holder.sliding_layout_ll.setBackgroundResource(R.color.Green_Peace);
                        iv_gold.setVisibility(View.GONE);
                        iv_night.setVisibility(View.GONE);
                        iv_ocean.setVisibility(View.GONE);
                        iv_green.setVisibility(View.VISIBLE);
                        iv_pink.setVisibility(View.GONE);
                        iv_purple.setVisibility(View.GONE);
                        holder.text_the.setTextColor(mActivity.getResources().getColor(R.color.green));
                        setNumColor();
                        PrefUtils.setThemes(sp, "themes", "green");
                        break;
                    case R.id.themes_rb_pink:
                        holder.sliding_layout_ll.setBackgroundResource(R.color.Pinky_Girl);
                        iv_gold.setVisibility(View.GONE);
                        iv_night.setVisibility(View.GONE);
                        iv_ocean.setVisibility(View.GONE);
                        iv_green.setVisibility(View.GONE);
                        iv_pink.setVisibility(View.VISIBLE);
                        iv_purple.setVisibility(View.GONE);
                        holder.text_the.setTextColor(mActivity.getResources().getColor(R.color.pink));
                        setNumColor();
                        PrefUtils.setThemes(sp, "themes", "pink");
                        break;
                    case R.id.themes_rb_purple:
                        holder.sliding_layout_ll.setBackgroundResource(R.color.Purple_Viola);
                        iv_gold.setVisibility(View.GONE);
                        iv_night.setVisibility(View.GONE);
                        iv_ocean.setVisibility(View.GONE);
                        iv_green.setVisibility(View.GONE);
                        iv_pink.setVisibility(View.GONE);
                        iv_purple.setVisibility(View.VISIBLE);
                        holder.text_the.setTextColor(mActivity.getResources().getColor(R.color.purple));
                        setNumColor();
                        PrefUtils.setThemes(sp, "themes", "purple");
                        break;
                }
            }
        });
    }

    //设置主题页的文本颜色
    private void setTextColor(TextView tv) {
        if (rb_gold.isChecked()) {
            tv.setTextColor(mActivity.getResources().getColor(R.color.gold));
        } else if (rb_night.isChecked()) {
            tv.setTextColor(mActivity.getResources().getColor(R.color.night));
        } else if (rb_ocean.isChecked()) {
            tv.setTextColor(mActivity.getResources().getColor(R.color.ocean));
        } else if (rb_green.isChecked()) {
            tv.setTextColor(mActivity.getResources().getColor(R.color.green));
        } else if (rb_pink.isChecked()) {
            tv.setTextColor(mActivity.getResources().getColor(R.color.pink));
        } else if (rb_purple.isChecked()) {
            tv.setTextColor(mActivity.getResources().getColor(R.color.purple));
        } else {
            tv.setTextColor(Color.WHITE);
        }
    }
    //设置数字颜色
    private void setNumColor(){
        if (rb_gold.isChecked()) {
            setGoldNum();
        } else if (rb_night.isChecked()) {
            setNightNum();
        } else if (rb_ocean.isChecked()) {
            setOceanNum();
        } else if (rb_green.isChecked()) {
            setGreenNum();
        } else if (rb_pink.isChecked()) {
            setPinkNum();
        } else if (rb_purple.isChecked()) {
            setPurpleNum();
        } else {

        }
    }
    //黄色数字
    private void setGoldNum(){
        learn_num.setTextColor(mActivity.getResources().getColor(R.color.gold));
        review_num.setTextColor(mActivity.getResources().getColor(R.color.gold));
        holder.statistics_tv_today_sum.setTextColor(mActivity.getResources().getColor(R.color.gold));
        holder.statistics_tv_all_sum.setTextColor(mActivity.getResources().getColor(R.color.gold));
        holder.statistics_tv_sign.setTextColor(mActivity.getResources().getColor(R.color.gold));
        holder.statistics_tv_money.setTextColor(mActivity.getResources().getColor(R.color.gold));
        holder.library_word_sum.setTextColor(mActivity.getResources().getColor(R.color.gold));
        holder.library_have_learn_sum.setTextColor(mActivity.getResources().getColor(R.color.gold));
        holder.library_have_grasp_sum.setTextColor(mActivity.getResources().getColor(R.color.gold));
        holder.show_wordbook.setBackgroundColor(mActivity.getResources().getColor(R.color.library_gold));
    }
    //夜晚数字
    private void setNightNum(){
        learn_num.setTextColor(mActivity.getResources().getColor(R.color.night));
        review_num.setTextColor(mActivity.getResources().getColor(R.color.night));
        holder.statistics_tv_today_sum.setTextColor(mActivity.getResources().getColor(R.color.night));
        holder.statistics_tv_all_sum.setTextColor(mActivity.getResources().getColor(R.color.night));
        holder.statistics_tv_sign.setTextColor(mActivity.getResources().getColor(R.color.night));
        holder.statistics_tv_money.setTextColor(mActivity.getResources().getColor(R.color.night));
        holder.library_word_sum.setTextColor(mActivity.getResources().getColor(R.color.night));
        holder.library_have_learn_sum.setTextColor(mActivity.getResources().getColor(R.color.night));
        holder.library_have_grasp_sum.setTextColor(mActivity.getResources().getColor(R.color.night));
        holder.show_wordbook.setBackgroundColor(mActivity.getResources().getColor(R.color.library_night));
    }
    //海洋数字
    private void setOceanNum(){
        learn_num.setTextColor(mActivity.getResources().getColor(R.color.ocean));
        review_num.setTextColor(mActivity.getResources().getColor(R.color.ocean));
        holder.statistics_tv_today_sum.setTextColor(mActivity.getResources().getColor(R.color.ocean));
        holder.statistics_tv_all_sum.setTextColor(mActivity.getResources().getColor(R.color.ocean));
        holder.statistics_tv_sign.setTextColor(mActivity.getResources().getColor(R.color.ocean));
        holder.statistics_tv_money.setTextColor(mActivity.getResources().getColor(R.color.ocean));
        holder.library_word_sum.setTextColor(mActivity.getResources().getColor(R.color.ocean));
        holder.library_have_learn_sum.setTextColor(mActivity.getResources().getColor(R.color.ocean));
        holder.library_have_grasp_sum.setTextColor(mActivity.getResources().getColor(R.color.ocean));
        holder.show_wordbook.setBackgroundColor(mActivity.getResources().getColor(R.color.library_ocean));
    }
    //绿色数字
    private void setGreenNum(){
        learn_num.setTextColor(mActivity.getResources().getColor(R.color.green));
        review_num.setTextColor(mActivity.getResources().getColor(R.color.green));
        holder.statistics_tv_today_sum.setTextColor(mActivity.getResources().getColor(R.color.green));
        holder.statistics_tv_all_sum.setTextColor(mActivity.getResources().getColor(R.color.green));
        holder.statistics_tv_sign.setTextColor(mActivity.getResources().getColor(R.color.green));
        holder.statistics_tv_money.setTextColor(mActivity.getResources().getColor(R.color.green));
        holder.library_word_sum.setTextColor(mActivity.getResources().getColor(R.color.green));
        holder.library_have_learn_sum.setTextColor(mActivity.getResources().getColor(R.color.green));
        holder.library_have_grasp_sum.setTextColor(mActivity.getResources().getColor(R.color.green));
        holder.show_wordbook.setBackgroundColor(mActivity.getResources().getColor(R.color.library_green));
    }
    //粉色数字
    private void setPinkNum(){
        learn_num.setTextColor(mActivity.getResources().getColor(R.color.pink));
        review_num.setTextColor(mActivity.getResources().getColor(R.color.pink));
        holder.statistics_tv_today_sum.setTextColor(mActivity.getResources().getColor(R.color.pink));
        holder.statistics_tv_all_sum.setTextColor(mActivity.getResources().getColor(R.color.pink));
        holder.statistics_tv_sign.setTextColor(mActivity.getResources().getColor(R.color.pink));
        holder.statistics_tv_money.setTextColor(mActivity.getResources().getColor(R.color.pink));
        holder.library_word_sum.setTextColor(mActivity.getResources().getColor(R.color.pink));
        holder.library_have_learn_sum.setTextColor(mActivity.getResources().getColor(R.color.pink));
        holder.library_have_grasp_sum.setTextColor(mActivity.getResources().getColor(R.color.pink));
        holder.show_wordbook.setBackgroundColor(mActivity.getResources().getColor(R.color.library_pink));
    }
    //紫色数字
    private void setPurpleNum(){
        learn_num.setTextColor(mActivity.getResources().getColor(R.color.purple));
        review_num.setTextColor(mActivity.getResources().getColor(R.color.purple));
        holder.statistics_tv_today_sum.setTextColor(mActivity.getResources().getColor(R.color.purple));
        holder.statistics_tv_all_sum.setTextColor(mActivity.getResources().getColor(R.color.purple));
        holder.statistics_tv_sign.setTextColor(mActivity.getResources().getColor(R.color.purple));
        holder.statistics_tv_money.setTextColor(mActivity.getResources().getColor(R.color.purple));
        holder.library_word_sum.setTextColor(mActivity.getResources().getColor(R.color.purple));
        holder.library_have_learn_sum.setTextColor(mActivity.getResources().getColor(R.color.purple));
        holder.library_have_grasp_sum.setTextColor(mActivity.getResources().getColor(R.color.purple));
        holder.show_wordbook.setBackgroundColor(mActivity.getResources().getColor(R.color.library_purple));
    }
    //广播更新签到天数
    class SignBroadcast extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            UserDao userDao = new UserDao(mContext);
            User user = userDao.query();
            holder.statistics_tv_sign.setText(user.getSign_in_continue() + "天");    //设置连续签到天数
            holder.statistics_tv_money.setText(user.getCool_money() + "枚");     //设置剩余酷币枚数
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.unregisterReceiver(receiver);
    }
}
