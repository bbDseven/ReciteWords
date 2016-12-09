package recitewords.apj.com.recitewords.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.bean.WordExampleSentence;
import recitewords.apj.com.recitewords.db.ReciteWordsSQLiteOpenHelper;

/**
 * Created by Seven on 2016/11/28.
 * 例句表的方法类
 */

public class ExampleSentenceDao {
    private SQLiteOpenHelper helper;

    public ExampleSentenceDao(Context context){
        helper = new ReciteWordsSQLiteOpenHelper(context, MainActivity.dbName, 1);
    }

    //插入例句数据
    public void insert(String word, String example_sentence, String example_sentence_mean, String example_sentence_pronounce, String example_sentence_resource){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word", word);
        values.put("example_sentence", example_sentence);
        values.put("example_sentence_mean", example_sentence_mean);
        values.put("example_sentence_pronounce", example_sentence_pronounce);
        values.put("example_sentence_resource", example_sentence_resource);
        db.insert("word_example_sentence", null, values);
        values.clear();
        db.close();
    }
    //删除例句数据
    public void delete(String word){
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("word_example_sentence", "word = ?", new String[]{word});
        db.close();
    }
    //修改例句数据
    public void update(ContentValues value){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word", value.getAsString("word"));
        values.put("example_sentence", value.getAsString("example_sentence"));
        values.put("example_sentence_mean", value.getAsString("example_sentence_mean"));
        values.put("example_sentence_pronounce", value.getAsString("example_sentence_pronounce"));
        values.put("example_sentence_resource", value.getAsString("example_sentence_resource"));
        db.update("word_example_sentence", values, null, null);
        values.clear();
        db.close();
    }
    //查询例句数据
    public WordExampleSentence query(String word){
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from word_example_sentence where word = ?", new String[]{word});
        if (cursor != null && cursor.getCount() > 0){
            if (cursor.moveToFirst()){
                WordExampleSentence bean_example_sentence = new WordExampleSentence();
                bean_example_sentence.setWord(cursor.getString(cursor.getColumnIndex("word")));
                bean_example_sentence.setExample_sentence(cursor.getString(cursor.getColumnIndex("example_sentence")));
                bean_example_sentence.setExample_sentence_mean(cursor.getString(cursor.getColumnIndex("example_sentence_mean")));
                bean_example_sentence.setExample_sentence_pronounce(cursor.getString(cursor.getColumnIndex("example_sentence_pronounce")));
                bean_example_sentence.setExample_sentence_resource(cursor.getString(cursor.getColumnIndex("example_sentence_resource")));
                return bean_example_sentence;
            }
        }
        return null;
    }
}
