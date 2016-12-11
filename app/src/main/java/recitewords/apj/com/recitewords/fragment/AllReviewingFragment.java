package recitewords.apj.com.recitewords.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.bean.Book;
import recitewords.apj.com.recitewords.db.dao.BookDao;
import recitewords.apj.com.recitewords.globle.AppConfig;
import recitewords.apj.com.recitewords.util.DateUtil;

/**
 * Created by Greetty on 2016/12/10.
 * <p/>
 * 已掌握的全部单词
 */
public class AllReviewingFragment extends BaseFragment {

    public class ViewHolder {
        RecyclerView all_rv_reviewing;
    }

    private ViewHolder holder;
    private BookDao bookDao;
    private List<Book> list;
    private List<Integer> mClickList;
    private String day;  //日期

    @Override
    public View initView() {
        holder = new ViewHolder();
        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_all_reviewing, null);
        holder.all_rv_reviewing = findViewByIds(view, R.id.all_rv_reviewing);
        return view;
    }

    @Override
    public void initData() {
        mClickList = new ArrayList<>();
        bookDao = new BookDao(mActivity);
        list = bookDao.queryAllRevewing(AppConfig.BOOK_NAME);
        //单词按日期排序
        sortDate(list);
        holder.all_rv_reviewing.setAdapter(new MyAdapter());
        //设置一个LinearLayoutManager
        holder.all_rv_reviewing.setLayoutManager(new LinearLayoutManager(mActivity));

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
            if (position>0){
                if (!list.get(position).getDate().equals(list.get(position-1).getDate())){
                   return 0;
                }else{
                    return 1;
                }
            }else {
                return 0;
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
                    day=list.get(position).getDate();
                    String[] time = day.split("-");
                    ((MyViewHolderOne) holder).item_word_date.
                            setText(time[0] + "年" + time[1] + "月" + time[2] + "日    " +
                                    calculateDate(list,list.get(position).getDate()) + "个单词");
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
     * 日期排序
     *
     * @return list
     */
    public List<Book> sortDate(List<Book> list) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd");
        // 冒泡排序
        Book book = null;
        for (int i = list.size() - 1; i > 0; --i) {
            for (int j = 0; j < i; ++j) {

                try {
                    //从大到小的排序
                    if (format.parse(list.get(j + 1).getDate()).
                            after(format.parse(list.get(j).getDate()))) {
                        book = list.get(j);
                        list.set(j, list.get(j + 1));
                        list.set(j + 1, book);
                    } else {
                        //从小到大的排序
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }


    /**
     * 计算一个集合中同一个日期的个数
     * @param list  数据源
     * @param date  寻找的日期
     * @return  int
     */
    public int calculateDate(List<Book> list,String date){
        int sum=0;
        for (int i=0;i<list.size();i++){
            if (list.get(i).getDate().equals(date)){
                sum++;
            }
        }
        return sum;
    }
    /**
     * 获取今天复习单词的总数
     *
     * @return
     */
    public int getLearnSum() {
        return list.size();
    }
}
