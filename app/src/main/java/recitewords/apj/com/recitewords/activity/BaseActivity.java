package recitewords.apj.com.recitewords.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import recitewords.apj.com.recitewords.MyApplication;

/**
 * Created by CGT on 2016/11/12.
 *
 * Activity的基类
 */
public class BaseActivity extends AppCompatActivity {

    public MyApplication mApplication=new MyApplication();

    protected void onCreate(Bundle savedInstanceState) {

        //写死横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        //去标题
        if (getSupportActionBar() != null){
            getSupportActionBar().hide();
        }
        mApplication.addActivity(this);

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApplication.removeActivity(this);
    }
}
