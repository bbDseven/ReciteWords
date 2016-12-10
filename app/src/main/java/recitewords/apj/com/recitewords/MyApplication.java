package recitewords.apj.com.recitewords;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.ShareSDK;

/**
 * Created by CGT on 2016/11/22.
 */
public class MyApplication extends Application {

    public static List<Activity> mActivityList = new ArrayList<Activity>();

    @Override
    public void onCreate() {
        super.onCreate();

        ShareSDK.initSDK(getBaseContext(),"19cb550c85b26");
    }


    //提供一个添加activity的方法
    public void addActivity(Activity activity) {
        mActivityList.add(activity);

    }

    //提供一个移除activity的方法
    public void removeActivity(Activity activity) {
        mActivityList.remove(activity);
    }

    //提供一个清空集合的方法
    public void finishAllActivity() {
        /**
         * 关闭所有activity
         */
        for (Activity activity : mActivityList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
        mActivityList.clear();
    }


}
