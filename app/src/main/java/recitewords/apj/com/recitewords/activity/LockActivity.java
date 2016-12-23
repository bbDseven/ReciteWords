package recitewords.apj.com.recitewords.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.adapter.MyLockWordRecycleViewAdpter;
import recitewords.apj.com.recitewords.bean.WordStudy;
import recitewords.apj.com.recitewords.db.dao.WordStudyDao;
import recitewords.apj.com.recitewords.util.DateUtil;

/**
 * 用于显示锁屏界面
 * Created by CJZ on 2016/12/21 0021.
 */
public class LockActivity extends BaseActivity {

    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("LockActivity","锁屏界面");
        setContentView(R.layout.activity_lock);
        initView();
        showInfo();
        initWordRecycleViewData();
    }

    public void initView(){
        viewHolder = new ViewHolder();
        viewHolder.ll_lock = findViewByIds(R.id.ll_lock);
        viewHolder.lock_time = findViewByIds(R.id.lock_time);
        viewHolder.lock_date = findViewByIds(R.id.lock_date);
        viewHolder.lock_word_recyclerView = findViewByIds(R.id.lock_word_recyclerView);
        viewHolder.wordView = new ArrayList<>();
    }

    /**
     *  用于显示锁屏界面背景图片，设置透明度，显示时间，日期
     * */
    public void showInfo(){
        viewHolder.ll_lock.setBackgroundResource(R.mipmap.haixin_bg_dim_01);//显示锁屏界面背景图片
        viewHolder.ll_lock.getBackground().setAlpha(170);//设置透明度
        viewHolder.lock_time.setText(DateUtil.getTime());//显示时间
        viewHolder.lock_date.setText(DateUtil.getNowDate("yyyy-MM-dd")+" "+DateUtil.getWeeks());//显示日期
    }

    /**
     * 用于显示单词的RecycleView
     * */
    public void initWordRecycleViewData(){
        WordStudyDao wordStudyDao = new WordStudyDao(LockActivity.this);
        viewHolder.wordView = wordStudyDao.getWordStudy();
        viewHolder.lock_word_recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.HORIZONTAL));
        MyLockWordRecycleViewAdpter myLockWordRecycleViewAdpter = new MyLockWordRecycleViewAdpter(LockActivity.this,viewHolder.wordView);
        viewHolder.lock_word_recyclerView.setAdapter(myLockWordRecycleViewAdpter);
    }

    public class ViewHolder{
        private LinearLayout ll_lock;    //锁屏界面的跟布局
        private TextView lock_time,lock_date; //时间，日期
        private RecyclerView lock_word_recyclerView;  //学习单词的ViewPager
        private ArrayList<WordStudy> wordView;
    }

}
