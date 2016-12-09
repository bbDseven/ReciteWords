package recitewords.apj.com.recitewords.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

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

    //设置更换头像的uri
    public static void setImage(SharedPreferences sp, String key, String uri_string){
        sp.edit().putString(key, uri_string).commit();
    }
    //获取更换头像的uri
    public static String getImage(SharedPreferences sp, String key, String uri_string_def){
        return sp.getString(key, uri_string_def);
    }

    //设置主题的颜色
    public static void setThemes(SharedPreferences sp, String key, String themes){
        sp.edit().putString(key, themes).commit();
    }
    //获取主题的颜色
    public static String getThemes(SharedPreferences sp, String key, String def_themes){
        return sp.getString(key, def_themes);
    }
}
