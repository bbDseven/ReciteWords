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

    // 创建词库表的sql语句
    private final String LEXICON_SQL = "create table lexicon (book_name text, book_desc text, " +
            "word_num integer, word_num_study integer, word_num_grasp integer, word_num_review integer, userID integer)";

    // 创建词书表的sql语句
    private final String BOOK_SQL = "create table book (word text, soundmark_american text, " +
            "pronounce_american text, soundmark_british text, pronounce_british text, word_property text, word_mean text, " +
            "word_img text, word_is_study integer, word_is_grasp integer, book_name text, userID integer)";

    //创建单词学习表的sql语句
    private final String WORD_STUDY_SQL="create table word_study(word text," +
            "option_A text,option_B text,option_C text,option_D text,answer_right text," +
            "answer_user text,asterisk integer,date text,book_name text,userID integer)";
    //创建例句表的sql语句
    private final String WORD_EXAMPLE_SENTENCE = "create table word_example_sentence(word text, example_sentence text, example_sentence_mean," +
            "example_sentence_pronounce text, example_sentence_resource text)";

    //创建单词复习表的sql语句
    private final String WORD_REVIEW_SQL="create table word_review(word text," +
            "option_A text,option_B text,option_C text,option_D text,soundmark_american text," +
            "soundmark_british text,answer_right text,answer_user text,word_is_review  integer," +
            "date text,book_name text,userID integer)";

    public ReciteWordsSQLiteOpenHelper(Context context,String name,int version) {
        super(context,name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(WORD_STUDY_SQL);
        db.execSQL(WORD_EXAMPLE_SENTENCE);
        db.execSQL(LEXICON_SQL);
        db.execSQL(BOOK_SQL);
        db.execSQL(WORD_REVIEW_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
