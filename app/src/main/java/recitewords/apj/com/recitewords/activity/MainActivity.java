package recitewords.apj.com.recitewords.activity;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import recitewords.apj.com.recitewords.R;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    //主页面背景图片对应单词数组
    private String[] img_words = new String[]{"antler" ,"ferry" ,"lotus pond" ,"mist" ,"moon" ,"reading time" ,"scarecrow" ,"twinkling" ,};
    //定义好的8张背景图id数组
    private int[] imgs = new int[]{R.drawable.login_background_define01, R.drawable.login_background_define02, R.drawable.login_background_define03,
            R.drawable.login_background_define04, R.drawable.login_background_define05, R.drawable.login_background_define06,
            R.drawable.login_background_define07, R.drawable.login_background_define08, };
    private ViewHolder holder;



    private class ViewHolder{
        RelativeLayout activity_main;
        TextView tv_word;
        LinearLayout linearLayout;
        ImageView img_sign;
        TextView tv_date;
        ImageView iv_menu;
    }
    private void init_view(){
        holder = new ViewHolder();
        holder.activity_main = findViewByIds(R.id.activity_main);
        holder.tv_word = findViewByIds(R.id.main_tv_word);
        holder.linearLayout = findViewByIds(R.id.main_ll);
        holder.img_sign = findViewByIds(R.id.main_img_sign);
        holder.tv_date = findViewByIds(R.id.main_tv_date);
        holder.iv_menu = findViewByIds(R.id.main_img_menu);
    }
    private void init_event(){
        holder.iv_menu.setOnClickListener(this);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //初始化控件
        init_view();
        //初始化事件
        init_event();
        //生成一个随机数
        int num = randomNum();
        //为主界面设置随机图片和图片对应单词
        holder.activity_main.setBackgroundResource(imgs[num]);
        holder.tv_word.setText(img_words[num]);

        //将学习复习背景、签到背景设置为半透明
        holder.linearLayout.getBackground().setAlpha(150);
        holder.img_sign.setAlpha(150);

        //设置签到里的日期和星期
        holder.tv_date.setText(setDate());

    }

        //生成0 - 7的随机数
    private int randomNum(){
        int num = (int) (Math.random()* 8);
        return num;
    }

    //设置签到里的日期和星期
    private String setDate(){
        SimpleDateFormat format = new SimpleDateFormat("MM/dd");
        String str = format.format(new Date());

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

        return str+" "+wDay;
    }
    //菜单的点击事件
    @Override
    public void onClick(View v) {

    }
}
