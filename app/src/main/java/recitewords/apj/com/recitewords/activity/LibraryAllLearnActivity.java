package recitewords.apj.com.recitewords.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.fragment.AllReviewingFragment;
import recitewords.apj.com.recitewords.globle.AppConfig;

public class LibraryAllLearnActivity extends BaseActivity implements View.OnClickListener {
    private static final String ALLHaveLearn = "all_have_learn";

    public class ViewHolder {
        TextView library_have_learn_close;  //关闭
        FrameLayout library_learn_fl;  //本词书所有以学习的单词
    }

    private ViewHolder holder;
    private AllReviewingFragment mAllHaveLearnFragment;
    private FragmentManager fm;
    private FragmentTransaction ft;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_all_learn);

        initView();
        initEvent();
        initDate();
    }

    private void initView() {
        holder = new ViewHolder();
        holder.library_have_learn_close = findViewByIds(R.id.library_have_learn_close);
        holder.library_learn_fl = findViewByIds(R.id.library_learn_fl);

    }

    private void initEvent() {
        holder.library_have_learn_close.setOnClickListener(this);
    }

    private void initDate() {
        mAllHaveLearnFragment = new AllReviewingFragment(AppConfig.MODE_LIBRARY_LEARN);
        fm = getFragmentManager();
        ft = fm.beginTransaction();
        ft.replace(R.id.library_learn_fl, mAllHaveLearnFragment, ALLHaveLearn);
        ft.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.library_have_learn_close:
                //关闭当前窗口
                finish();
                break;
            default:
                break;
        }
    }
}
