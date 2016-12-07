package recitewords.apj.com.recitewords.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.bean.Book;
import recitewords.apj.com.recitewords.db.ReciteWordsSQLiteOpenHelper;

/**
 * 词书表book 的实体类
 * Created by cjz on 2016/11/28 0028.
 */
public class BookDao {
    public ReciteWordsSQLiteOpenHelper helper;

    String add_sql = "insert into book(word,soundmark_american,soundmark_british," +
            "word_mean,word_is_study,word_is_grasp,book_name,userID) values " +
            "(?,?,?,?,?,?,?,?)";

    String add ="insert into word_study(word,soundmark_american,soundmark_british,answer_right,asterisk) values " +
            "(?,?,?,?,?)";

    public BookDao(Context context) {
        helper = new ReciteWordsSQLiteOpenHelper(context, MainActivity.dbName, 1);
    }


    /**
     * 增加单词的方法
     * 包括增加英语单词，美式音标，美式发音，英式音标，英式发音
     * 词性，词义，是否已学习，是否已掌握
     */
    public void addWord(String word, String soundmark_american, String soundmark_british,
                        String word_mean, int word_is_study, int word_is_grasp, String book_name, int userID) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(add_sql, new Object[]{word, soundmark_american, soundmark_british,
                word_mean, word_is_study, word_is_grasp, book_name, userID});
        db.close();
    }

    /**
     * 查询需要复习的单词，返回20条数据
     *
     * @return 单词信息
     */
    public List<Book> queryReviewWOrd() {
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<Book> books = new ArrayList<>();
        Cursor cursor = db.query("book", null, "word_is_study=? and word_is_grasp=?",
                new String[]{"1", "0"}, null, null, null, "20");
        while (cursor.moveToNext()) {
            Book book = new Book();
            book.setWord(cursor.getString(cursor.getColumnIndex("word")));
            book.setSoundmark_american(cursor.getString(cursor.getColumnIndex("soundmark_american")));
            book.setSoundmark_british(cursor.getString(cursor.getColumnIndex("soundmark_british")));
            book.setWord_mean(cursor.getString(cursor.getColumnIndex("word_mean")));
            book.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
            book.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
            books.add(book);
        }
        return books;
    }


    /**
     * 更新单词是否为已掌握
     * @param book_name  词书名字
     * @param word  更新的单词
     * @return  受影响行数
     */
    public int updateGraspWord(String book_name ,String word){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word_is_grasp ",1);
       return db.update("book", values, "book_name=? and word=?", new String[]{book_name, word});
    }

    /**
     *  更改单词的：是否已学习 word_is_study 字段 变为 1
     *  表示已学习
     * */
    public void updateWord_is_study(String word){
        String update_sql ="update book set word_is_study=? where word=?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(update_sql,new Object[]{1,word});
        db.close();
    }

    /**
     * 从Book 词书表里获取20个单词
     * 供给 word_study 学习表去学习
     * */
    public ArrayList<Book> getWord_study(){
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Book> wordList = new ArrayList<Book>();//存放20个单词信息的集合
        Cursor cursor = db.rawQuery("select word, soundmark_american, soundmark_british, word_mean from book",null);
        // 先获取
        while (cursor.moveToNext()){
            String word = cursor.getString(cursor.getColumnIndex("word"));
            String soundmark_american = cursor.getString(cursor.getColumnIndex("soundmark_american"));
            String soundmark_british = cursor.getString(cursor.getColumnIndex("soundmark_british"));
            String word_mean = cursor.getString(cursor.getColumnIndex("word_mean"));
            Book book = new Book(word,soundmark_american,soundmark_british,word_mean);
            wordList.add(book);
        }
        cursor.close();
        db.close();
        return wordList;
    }

    /**
     * 从 getWord_study 方法里获取到的20个单词集合
     * 插入到 word_study 学习表里
     * */
    public void insertWord_study(){
        ArrayList<Book> books = getWord_study();
        for (int i=0;i<books.size();i++){
            add(books.get(i).getWord(),
                    books.get(i).getSoundmark_american(),
                    books.get(i).getSoundmark_british(),
                    books.get(i).getWord_mean(),0);
        }
    }

    /**
     * 插入，单词，美式音标，英式音标，词义，星号（认识次数）
     * */
    public void add(String word,String soundmark_american,String soundmark_british,String answer_right,int asterisk) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(add, new Object[]{word,soundmark_american,soundmark_british,answer_right,asterisk});
        db.close();
    }

}
