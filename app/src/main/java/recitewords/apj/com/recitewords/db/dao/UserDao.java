package recitewords.apj.com.recitewords.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.bean.User;
import recitewords.apj.com.recitewords.db.ReciteWordsSQLiteOpenHelper;

/**
 * Created by Seven on 2016/12/8.
 */

public class UserDao {
    private ReciteWordsSQLiteOpenHelper helper;
    public UserDao(Context context){
        helper = new ReciteWordsSQLiteOpenHelper(context, MainActivity.dbName, 1);
    }
    //添加信息
    public void add(String sign_in, Integer sign_in_continue, Integer cool_money){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("sign_in", sign_in);
        values.put("sign_in_continue", sign_in_continue);
        values.put("cool_money", cool_money);
        db.insert("user", null, values);
        values.clear();
        db.close();
    }
    //更新信息
    public void update(String sign_in, Integer sign_in_continue, Integer cool_money){
        SQLiteDatabase db = helper.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put("sign_in", sign_in);
        values.put("sign_in_continue", sign_in_continue);
        values.put("cool_money", cool_money);
        db.update("user", values, null, null);
        values.clear();
        db.close();
    }

    //查询信息
    public User query(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from user", null);
        User user = new User();
        if (cursor != null && cursor.getCount() > 0){
            if (cursor.moveToFirst()){
                user.setSign_in(cursor.getString(cursor.getColumnIndex("sign_in")));
                user.setSign_in_continue(cursor.getInt(cursor.getColumnIndex("sign_in_continue")));
                user.setCool_money(cursor.getInt(cursor.getColumnIndex("cool_money")));
            }
        }
        return user;
    }
}
