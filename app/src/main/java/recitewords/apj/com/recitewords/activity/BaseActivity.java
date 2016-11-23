package recitewords.apj.com.recitewords.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import recitewords.apj.com.recitewords.MyApplication;

/**
 * Created by CGT on 2016/11/12.
 * <p>
 * Activity的基类
 */
public class BaseActivity extends Activity {

    public MyApplication mApplication = new MyApplication();

    protected void onCreate(Bundle savedInstanceState) {

        //写死横屏
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        //设置充满屏幕
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        requestWindowFeature(Window.FEATURE_NO_TITLE);//去标题

        mApplication.addActivity(this);

    }

    public <E extends View> E findViewByIds(int id) {
        return (E) findViewById(id);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mApplication.removeActivity(this);
    }
}
