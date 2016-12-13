package recitewords.apj.com.recitewords.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import recitewords.apj.com.recitewords.activity.MainActivity;
import recitewords.apj.com.recitewords.db.ReciteWordsSQLiteOpenHelper;

/**
 * Created by Greetty on 2016/12/13.
 *
 * 词书表内容提供者
 */
public class BookProvider extends ContentProvider {

    ReciteWordsSQLiteOpenHelper helper;
    SQLiteDatabase db;

    @Override
    public boolean onCreate() {
        helper = new ReciteWordsSQLiteOpenHelper(getContext(), MainActivity.dbName, 1);
        db = helper.getWritableDatabase();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = db.query("book", projection, selection, selectionArgs, null, null, sortOrder, null);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        db.insert("book", null, values);
        ContentResolver cr = getContext().getContentResolver();
        //发出通知，所有注册在这个uri上的内容观察者都可以收到通知
        cr.notifyChange(uri, null);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
