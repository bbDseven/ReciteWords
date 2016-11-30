package recitewords.apj.com.recitewords.db.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.db.ReciteWordsSQLiteOpenHelper;

/**
 * 词库表的Dao方法
 * Created by 陈金振 on 2016/11/28 0028.
 */
public class LexiconDao {
    public ReciteWordsSQLiteOpenHelper helper;

    String add_sql ="insert into lexicon(book_name,book_desc,word_num,word_num_study,word_num_grasp,word_num_review,userID) values " +
            "(?,?,?,?,?,?,?)";

    public LexiconDao(Context context) {
        helper = new ReciteWordsSQLiteOpenHelper(context, MainActivity.dbName, 1);
    }

    /*
    * 增加词库表方法
    * */
    public void addWord(String book_name,String book_desc,int word_num, int word_num_study,
                        int word_num_grasp, int word_num_review,int userID) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(add_sql, new Object[]{book_name,book_desc,word_num,word_num_study,word_num_grasp,word_num_review,userID});
        db.close();
    }

}