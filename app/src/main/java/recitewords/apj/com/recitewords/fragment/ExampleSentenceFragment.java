package recitewords.apj.com.recitewords.fragment;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.bean.WordExampleSentence;
import recitewords.apj.com.recitewords.db.dao.ExampleSentenceDao;

/**
 * 例句的展示
 * Created by Seven on 2016/11/30.
 */

public class ExampleSentenceFragment extends BaseFragment {
    private ViewPager vp;
    private String word;    //单词
    private ImageView cursor;   //游标图片
    private List<View> list;    //装View的集合

    public ExampleSentenceFragment(String word){
        this.word = word;
    }
    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.fragment_example_sentence, null);
        vp = (ViewPager) view.findViewById(R.id.example_sentence_vp);
        cursor = (ImageView) view.findViewById(R.id.vp_cursor);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        //从数据库获取单词例句
        WordExampleSentence bean = querySentence(word);
        String example_sentence = bean.getExample_sentence();
        String example_sentence_mean = bean.getExample_sentence_mean();
        String[] example_sentence_means = example_sentence_mean.split("。");
        String[] example_sentences = example_sentence.split("\\.");
        //将例句设置到View控件中并放入集合
        list = new ArrayList<>();
        for (int i=0; i<example_sentence_means.length; i++){
            View view = View.inflate(mActivity, R.layout.viewpager_example_sentence, null);
            TextView vp_example_sentence = (TextView) view.findViewById(R.id.vp_example_sentence);
            TextView vp_example_sentence_mean = (TextView) view.findViewById(R.id.vp_example_sentence_mean);
            vp_example_sentence.setText(example_sentences[i]);
            vp_example_sentence_mean.setText(example_sentence_means[i]);
            list.add(view);
        }
        vp.setAdapter(new MyPagerAdapter());//ViewPager设置适配器
        //ViewPager设置页面改变监听
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //如果是ViewPager第二页则将游标设置到最后
                if (position == 1){
                    cursor.setScaleType(ImageView.ScaleType.FIT_END);
                }else {
                    cursor.setScaleType(ImageView.ScaleType.FIT_START);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    //查询数据库例句获取实体对象
    private WordExampleSentence querySentence(String word) {
        ExampleSentenceDao dao = new ExampleSentenceDao(mActivity);
        WordExampleSentence bean = dao.query(word);
        return bean;
    }

    //适配器
    class MyPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(list.get(position));
            return list.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
