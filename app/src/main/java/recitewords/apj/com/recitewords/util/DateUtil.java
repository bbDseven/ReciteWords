package recitewords.apj.com.recitewords.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by CGT on 2016/11/22.
 *
 * 日期工具类
 */
public class DateUtil {

    /**
     * 获取月日
     * @return  月日
     */
    public static String getMonthAndDay(){
        SimpleDateFormat format = new SimpleDateFormat("MM/dd");
        return format.format(new Date());
    }

    /**
     * 获取星期
     * @return  星期英文缩写
     */
    public static String getWeek(){
        Calendar calendar = Calendar.getInstance();
        String wDay = String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
        if ("1".equals(wDay)){
            wDay = "sun.";
        }else if ("2".equals(wDay)){
            wDay = "Mon.";
        }else if ("3".equals(wDay)){
            wDay = "Tue.";
        }else if ("4".equals(wDay)){
            wDay = "Wed.";
        }else if ("5".equals(wDay)){
            wDay = "Thu.";
        }else if ("6".equals(wDay)){
            wDay = "Fri.";
        }else if ("7".equals(wDay)){
            wDay = "Sat.";
        }
        return wDay;
    }


}
