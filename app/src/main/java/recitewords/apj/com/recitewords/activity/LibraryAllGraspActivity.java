package recitewords.apj.com.recitewords.activity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.fragment.AllGraspFragment;
import recitewords.apj.com.recitewords.fragment.AllReviewingFragment;
import recitewords.apj.com.recitewords.globle.AppConfig;
import recitewords.apj.com.recitewords.util.PrefUtils;

public class LibraryAllGraspActivity extends BaseActivity implements View.OnClickListener {
    private static final String ALLHaveGrasp = "all_have_grasp";

    public class ViewHolder {
        TextView library_have_grasp_close;  //关闭
        TextView library_have_grasp_info;  //说明
        FrameLayout library_grasp_fl;  //本词书所有以学习的单词
    }

    private ViewHolder holder;
    private AllGraspFragment mAllHaveGraspFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_all_grasp);

        initView();
        initEvent();
        initDate();
    }

    private void initView() {
        holder = new ViewHolder();
        holder.library_have_grasp_close = findViewByIds(R.id.library_have_grasp_close);
        holder.library_have_grasp_info = findViewByIds(R.id.library_have_grasp_info);
        holder.library_grasp_fl = findViewByIds(R.id.library_learn_fl);
    }

    private void initEvent() {
        holder.library_have_grasp_close.setOnClickListener(this);
        holder.library_have_grasp_info.setOnClickListener(this);
    }

    private void initDate() {

        SharedPreferences pref = PrefUtils.getPref(this);
        boolean new_words = pref.getBoolean("NEW_WORDS", false);
        if (new_words){
            mAllHaveGraspFragment = new AllGraspFragment(AppConfig.MODE_BOOK_NAME_AND_NEWWORDS);
        }else {
            mAllHaveGraspFragment = new AllGraspFragment(AppConfig.MODE_LIBRARY_GRASP);

        }
//        mAllHaveGraspFragment = new AllGraspFragment(AppConfig.MODE_LIBRARY_GRASP);
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.library_grasp_fl, mAllHaveGraspFragment, ALLHaveGrasp);
        ft.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.library_have_grasp_close:
                //关闭当前窗口
                finish();
                break;
            case R.id.library_have_grasp_info:
                //提示说明
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("温馨提示：");
                builder.setMessage("复习中:已学习单完成复习\n已掌握:已完全掌握，" +
                        "不用再复习\n\n1.点击单词查看词义\n2.已掌握的单词如果有遗忘，可以向左滑动单词后选择“重学”");
                builder.setPositiveButton("臣妾知道啦",null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            default:
                break;
        }
    }
}
