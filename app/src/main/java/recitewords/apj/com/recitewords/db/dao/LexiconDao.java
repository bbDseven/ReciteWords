package recitewords.apj.com.recitewords.db.dao;

import android.content.Context;

import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.db.ReciteWordsSQLiteOpenHelper;

/**
 * 词库表的Dao方法
 * Created by 陈金振 on 2016/11/28 0028.
 */
public class LexiconDao {
    public ReciteWordsSQLiteOpenHelper helper;

    public LexiconDao(Context context) {
        helper = new ReciteWordsSQLiteOpenHelper(context, MainActivity.dbName, 1);
    }

}