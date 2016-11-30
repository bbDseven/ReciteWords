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

    String add_sql ="insert into book(word,soundmark_american,soundmark_british," +
            "word_mean,word_is_study,word_is_grasp,book_name,userID) values " +
            "(?,?,?,?,?,?,?,?)";

    public BookDao(Context context){
        helper = new ReciteWordsSQLiteOpenHelper(context, MainActivity.dbName, 1);
    }

    /*
    * 增加单词的方法
    *  包括增加英语单词，美式音标，美式发音，英式音标，英式发音
    *  词性，词义，是否已学习，是否已掌握
    * */
    public void addWord(String word,String soundmark_american,String soundmark_british,
                        String word_mean,int word_is_study,int word_is_grasp,String book_name,int userID) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(add_sql, new Object[]{word,soundmark_american,soundmark_british,
                word_mean,word_is_study,word_is_grasp,book_name,userID});
        db.close();
    }
}
