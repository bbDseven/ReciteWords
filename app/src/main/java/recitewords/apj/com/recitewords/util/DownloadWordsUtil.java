package recitewords.apj.com.recitewords.util;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

/**
 * Created by CJZ on 2016/12/16 0016.
 * 下载单词库的工具类
 */
public class DownloadWordsUtil {

    public static void DownloadBook(final String BookName,final String url,final HashMap<String, Object> metaData){
                final String packageName = "recitewords.apj.com.recitewords";
                final AVFile file = new AVFile(BookName,url,metaData);
                file.getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, AVException e) {
                        try {
                            File wordFile = new File("/data/data/" + packageName + "/databases/" + "ReciteWords_0.db");
                            FileOutputStream fos = new FileOutputStream(wordFile);
                            fos.write(bytes);
                            fos.close();
                            Log.e("123","下载完成。。。");
                        }catch (Exception e1){

                        }
                    }
                }, new ProgressCallback() {
                    @Override
                    public void done(Integer integer) {
                    }
                });
    }

}
