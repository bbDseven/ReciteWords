package recitewords.apj.com.recitewords.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences 工具类
 * 下面SharedPreferences用sp代替
 * Created by Seven on 2016/11/29.
 */

public class PrefUtils {
    private final static String SP_NAME = "config";   //sp文件名字
    //获取sp
    public static SharedPreferences getPref(Context context){
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return sp;
    }
    //获取数据库是否插入数据的标记
    public static boolean getDBFlag(SharedPreferences sp, String key, boolean defValue){
        return sp.getBoolean(key, defValue);
    }
    //设置数据库是否插入数据的标记
    public static void setDBFlag(SharedPreferences sp, String key, boolean value){
        sp.edit().putBoolean(key, value).commit();
    }
}