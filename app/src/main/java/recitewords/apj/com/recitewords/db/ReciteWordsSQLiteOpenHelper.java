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

    private final String WORD_STUDY_SQL="create table word_study(word text," +
            "option_A text,option_B text,option_C text,option_D text,answer_right text," +
            "answer_user text,asterisk integer,date text,book_name text，userID integer)";

    public ReciteWordsSQLiteOpenHelper(Context context,String name,int version) {
        super(context,name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //单词学习表word_study

        db.execSQL(WORD_STUDY_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
