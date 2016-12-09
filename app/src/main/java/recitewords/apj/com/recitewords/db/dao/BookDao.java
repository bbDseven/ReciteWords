package recitewords.apj.com.recitewords.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.bean.Book;
import recitewords.apj.com.recitewords.db.ReciteWordsSQLiteOpenHelper;
import recitewords.apj.com.recitewords.globle.Grasp;
import recitewords.apj.com.recitewords.util.DateUtil;

/**
 * 词书表book 的实体类
 * Created by cjz on 2016/11/28 0028.
 */
public class BookDao {
    public ReciteWordsSQLiteOpenHelper helper;

    String add_sql = "insert into book(word,soundmark_american,soundmark_british," +
            "word_mean,word_is_study,word_is_grasp,grasp_values,date,book_name,userID) values " +
            "(?,?,?,?,?,?,?,?,?,?)";

    String add = "insert into word_study(word,soundmark_american,soundmark_british,answer_right,asterisk) values " +
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
                        String word_mean, int word_is_study, int word_is_grasp, String grsspValues,
                        String date, String book_name, int userID) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(add_sql, new Object[]{word, soundmark_american, soundmark_british,
                word_mean, word_is_study, word_is_grasp, grsspValues, date, book_name, userID});
        db.close();
    }

    /**
     * 查询需要复习的单词，返回20条数据
     *
     * @return 单词信息
     */
    public List<Book> queryReviewWOrd() {
        int day = 0;   //相差日期
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<Book> books = new ArrayList<>();
        Cursor cursor = db.query("book", null, "word_is_study=? and word_is_grasp=?",
                new String[]{"1", "0"}, null, null, null, null);
        while (cursor.moveToNext()) {
            Book book = new Book();
            //熟悉程度
            String graspValues = cursor.getString(cursor.getColumnIndex("grasp_values"));
            //
            String date = cursor.getString(cursor.getColumnIndex("date"));
            day = DateUtil.compareToday(date);
            if (books.size() < Grasp.RRVIEW_NUMBER) {
                if (graspValues.equals(Grasp.GRASP_A)) {
                    if (day >= 100) {
                        book.setWord(cursor.getString(cursor.getColumnIndex("word")));
                        book.setSoundmark_american(cursor.getString(cursor.getColumnIndex("soundmark_american")));
                        book.setSoundmark_british(cursor.getString(cursor.getColumnIndex("soundmark_british")));
                        book.setWord_mean(cursor.getString(cursor.getColumnIndex("word_mean")));
                        book.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
                        book.setGrasp_values(cursor.getString(cursor.getColumnIndex("grasp_values")));
                        book.setDate(cursor.getString(cursor.getColumnIndex("date")));
                        book.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
                        books.add(book);
                    }
                } else if (graspValues.equals(Grasp.GRASP_B)) {
                    if (day >= 50) {
                        book.setWord(cursor.getString(cursor.getColumnIndex("word")));
                        book.setSoundmark_american(cursor.getString(cursor.getColumnIndex("soundmark_american")));
                        book.setSoundmark_british(cursor.getString(cursor.getColumnIndex("soundmark_british")));
                        book.setWord_mean(cursor.getString(cursor.getColumnIndex("word_mean")));
                        book.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
                        book.setGrasp_values(cursor.getString(cursor.getColumnIndex("grasp_values")));
                        book.setDate(cursor.getString(cursor.getColumnIndex("date")));
                        book.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
                        books.add(book);
                    }
                } else if (graspValues.equals(Grasp.GRASP_C)) {
                    if (day >= 20) {
                        book.setWord(cursor.getString(cursor.getColumnIndex("word")));
                        book.setSoundmark_american(cursor.getString(cursor.getColumnIndex("soundmark_american")));
                        book.setSoundmark_british(cursor.getString(cursor.getColumnIndex("soundmark_british")));
                        book.setWord_mean(cursor.getString(cursor.getColumnIndex("word_mean")));
                        book.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
                        book.setGrasp_values(cursor.getString(cursor.getColumnIndex("grasp_values")));
                        book.setDate(cursor.getString(cursor.getColumnIndex("date")));
                        book.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
                        books.add(book);
                    }
                } else if (graspValues.equals(Grasp.GRASP_D)) {
                    if (day >= 10) {
                        book.setWord(cursor.getString(cursor.getColumnIndex("word")));
                        book.setSoundmark_american(cursor.getString(cursor.getColumnIndex("soundmark_american")));
                        book.setSoundmark_british(cursor.getString(cursor.getColumnIndex("soundmark_british")));
                        book.setWord_mean(cursor.getString(cursor.getColumnIndex("word_mean")));
                        book.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
                        book.setGrasp_values(cursor.getString(cursor.getColumnIndex("grasp_values")));
                        book.setDate(cursor.getString(cursor.getColumnIndex("date")));
                        book.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
                        books.add(book);
                    }
                } else if (graspValues.equals(Grasp.GRASP_E)) {
                    if (day >= 5) {
                        book.setWord(cursor.getString(cursor.getColumnIndex("word")));
                        book.setSoundmark_american(cursor.getString(cursor.getColumnIndex("soundmark_american")));
                        book.setSoundmark_british(cursor.getString(cursor.getColumnIndex("soundmark_british")));
                        book.setWord_mean(cursor.getString(cursor.getColumnIndex("word_mean")));
                        book.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
                        book.setGrasp_values(cursor.getString(cursor.getColumnIndex("grasp_values")));
                        book.setDate(cursor.getString(cursor.getColumnIndex("date")));
                        book.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
                        books.add(book);
                    }
                } else if (graspValues.equals(Grasp.GRASP_F)) {
                    if (day >= 0) {
                        book.setWord(cursor.getString(cursor.getColumnIndex("word")));
                        book.setSoundmark_american(cursor.getString(cursor.getColumnIndex("soundmark_american")));
                        book.setSoundmark_british(cursor.getString(cursor.getColumnIndex("soundmark_british")));
                        book.setWord_mean(cursor.getString(cursor.getColumnIndex("word_mean")));
                        book.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
                        book.setGrasp_values(cursor.getString(cursor.getColumnIndex("grasp_values")));
                        book.setDate(cursor.getString(cursor.getColumnIndex("date")));
                        book.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
                        books.add(book);
                    }
                }
            }
        }
        return books;
    }

    /**
     * 获取需要复习的单词总数
     *
     * @return 单词总数 int
     */
    public int queryAllReviewWordSize() {
        int day = 0;   //相差日期
        SQLiteDatabase db = helper.getWritableDatabase();
        ArrayList<Book> books = new ArrayList<>();
        Cursor cursor = db.query("book", null, "word_is_study=? and word_is_grasp=?",
                new String[]{"1", "0"}, null, null, null, null);
        while (cursor.moveToNext()) {
            Book book = new Book();
            //熟悉程度
            String graspValues = cursor.getString(cursor.getColumnIndex("grasp_values"));
            //
            String date = cursor.getString(cursor.getColumnIndex("date"));
           // Log.e("BookDao","日期差： "+day);
            day = DateUtil.compareToday(date);
            if (graspValues.equals(Grasp.GRASP_A)) {
                if (day >= 100) {
                    book.setWord(cursor.getString(cursor.getColumnIndex("word")));
                    book.setSoundmark_american(cursor.getString(cursor.getColumnIndex("soundmark_american")));
                    book.setSoundmark_british(cursor.getString(cursor.getColumnIndex("soundmark_british")));
                    book.setWord_mean(cursor.getString(cursor.getColumnIndex("word_mean")));
                    book.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
                    book.setGrasp_values(cursor.getString(cursor.getColumnIndex("grasp_values")));
                    book.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    book.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
                    books.add(book);
                }
            } else if (graspValues.equals(Grasp.GRASP_B)) {
                if (day >= 50) {
                    book.setWord(cursor.getString(cursor.getColumnIndex("word")));
                    book.setSoundmark_american(cursor.getString(cursor.getColumnIndex("soundmark_american")));
                    book.setSoundmark_british(cursor.getString(cursor.getColumnIndex("soundmark_british")));
                    book.setWord_mean(cursor.getString(cursor.getColumnIndex("word_mean")));
                    book.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
                    book.setGrasp_values(cursor.getString(cursor.getColumnIndex("grasp_values")));
                    book.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    book.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
                    books.add(book);
                }
            } else if (graspValues.equals(Grasp.GRASP_C)) {
                if (day >= 20) {
                    book.setWord(cursor.getString(cursor.getColumnIndex("word")));
                    book.setSoundmark_american(cursor.getString(cursor.getColumnIndex("soundmark_american")));
                    book.setSoundmark_british(cursor.getString(cursor.getColumnIndex("soundmark_british")));
                    book.setWord_mean(cursor.getString(cursor.getColumnIndex("word_mean")));
                    book.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
                    book.setGrasp_values(cursor.getString(cursor.getColumnIndex("grasp_values")));
                    book.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    book.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
                    books.add(book);
                }
            } else if (graspValues.equals(Grasp.GRASP_D)) {
                if (day >= 10) {
                    book.setWord(cursor.getString(cursor.getColumnIndex("word")));
                    book.setSoundmark_american(cursor.getString(cursor.getColumnIndex("soundmark_american")));
                    book.setSoundmark_british(cursor.getString(cursor.getColumnIndex("soundmark_british")));
                    book.setWord_mean(cursor.getString(cursor.getColumnIndex("word_mean")));
                    book.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
                    book.setGrasp_values(cursor.getString(cursor.getColumnIndex("grasp_values")));
                    book.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    book.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
                    books.add(book);
                }
            } else if (graspValues.equals(Grasp.GRASP_E)) {
                if (day >= 5) {
                    book.setWord(cursor.getString(cursor.getColumnIndex("word")));
                    book.setSoundmark_american(cursor.getString(cursor.getColumnIndex("soundmark_american")));
                    book.setSoundmark_british(cursor.getString(cursor.getColumnIndex("soundmark_british")));
                    book.setWord_mean(cursor.getString(cursor.getColumnIndex("word_mean")));
                    book.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
                    book.setGrasp_values(cursor.getString(cursor.getColumnIndex("grasp_values")));
                    book.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    book.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
                    books.add(book);
                }
            } else if (graspValues.equals(Grasp.GRASP_F)) {
                if (day >= 0) {
                    book.setWord(cursor.getString(cursor.getColumnIndex("word")));
                    book.setSoundmark_american(cursor.getString(cursor.getColumnIndex("soundmark_american")));
                    book.setSoundmark_british(cursor.getString(cursor.getColumnIndex("soundmark_british")));
                    book.setWord_mean(cursor.getString(cursor.getColumnIndex("word_mean")));
                    book.setBook_name(cursor.getString(cursor.getColumnIndex("book_name")));
                    book.setGrasp_values(cursor.getString(cursor.getColumnIndex("grasp_values")));
                    book.setDate(cursor.getString(cursor.getColumnIndex("date")));
                    book.setUserID(cursor.getInt(cursor.getColumnIndex("userID")));
                    books.add(book);
                }
            }
        }
        return books.size();
    }


    /**
     * 查询单词的熟悉程度更新日期
     * @param book_name  词书名字
     * @param word  单词
     * @return  日期 String
     */
    public String queryWordDate(String book_name, String word){
        String date = "";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("book", null, "book_name=? and word=?",
                new String[]{book_name, word}, null, null, null);
        while (cursor.moveToNext()){
            date = cursor.getString(cursor.getColumnIndex("date"));
        }
        return date;
    }

    /**
     * 查询单词的熟悉程度
     * @param book_name  词书名字
     * @param word  单词
     * @return  熟悉程度 String
     */
    public String queryWordGrasp(String book_name, String word){
        String grasp = "";
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query("book", null, "book_name=? and word=?",
                new String[]{book_name, word}, null, null, null);
        while (cursor.moveToNext()){
            grasp = cursor.getString(cursor.getColumnIndex("grasp_values"));
        }
        return grasp;
    }

    /**
     * 更新单词是否为已掌握
     *
     * @param book_name 词书名字
     * @param word      更新的单词
     * @return 受影响行数
     */
    public int updateGraspWord(String book_name, String word) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("word_is_grasp ", 1);
        return db.update("book", values, "book_name=? and word=?", new String[]{book_name, word});
    }


    /**
     * 更新掌握程度
     *
     * @param book_name   词书名字
     * @param word        更新的单词
     * @param graspValues 掌握程度
     * @return 受影响行数
     */
    public int updateGraspValues(String book_name, String word, String graspValues) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("grasp_values  ", graspValues);
        return db.update("book", values, "book_name=? and word=?", new String[]{book_name, word});
    }

    /**
     * 更新日期
     *
     * @param book_name 词书名字
     * @param word      单词
     * @param date      日期
     * @return 受影响行数
     */
    public int updateDate(String book_name, String word, String date) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("date ", date);
        return db.update("book", values, "book_name=? and word=?", new String[]{book_name, word});
    }

    /**
     * 更改单词的：是否已学习 word_is_study 字段 变为 1
     * 表示已学习
     */
    public void updateWord_is_study(String word) {
        String update_sql = "update book set word_is_study=? where word=?";
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(update_sql, new Object[]{1, word});
        db.close();
    }

    /**
     * 从Book 词书表里获取20个单词
     * 供给 word_study 学习表去学习
     */
    public ArrayList<Book> getWord_study() {
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<Book> wordList = new ArrayList<Book>();//存放20个单词信息的集合
        Cursor cursor = db.rawQuery("select word, soundmark_american, soundmark_british, word_mean from book", null);
        // 先获取
        while (cursor.moveToNext()) {
            String word = cursor.getString(cursor.getColumnIndex("word"));
            String soundmark_american = cursor.getString(cursor.getColumnIndex("soundmark_american"));
            String soundmark_british = cursor.getString(cursor.getColumnIndex("soundmark_british"));
            String word_mean = cursor.getString(cursor.getColumnIndex("word_mean"));
            Book book = new Book(word, soundmark_american, soundmark_british, word_mean);
            wordList.add(book);
        }
        cursor.close();
        db.close();
        return wordList;
    }

    /**
     * 从 getWord_study 方法里获取到的20个单词集合
     * 插入到 word_study 学习表里
     */
    public void insertWord_study() {
        ArrayList<Book> books = getWord_study();
        for (int i = 0; i < books.size(); i++) {
            add(books.get(i).getWord(),
                    books.get(i).getSoundmark_american(),
                    books.get(i).getSoundmark_british(),
                    books.get(i).getWord_mean(), 0);
        }
    }

    /**
     * 插入，单词，美式音标，英式音标，词义，星号（认识次数）
     */
    public void add(String word, String soundmark_american, String soundmark_british, String answer_right, int asterisk) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL(add, new Object[]{word, soundmark_american, soundmark_british, answer_right, asterisk});
        db.close();
    }

    /**
     * 根据单词查询单词信息   返回音标和词义
    * */
    public Book query_wordInfo(String word){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor wordInfo = db.rawQuery("select * from book where word=?", new String[]{word});
        Book book = new Book();
        if (wordInfo != null && wordInfo.getCount() > 0){
            if (wordInfo.moveToFirst()){
                book.setSoundmark_american(wordInfo.getString(wordInfo.getColumnIndex("soundmark_american")));
                book.setWord_mean(wordInfo.getString(wordInfo.getColumnIndex("word_mean")));
                return book;
            }
        }
        return null;
    }
}
