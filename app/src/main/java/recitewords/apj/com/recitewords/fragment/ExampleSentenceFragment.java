package recitewords.apj.com.recitewords.fragment;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.activity.LearnActivity;
import recitewords.apj.com.recitewords.bean.WordExampleSentence;
import recitewords.apj.com.recitewords.db.dao.ExampleSentenceDao;
import recitewords.apj.com.recitewords.util.MediaUtils;
import recitewords.apj.com.recitewords.view.SlidingUpMenu;

/**
 * 例句的展示
 * Created by Seven on 2016/11/30.
 */

public class ExampleSentenceFragment extends BaseFragment implements LearnActivity.OnToggleListner {
    private ViewPager vp;
    private String word;    //单词
    private ImageView cursor;   //游标图片
    private List<View> list;    //装View的集合
    private LinearLayout sentence_pronounce; //例句点击发音
    private String str; //例句
    private SpannableStringBuilder style;   //设置字体颜色


    public ExampleSentenceFragment(String word) {
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
        for (int i = 0; i < example_sentence_means.length; i++) {
            View view = View.inflate(mActivity, R.layout.viewpager_example_sentence, null);
            TextView vp_example_sentence = (TextView) view.findViewById(R.id.vp_example_sentence);
            TextView vp_example_sentence_mean = (TextView) view.findViewById(R.id.vp_example_sentence_mean);
            sentence_pronounce = (LinearLayout) view.findViewById(R.id.sentence_pronounce);
            setSentence_pronounce();
            str = example_sentences[i];
            style = new SpannableStringBuilder(str);
            int start = str.indexOf(word);  //单词第一次出现的索引
            int end = start + word.length();
            style.setSpan(new ForegroundColorSpan(Color.parseColor("#d1f57f")), start, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            vp_example_sentence.setText(style);
            vp_example_sentence_mean.setText(example_sentence_means[i]);
            list.add(view);
        }
        vp.setAdapter(new MyPagerAdapter());//ViewPager设置适配器

        //监听例句是否显示，显示自动播放例句
        LearnActivity learnActivity = (LearnActivity) mActivity;
        learnActivity.setOnToggleListener(this);

        //ViewPager设置页面改变监听
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //如果是ViewPager第二页则将游标设置到最后
                if (position == 1) {
                    cursor.setScaleType(ImageView.ScaleType.FIT_END);
                } else {
                    cursor.setScaleType(ImageView.ScaleType.FIT_START);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    //例句设置监听，点击播放单词
    private void setSentence_pronounce() {
        sentence_pronounce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MediaUtils.playWord(mActivity, "abroad");
            }
        });
    }

    //查询数据库例句获取实体对象
    private WordExampleSentence querySentence(String word) {
        ExampleSentenceDao dao = new ExampleSentenceDao(mActivity);
        WordExampleSentence bean = dao.query(word);
        return bean;
    }


    //监听例句显示状态回调方法
    @Override
    public void onToggleChange(SlidingUpMenu view, boolean isOpen) {
        if (isOpen) {
            //显示例句
            MediaUtils.playWord(mActivity, "abroad");
        }
        // Toast.makeText(mActivity,"当前状态为："+isOpen,Toast.LENGTH_LONG).show();
    }

    //适配器
    class MyPagerAdapter extends PagerAdapter {

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
