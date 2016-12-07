package recitewords.apj.com.recitewords.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.db.ReciteWordsSQLiteOpenHelper;

/**
 * 词库表的Dao方法
 * <p/>
 * Created by 陈金振 on 2016/11/28 0028.
 */
public class LexiconDao {
    public ReciteWordsSQLiteOpenHelper helper;

    String add_sql = "insert into lexicon(book_name,book_desc,word_num,word_num_study,word_num_grasp,word_num_review,userID) values " +
            "(?,?,?,?,?,?,?)";

    public LexiconDao(Context context) {
        helper = new ReciteWordsSQLiteOpenHelper(context, MainActivity.dbName, 1);
    }


    /*
    * 增加词库表方法
    * */
    public void addWord(String book_name, String book_desc, int word_num, int word_num_study,
                        int word_num_grasp, int word_num_review, int userID) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(add_sql, new Object[]{book_name, book_desc, word_num, word_num_study, word_num_grasp, word_num_review, userID});
        db.close();
    }

    /**
     * 获取已掌握单词总数
     * @param book_name  词书名字
     * @return  掌握单词数
     */
    public int getGraspWord(String book_name) {
        int mGraspWord = 0;  //已掌握单词
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("lexicon", new String[]{"word_num_grasp"}, "book_name =?",
                new String[]{book_name}, null, null, null);
        while (cursor.moveToNext()) {
            mGraspWord = cursor.getInt(0);
        }
        return mGraspWord;
    }


    /**
     * 获取需复习的单词总数
     * @param book_name  词书名字
     * @return  需复习单词数
     */
    public int getReviewWord(String book_name) {
        int word_num_review  = 0;  //已掌握单词
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("lexicon", new String[]{"word_num_review"}, "book_name =?",
                new String[]{book_name}, null, null, null);
        while (cursor.moveToNext()) {
            word_num_review = cursor.getInt(0);
        }
        return word_num_review;
    }

    /**
     * 更新已掌握的单词个数
     *
     * @param number 已掌握个数
     * @return 受影响行数
     */
    public int updateGraspWord(int number, String book_name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word_num_grasp",number);
        return db.update("lexicon", values, "book_name =?", new String[]{book_name});
    }

    /**
     * 更新需复习的单词个数
     *
     * @param number 需复习单词个数
     * @return 受影响行数
     */
    public int updateReviewWord(int number, String book_name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word_num_review",number);
        return db.update("lexicon", values, "book_name =?", new String[]{book_name});
    }


}