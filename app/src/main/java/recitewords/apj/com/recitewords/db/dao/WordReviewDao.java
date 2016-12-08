package recitewords.apj.com.recitewords.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.bean.WordExampleSentence;
import recitewords.apj.com.recitewords.bean.WordReview;
import recitewords.apj.com.recitewords.db.ReciteWordsSQLiteOpenHelper;

/**
 * Created by Greetty on 2016/12/5.
 * <p>
 * 单词复习表工具类
 */
public class WordReviewDao {

    public ReciteWordsSQLiteOpenHelper helper;

    public WordReviewDao(Context context) {
        helper = new ReciteWordsSQLiteOpenHelper(context, MainActivity.dbName, 1);
    }


    /**
     * 增加单词
     *
     * @param userID             id
     * @param word               单词
     * @param option_A           A
     * @param option_B           B
     * @param option_C           C
     * @param option_D           D
     * @param soundmark_american 美式发音音标
     * @param soundmark_british  英式发音音标
     * @param answer_right       正确答案
     * @param answer_user        用户答案
     * @param word_is_review     该单词是否已复习
     * @param date               日期
     * @param book_name          单词所属词书名字
     * @return 受影响行数
     */

    public long addWord(String word, String option_A, String option_B, String option_C, String option_D,
                        String soundmark_american, String soundmark_british, String answer_right,
                        String answer_user, int word_is_review, String grasp_values, String date, String book_name, int userID) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word", word);
        values.put("option_A", option_A);
        values.put("option_B", option_B);
        values.put("option_C", option_C);
        values.put("option_D", option_D);
        values.put("soundmark_american", soundmark_american);
        values.put("soundmark_british", soundmark_british);
        values.put("answer_right", answer_right);
        values.put("answer_user", answer_user);
        values.put("word_is_review", word_is_review);
        values.put("grasp_values", grasp_values);
        values.put("date", date);
        values.put("book_name", book_name);
        values.put("userID", userID);
        long num = db.insert("word_review", null, values);
        values.clear();
        db.close();
        return num;
    }

    /**
     * 查询本组复习的单词
     *
     * @return 所有单词
     */
    public List<WordReview> queryAll(String book_name) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<WordReview> wordReviews = new ArrayList<>();
        Cursor cursor = db.query("word_review", null, "book_name=?", new String[]{book_name}, null, null, null);
        while (cursor.moveToNext()) {
            WordReview wordReview = new WordReview();
            wordReview.setWord(cursor.getString(cursor.getColumnIndex("word")));
            wordReview.setOption_A(cursor.getString(cursor.getColumnIndex("option_A")));
            wordReview.setOption_B(cursor.getString(cursor.getColumnIndex("option_B")));
            wordReview.setOption_C(cursor.getString(cursor.getColumnIndex("option_C")));
            wordReview.setOption_D(cursor.getString(cursor.getColumnIndex("option_D")));
            wordReview.setSoundmark_american(cursor.getString(cursor.getColumnIndex("soundmark_american")));
            wordReview.setSoundmark_british(cursor.getString(cursor.getColumnIndex("soundmark_british")));
            wordReview.setAnswer_right(cursor.getString(cursor.getColumnIndex("answer_right")));
            wordReview.setAnswer_user(cursor.getString(cursor.getColumnIndex("answer_user")));
            wordReview.setWord_is_review(cursor.getInt(cursor.getColumnIndex("word_is_review")));
            wordReview.setDate(cursor.getString(cursor.getColumnIndex("date")));
            wordReview.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
            wordReview.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
            wordReviews.add(wordReview);
        }
        cursor.close();
        db.close();
        return wordReviews;
    }


    /**
     * 更新单词的学习日期
     *
     * @param date      更新日期
     * @param word      要更新日期的单词
     * @param book_name 要更新日期的单词所在的词书
     * @return 受影响行数
     */
    public int updateWordDate(String date, String word, String book_name) {
        ContentValues mValues = new ContentValues();
        mValues.put("date", date);
        SQLiteDatabase db = helper.getWritableDatabase();
        int num = db.update("word_review", mValues, "word=? and book_name=?", new String[]{word, book_name});
        mValues.clear();
        db.close();
        return num;
    }

    /**
     * 更新单词的复习状态
     *
     * @param word      单词
     * @param book_name 单词所在词书名字
     * @return 受影响行数
     */
    public int updateReviewState(String word, String book_name) {
        ContentValues mValues = new ContentValues();
        mValues.put("word_is_review", 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        return db.update("word_review", mValues, "word=? and book_name=?", new String[]{word, book_name});
    }

//    /**
//     * 获取已经完成复习的单词个数
//     *
//     * @return  完成复习单词数
//     */
//    public int getCompleteReviewWord(){
//
//    }


}
