package recitewords.apj.com.recitewords.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.bean.WordStudy;
import recitewords.apj.com.recitewords.db.ReciteWordsSQLiteOpenHelper;

/**
 * Created by CGT on 2016/11/28.
 *
 * 单词学习表工具类
 */
public class WordStudyDao {

    public ReciteWordsSQLiteOpenHelper helper;

    public WordStudyDao(Context context) {
        helper = new ReciteWordsSQLiteOpenHelper(context, MainActivity.dbName, 1);
    }


    /**
     * 增加单词
     *
     * @param values 要插入的数据
     * @return 受影响行数
     */
    public long addWord(ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues mValues = new ContentValues();
        mValues.put("userID", values.getAsInteger("userID"));
        mValues.put("word", values.getAsString("word"));
        mValues.put("option_A", values.getAsString("option_A"));
        mValues.put("option_B", values.getAsString("option_B"));
        mValues.put("option_C", values.getAsString("option_C"));
        mValues.put("option_D", values.getAsString("option_D"));
        mValues.put("answer_right", values.getAsString("answer_right"));
        mValues.put("answer_user", values.getAsString("answer_user"));
        mValues.put("asterisk", values.getAsInteger("asterisk"));
        mValues.put("date", values.getAsString("date"));
        mValues.put("book_name", values.getAsString("book_name"));
        long num = db.insert("word_study", null, mValues);
        mValues.clear();
        db.close();
        return num;
    }

    /**
     * 更新单词的*（记住次数）号
     *
     * @param asterisk  记住次数
     * @param word      要更改记住次数的单词
     * @param book_name 要更改记住次数的单词所在的词书
     * @return 受影响行数
     */
    public long updateWordAsterisk(int asterisk, String word, String book_name) {
        ContentValues mValues = new ContentValues();
        mValues.put("asterisk", asterisk);
        SQLiteDatabase db = helper.getWritableDatabase();
        int num = db.update("word_study", mValues, "word=?,book_name=?", new String[]{word, book_name});
        mValues.clear();
        db.close();
        return num;
    }

    /**
     * 更新单词的学习日期
     *
     * @param date      更新日期
     * @param word      要更新日期的单词
     * @param book_name 要更新日期的单词所在的词书
     * @return 受影响行数
     */
    public long updateWordDate(String date, String word, String book_name) {
        ContentValues mValues = new ContentValues();
        mValues.put("date", date);
        SQLiteDatabase db = helper.getWritableDatabase();
        int num = db.update("word_study", mValues, "word=?,book_name=?", new String[]{word, book_name});
        mValues.clear();
        db.close();
        return num;
    }

    /**
     * 根据需要学习的单词查询该单词详情
     *
     * @param word  要查询的单词
     * @param book_name  单词所在的词书名字
     * @return  WordStudy，单词的详细信息
     */
    public WordStudy findWordDetailByWord(String word, String book_name) {
        WordStudy wordStudy=null;
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("word_study", null, "book_name=?",
                new String[]{book_name}, null, null, null);
        while (cursor.moveToNext()) {
            wordStudy = new WordStudy();
            wordStudy.setWord(cursor.getString(cursor.getColumnIndex("word")));
            wordStudy.setOption_A(cursor.getString(cursor.getColumnIndex("option_A")));
            wordStudy.setOption_B(cursor.getString(cursor.getColumnIndex("option_B")));
            wordStudy.setOption_C(cursor.getString(cursor.getColumnIndex("option_C")));
            wordStudy.setOption_D(cursor.getString(cursor.getColumnIndex("option_D")));
            wordStudy.setAnswer_right(cursor.getString(cursor.getColumnIndex("answer_right")));
            wordStudy.setAnswer_user(cursor.getString(cursor.getColumnIndex("answer_user")));
            wordStudy.setAsterisk(cursor.getInt(cursor.getColumnIndex("asterisk")));
            wordStudy.setDate(cursor.getString(cursor.getColumnIndex("date")));
            wordStudy.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
            wordStudy.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
        }
        cursor.close();
        db.close();
        return wordStudy;
    }
}
