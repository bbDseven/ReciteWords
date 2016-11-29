package recitewords.apj.com.recitewords.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CGT on 2016/11/28.
 *
 * 不背单词书库
 *
 * 数据库库打开帮助类
 */
public class ReciteWordsSQLiteOpenHelper extends SQLiteOpenHelper{
    private final String WORD_EXAMPLE_SENTENCE = "create table word_example_sentence(word text, example_sentence text, example_sentence_mean," +
            "example_sentence_pronounce text, example_sentence_resource text)";
    public ReciteWordsSQLiteOpenHelper(Context context, String name, int version) {
        //创建数据库时，写死数据库名字为ReciteWords.db，版本为1
        super(context, name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WORD_EXAMPLE_SENTENCE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
