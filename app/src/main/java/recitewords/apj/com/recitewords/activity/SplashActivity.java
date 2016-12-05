package recitewords.apj.com.recitewords.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import net.youmi.android.AdManager;
import net.youmi.android.offers.OffersManager;

import recitewords.apj.com.recitewords.R;

public class SplashActivity extends BaseActivity {
    private Handler mHandler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AdManager.getInstance(this).init("0256701d91637145", "20a7b9748d5e4712", true, true);//广告条注册
        OffersManager.getInstance(this).onAppLaunch();//广告积分墙注册
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
