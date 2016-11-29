package recitewords.apj.com.recitewords.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.db.ReciteWordsSQLiteOpenHelper;

/**
 * 词书表book 的实体类
 * Created by cjz on 2016/11/28 0028.
 */
public class BookDao {
    public ReciteWordsSQLiteOpenHelper helper;

    public BookDao(Context context) {
        helper = new ReciteWordsSQLiteOpenHelper(context, MainActivity.dbName, 1);
    }

    /**
    * 增加单词的方法
    *  包括增加英语单词，美式音标，美式发音，英式音标，英式发音
    *  词性，词义，是否已学习，是否已掌握
    * */
    public long addWord(ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues mValues = new ContentValues();
        mValues.put("word", values.getAsInteger("word"));
        mValues.put("soundmark_american", values.getAsString("soundmark_american"));
        mValues.put("pronounce_american", values.getAsString("pronounce_american"));
        mValues.put("soundmark_british", values.getAsString("soundmark_british"));
        mValues.put("pronounce_british", values.getAsString("pronounce_british"));
        mValues.put("word_property", values.getAsString("word_property"));
        mValues.put("word_mean", values.getAsString("word_mean"));
        mValues.put("word_img", values.getAsString("word_img"));
        mValues.put("word_is_study", values.getAsInteger("word_is_study"));
        mValues.put("word_is_grasp", values.getAsString("word_is_grasp"));
        mValues.put("book_name", values.getAsString("book_name"));
        long num = db.insert("book", null, mValues);
        return num;
    }


}
