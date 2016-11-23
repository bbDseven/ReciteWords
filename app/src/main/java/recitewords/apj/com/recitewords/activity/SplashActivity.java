package recitewords.apj.com.recitewords.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import recitewords.apj.com.recitewords.R;

public class SplashActivity extends BaseActivity {
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //3秒后跳转到主页面
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        },3000);
    }
}
