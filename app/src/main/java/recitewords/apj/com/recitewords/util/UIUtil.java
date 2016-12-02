package recitewords.apj.com.recitewords.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import recitewords.apj.com.recitewords.R;

/**
 * Created by CGT on 2016/12/1.
 * <p/>
 * UI工具类
 */
public class UIUtil {

    /**
     * 寻找ID
     *
     * @param view 布局
     * @param id   要寻找的id
     * @param <E>  对应所要寻找的id的view
     * @return id
     */
    public static <E extends View> E findViewByIds(View view, int id) {
        return (E) view.findViewById(id);
    }


    /**
     * 自定义透明土司
     * @param context  上下文对象
     * @param content  显示内容
     * @param time  显示时间
     * @param gravity  显示位置
     * @param xOffset  X偏移量
     * @param yOffset  Y轴偏移量
     */
    public static void toast(Context context, String content, int time,
                             int gravity, int xOffset, int yOffset) {
        Toast toast = new Toast(context);
        View mToastView = LayoutInflater.from(context).inflate(R.layout.toast_util, null);
        TextView toast_tv_tip = (TextView) mToastView.findViewById(R.id.toast_tv_tip);
        toast_tv_tip.setText(content);
        toast.setView(mToastView);
        toast.setDuration(time);
        toast.setGravity(gravity, xOffset, yOffset);
        toast.show();
    }
}
