package recitewords.apj.com.recitewords.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import recitewords.apj.com.recitewords.R;

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置充满屏幕
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
