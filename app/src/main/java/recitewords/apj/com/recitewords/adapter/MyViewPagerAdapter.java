package recitewords.apj.com.recitewords.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**用于显示activitymenu.xml文件的ViewPager
 * Created by Administrator on 2016/11/22 0022.
 * 作者：陈金振
 */
public class MyViewPagerAdapter extends PagerAdapter{

    private Context context;
    private List<View> viewList;

    public MyViewPagerAdapter(Context context,List<View> viewList){
        this.context = context;
        this.viewList = viewList;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(viewList.get(position));
        return viewList.get(position);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(viewList.get(position));
    }

    @Override
    public int getCount() {
        return viewList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
