package recitewords.apj.com.recitewords.fragment;

import android.nfc.FormatException;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.bean.Book;
import recitewords.apj.com.recitewords.db.dao.BookDao;
import recitewords.apj.com.recitewords.globle.AppConfig;
import recitewords.apj.com.recitewords.util.DateUtil;
import recitewords.apj.com.recitewords.util.MyDecoration;

/**
 * Created by Greetty on 2016/12/9.
 * <p/>
 * 今天已学习单词
 */
public class TodayLearnFragment extends BaseFragment {

    public class ViewHolder {
        RecyclerView today_rv_learn;
    }

    private ViewHolder holder;
    private BookDao bookDao;
    private List<Book> list;
    private List<Integer> mClickList;
    private String today;  //今天日期

    @Override
    public View initView() {
        holder = new ViewHolder();
        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_today_learn, null);
        holder.today_rv_learn = findViewByIds(view, R.id.today_rv_learn);
        return view;
    }

    @Override
    public void initData() {
        mClickList=new ArrayList<>();
        today = DateUtil.getNowDate("yyyy-MM-dd");
        bookDao = new BookDao(mActivity);
        list = bookDao.queryDayLearn("", today);   //词书名字为空，默认为从查询全部
        holder.today_rv_learn.setAdapter(new MyAdapter());
        //设置一个LinearLayoutManager
        holder.today_rv_learn.setLayoutManager(new LinearLayoutManager(mActivity));

    }

    /**
     * 适配器
     */
    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = LayoutInflater.from(mActivity).inflate(R.layout.item_word_date, parent, false);
                return new MyViewHolderOne(view);

            } else {
                view = LayoutInflater.from(mActivity).inflate(R.layout.item_word, parent, false);
                return new MyViewHolderTwo(view);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {


            switch (getItemViewType(position)) {
                case 0:

                    final MyViewHolderOne holderOne = (MyViewHolderOne) holder;
                    holderOne.item_ll_root.setTag(position);
                    holderOne.item_word.setText(list.get(position).getWord());
                    holderOne.item_word_mean.setText(list.get(position).getWord_mean());
                    String[] time = today.split("-");
                    ((MyViewHolderOne) holder).item_word_date.
                            setText(time[0]+"年"+time[1]+"月"+time[2]+"日    "+list.size()+"个单词");
                    //显示隐藏词义
                    holderOne.item_ll_root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //item复用问题
                            if (!mClickList.contains(holderOne.item_ll_root.getTag())) {
                                mClickList.add(position);
                                holderOne.item_word_mean.setVisibility(View.VISIBLE);
                            } else {
                                for (int i = 0; i < mClickList.size(); i++) {
                                    if (mClickList.get(i) == holderOne.item_ll_root.getTag()) {
                                        mClickList.remove(i);
                                        holderOne.item_word_mean.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    });
                    //最后一条横线隐藏
                    if (position == list.size()) {
                        holderOne.item_word_divide.setVisibility(View.GONE);
                    }
                    //解决item复用问题
                    if (mClickList.contains(position)) {
                        holderOne.item_word_mean.setVisibility(View.VISIBLE);
                    } else {
                        holderOne.item_word_mean.setVisibility(View.GONE);
                    }
                    break;
                case 1:

                    final MyViewHolderTwo holderTwo = (MyViewHolderTwo) holder;
                    holderTwo.item_ll_root.setTag(position);
                    holderTwo.item_word.setText(list.get(position).getWord());
                    holderTwo.item_word_mean.setText(list.get(position).getWord_mean());

                    //显示隐藏词义
                    holderTwo.item_ll_root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //item复用问题
                            if (!mClickList.contains(holderTwo.item_ll_root.getTag())) {
                                mClickList.add(position);
                                holderTwo.item_word_mean.setVisibility(View.VISIBLE);
                            } else {
                                for (int i = 0; i < mClickList.size(); i++) {
                                    if (mClickList.get(i) == holderTwo.item_ll_root.getTag()) {
                                        mClickList.remove(i);
                                        holderTwo.item_word_mean.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                    });
                    //最后一条横线隐藏
                    if (position == list.size()) {
                        holderTwo.item_word_divide.setVisibility(View.GONE);
                    }
                    //解决item复用问题
                    if (mClickList.contains(position)) {
                        holderTwo.item_word_mean.setVisibility(View.VISIBLE);
                    } else {
                        holderTwo.item_word_mean.setVisibility(View.GONE);
                    }

                    break;
                default:
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }


        public class MyViewHolderOne extends RecyclerView.ViewHolder {

            TextView item_word;    //单词
            TextView item_word_mean;    //词义
            LinearLayout item_ll_root;  //报错词义布局
            ImageView item_word_divide;  //分割线
            TextView item_word_date;  //日期

            public MyViewHolderOne(View view) {
                super(view);
                item_word = (TextView) view.findViewById(R.id.item_word);
                item_word_mean = (TextView) view.findViewById(R.id.item_word_mean);
                item_ll_root = (LinearLayout) view.findViewById(R.id.item_ll_root);
                item_word_divide = (ImageView) view.findViewById(R.id.item_word_divide);
                item_word_date = (TextView) view.findViewById(R.id.item_word_date);
            }
        }


        public class MyViewHolderTwo extends RecyclerView.ViewHolder {

            TextView item_word;    //单词
            TextView item_word_mean;    //词义
            LinearLayout item_ll_root;  //报错词义布局
            ImageView item_word_divide;  //分割线

            public MyViewHolderTwo(View view) {
                super(view);
                item_word = (TextView) view.findViewById(R.id.item_word);
                item_word_mean = (TextView) view.findViewById(R.id.item_word_mean);
                item_ll_root = (LinearLayout) view.findViewById(R.id.item_ll_root);
                item_word_divide = (ImageView) view.findViewById(R.id.item_word_divide);
            }
        }

    }

    /**
     * 获取今天复习单词的总数
     * @return
     */
    public int getLearnSum(){
        return list.size();
    }
}
