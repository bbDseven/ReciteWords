package recitewords.apj.com.recitewords.util;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import recitewords.apj.com.recitewords.bean.Book;
import recitewords.apj.com.recitewords.bean.WordStudy;
import recitewords.apj.com.recitewords.db.dao.BookDao;
import recitewords.apj.com.recitewords.db.dao.WordStudyDao;

/**
 * 此类为 从数据库中 word_study 单词学习表 里获取 单词 单词含义，美式音标，英式音标 的工具类
 * 在 学习界面 和 复习界面 中可用
 * Created by 陈金振 on 2016/12/1 0001.
 */
public class LearnWordsUtil {

    /**
     * 用来存放 随机产生的 4个ABCD选项（单词的含义） 的集合
     * */
    public static ArrayList<String> getWordsOption(Context context, String word){
        ArrayList<String> options = new ArrayList<String>();
        int arr[] = new int[4];
        int sum0,sum1,sum2,sum3;
        int a,b,c,num = 0;
        ArrayList<WordStudy> wordList = new ArrayList<WordStudy>();
        WordStudyDao wordStudyDao = new WordStudyDao(context);
        wordList = wordStudyDao.getWordStudy(context);// 获取WordStudy 学习表的20个单词
        for(int i=0;i<wordList.size();i++){
            if(word == wordList.get(i).getWord()){
                num = i;
                break;
            }
        }
        while(true){
            sum0 = rand4();
            sum1 = rand4();
            sum2 = rand4();
            sum3 = rand4();
            if(sum0!=sum1 && sum0!=sum2 && sum0!=sum3 && sum1!=sum2 && sum1!=sum3 && sum2!=sum3){
                break;
            }
        }
        while(true){
            a = make();
            b = make();
            c = make();
            if(a!=b && a!=c && a!=num && b!=c && b!=num && c!=num){
                arr[sum0] = a;
                arr[sum1] = b;
                arr[sum2] = c;
                arr[sum3] = num;
                break;
            }
        }
        if (!options.isEmpty()){
            options.clear();
        }
        for(int i=0;i<4;i++){
            options.add(wordList.get(arr[i]).getAnswer_right());
        }
        return options;
    }

    /**
     * 从WordStudy表里 获取 20 个单词，单词的美式音标，英式音标，和单词含义
     * */
    public static ArrayList<WordStudy> getWords(Context context){
        ArrayList<WordStudy> wordStudies = new ArrayList<WordStudy>();
        WordStudyDao wordStudyDao = new WordStudyDao(context);
        wordStudies = wordStudyDao.getWordStudy(context);
        for (int i=0;i<wordStudies.size();i++){
            Log.i("查看从WordStudy表里获取的20个数据",""+wordStudies.get(i).getWord()+wordStudies.get(i).getSoundmark_american()+
            wordStudies.get(i).getSoundmark_british()+wordStudies.get(i).getAnswer_right());
        }
        return wordStudies;
    }

    // 随机产生0-19 的数字
    public static int make(){
        Random r = new Random();
        int num = r.nextInt(20);
        return num;
    }

    // 随机产生0-3 的数字
    public static int rand4(){
        Random r = new Random();
        int num = r.nextInt(4);
        return num;
    }

}
