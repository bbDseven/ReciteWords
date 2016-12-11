package recitewords.apj.com.recitewords.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Greetty on 2016/12/11.
 *
 * 自定义RecyclerView
 */
public class MyRecyclerView extends RecyclerView {

    private float downX;
    private float downY;

    public MyRecyclerView(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    public MyRecyclerView(Context context) {
        super(context);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = ev.getX();
                float moveY = ev.getY();

                if (Math.abs(moveX - downX) > Math.abs(moveY - downY)) {
                    return false;
                }

                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }
}
