package recitewords.apj.com.recitewords.activity;

import android.app.Activity;
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
import recitewords.apj.com.recitewords.globle.AppConfig;
import recitewords.apj.com.recitewords.util.PrefUtils;

public class NewWordsActivity extends BaseActivity implements View.OnClickListener {
    private static final String ALLNEWWORDS = "all_new_words";

    public class ViewHolder {
        TextView library_new_words_close;  //关闭
        TextView library_new_words_info;  //说明
        FrameLayout library_new_words_fl;  //本词书所有以学习的单词
    }

    private ViewHolder holder;
    private AllGraspFragment mAllNewWordsFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_words);

        initView();
        initEvent();
        initDate();
    }

    private void initView() {
        holder = new ViewHolder();
        holder.library_new_words_close = findViewByIds(R.id.library_new_words_close);
        holder.library_new_words_info = findViewByIds(R.id.library_new_words_info);
        holder.library_new_words_fl = findViewByIds(R.id.library_new_words_fl);
    }

    private void initEvent() {
        holder.library_new_words_close.setOnClickListener(this);
        holder.library_new_words_info.setOnClickListener(this);
    }

    private void initDate() {


        mAllNewWordsFragment = new AllGraspFragment(AppConfig.BOOK_NEW_WORDS);

        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.library_new_words_fl, mAllNewWordsFragment, ALLNEWWORDS);
        ft.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.library_new_words_close:
                //关闭当前窗口
                finish();
                break;
            case R.id.library_new_words_info:
                //提示说明
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("温馨提示：");
                builder.setMessage("向左滑动后悔出现”删除“按钮，点击删除按钮可以将单词从生词本中删除");
                builder.setPositiveButton("臣妾知道啦", null);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                break;
            default:
                break;
        }
    }
}
