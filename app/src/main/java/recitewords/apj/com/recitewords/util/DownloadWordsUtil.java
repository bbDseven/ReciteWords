package recitewords.apj.com.recitewords.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
 * 判断是否有网络的方法
 */
public class DownloadWordsUtil {

    /**
     *下载单词库的方法
     * */
    public static void DownloadBook(final String BookName,final String url,final HashMap<String, Object> metaData,Context context){
        final String packageName = "recitewords.apj.com.recitewords";
        final AVFile file = new AVFile(BookName,url,metaData);
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("文件大小1MB");
        progressDialog.setMax(100);
        progressDialog.show();
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
                progressDialog.setProgress(integer);
            }
        });
        progressDialog.dismiss();
    }

    /**n
     * 判断网络状态的方法
     * */
    public static boolean isNetworkAvailable (Activity activity){
        Context context = activity.getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null){
            return false;
        }else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0){
                for (int i = 0; i < networkInfo.length; i++){
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     *  删除文件方法
     * */
    public static void deleteFile(File file){
        if (file.isDirectory()){
            File[] files = file.listFiles();
            for (File f : files){
                deleteFile(f);
                f.delete();
            }
        }else {
            file.delete();
        }
    }

    /**
     *  判断文件是否存在的方法
     * */
    public static boolean judgeFileExists(File file){
        if (file.exists()){
            return true;
        }else {
            return false;
        }
    }

}
