package recitewords.apj.com.recitewords.fragment;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.bean.Book;
import recitewords.apj.com.recitewords.db.dao.BookDao;
import recitewords.apj.com.recitewords.globle.AppConfig;
import recitewords.apj.com.recitewords.util.DateUtil;
import recitewords.apj.com.recitewords.view.SwipeLayout;

/**
 * Created by Greetty on 2016/12/10.
 * <p/>
 * 已学习的全部单词
 */
public class AllGraspFragment extends BaseFragment {

    public AllGraspFragment(String mode) {
        this.mode = mode;
    }


    public class ViewHolder {
        RecyclerView all_rv_grasp;
    }

    private ViewHolder holder;
    private BookDao bookDao;
    private List<Book> list;
    private List<Integer> mClickList;  //记录显示词义的集合
    private List<SwipeLayout> mOpenViews;  //记录显示删除的集合
    private String day;  //日期
    private String mode = AppConfig.MODE_STATISTICS_GRASP;  //展示那些数据

    @Override
    public View initView() {
        holder = new ViewHolder();
        View view = LayoutInflater.from(mActivity).inflate(R.layout.fragment_all_grasp, null);
        holder.all_rv_grasp = findViewByIds(view, R.id.all_rv_grasp);
        return view;
    }

    @Override
    public void initData() {
        mClickList = new ArrayList<>();
        mOpenViews = new ArrayList<>();
        bookDao = new BookDao(mActivity);
        if (mode.equals(AppConfig.MODE_STATISTICS_GRASP)) {
            list = bookDao.queryAllGrasp("");  //词书名字为空，默认为从查询全部
        } else if (mode.equals(AppConfig.MODE_LIBRARY_GRASP)) {
            list = bookDao.queryAllGrasp(AppConfig.BOOK_NAME);
        }else if (mode.equals(AppConfig.MODE_BOOK_NAME_AND_NEWWORDS)){
            //查询当前词书和生词本
            list = bookDao.queryAllGrasp(AppConfig.MODE_BOOK_NAME_AND_NEWWORDS);
        }else if (mode.equals(AppConfig.BOOK_NEW_WORDS)){
            list=bookDao.queryAllWOrd(AppConfig.BOOK_NEW_WORDS);  //生词本单词
        }
        //单词按日期排序
        sortDate(list);
        handleDate((ArrayList<Book>) list);
        holder.all_rv_grasp.setAdapter(new MyAdapter());
        //设置一个LinearLayoutManager
        holder.all_rv_grasp.setLayoutManager(new LinearLayoutManager(mActivity));
        holder.all_rv_grasp.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    // 需要关闭
                    closeAll();
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    /**
     * 适配器
     */
    class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;
            if (viewType == 0) {
                view = LayoutInflater.from(mActivity).inflate(R.layout.item_date, parent, false);
                return new MyViewHolderOne(view);
            } else {
                view = LayoutInflater.from(mActivity).inflate(R.layout.item_sweep_word, parent, false);
                return new MyViewHolderTwo(view);
            }
        }


        @Override
        public int getItemViewType(int position) {
            if (position > 0) {
                if (!list.get(position).getDate().equals(list.get(position - 1).getDate())) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return 0;
            }
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

//            Log.e("ha", "当前单词是：" + list.get(position).getWord() + "当前日期是" +
//                    list.get(position).getDate()+"  我的位置是："+position);

            switch (getItemViewType(position)) {
                case 0:
                    final MyViewHolderOne holderOne = (MyViewHolderOne) holder;
                    day = list.get(position).getDate();
                    String[] time = day.split("-");
                    ((MyViewHolderOne) holder).item_word_date.
                            setText(time[0] + "年" + time[1] + "月" + time[2] + "日    " +
                                    (calculateDate(list, list.get(position).getDate()) - 1) + "个单词");
//                    //显示隐藏词义
//                    holderOne.item_ll_root.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //item复用问题
//                            if (!mClickList.contains(holderOne.item_ll_root.getTag())) {
//                                mClickList.add(position);
//                                holderOne.item_word_mean.setVisibility(View.VISIBLE);
//                            } else {
//                                for (int i = 0; i < mClickList.size(); i++) {
//                                    if (mClickList.get(i) == holderOne.item_ll_root.getTag()) {
//                                        mClickList.remove(i);
//                                        holderOne.item_word_mean.setVisibility(View.GONE);
//                                    }
//                                }
//                            }
//                        }
//                    });
//                    //最后一条横线隐藏
//                    if (position == list.size()) {
//                        holderOne.item_word_divide.setVisibility(View.GONE);
//                    }
//                    //解决item复用问题
//                    if (mClickList.contains(position)) {
//                        holderOne.item_word_mean.setVisibility(View.VISIBLE);
//                    } else {
//                        holderOne.item_word_mean.setVisibility(View.GONE);
//                    }
                    break;
                case 1:
                    final MyViewHolderTwo holderTwo = (MyViewHolderTwo) holder;
                    holderTwo.item_ll_root.setTag(position);
                    holderTwo.sl_word.setTag(position);
                    holderTwo.item_word.setText(list.get(position).getWord());
                    holderTwo.item_delete.setText("删除");
                    holderTwo.item_word_mean.setText(list.get(position).getWord_mean());

                    // 显示隐藏词义，和删除按钮
                    holderTwo.item_ll_root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //  item显示词义复用问题
                            if (!mClickList.contains(holderTwo.item_ll_root.getTag())) {
                                if (holderTwo.sl_word.getOpenSwipeState()) {  //如果打开了删除按钮
                                    holderTwo.sl_word.closeSwipe();  //点击不显示词义，直接关闭删除按钮
                                } else {
                                    mClickList.add(position);  //如果没有打开删除按钮，就显示词义
                                    holderTwo.item_word_mean.setVisibility(View.VISIBLE);
                                }
                            } else {
                                for (int i = 0; i < mClickList.size(); i++) {
                                    if (mClickList.get(i) == holderTwo.item_ll_root.getTag()) {
                                        if (holderTwo.sl_word.getOpenSwipeState()) {  //如果打开了删除按钮
                                            holderTwo.sl_word.closeSwipe();  //关闭删除按钮，不隐藏词义
                                        } else {
                                            mClickList.remove(i);  //如果没有打开删除按钮，就隐藏词义
                                            holderTwo.item_word_mean.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        }
                    });

                    //删除
                    holderTwo.item_delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            closeAll();
                            int dateType = 0;  //一种类型的日期总数
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(position).getDate().equals(list.get(i).getDate())) {
                                    dateType++;
                                }
                            }
                            if (dateType > 2) {
                                //重新学习
                                if (mode.equals(AppConfig.MODE_STATISTICS_GRASP)) {  //LIBRARY
                                    bookDao.updateLearnAgain(AppConfig.BOOK_NAME, list.get(position).getWord());
                                } else if (mode.equals(AppConfig.MODE_LIBRARY_GRASP)||
                                        mode.equals(AppConfig.MODE_BOOK_NAME_AND_NEWWORDS)) {  //STATISTICS
                                    bookDao.updateLearnAgain(AppConfig.BOOK_NAME, list.get(position).getWord());
                                }else if (mode.equals(AppConfig.BOOK_NEW_WORDS)){
                                    bookDao.deleteWords(AppConfig.BOOK_NEW_WORDS,list.get(position).getWord());
                                }
                                holderTwo.item_word_mean.setVisibility(View.VISIBLE);
                                list.remove(position);
                            } else {
                                //重新学习
                                if (mode.equals(AppConfig.MODE_STATISTICS_GRASP)) {
                                    bookDao.updateLearnAgain(AppConfig.BOOK_NAME, list.get(position).getWord());
                                } else if (mode.equals(AppConfig.MODE_LIBRARY_GRASP)||
                                        mode.equals(AppConfig.MODE_BOOK_NAME_AND_NEWWORDS)) {
                                    bookDao.updateLearnAgain(AppConfig.BOOK_NAME, list.get(position).getWord());
                                }else if (mode.equals(AppConfig.BOOK_NEW_WORDS)){
                                    bookDao.deleteWords(AppConfig.BOOK_NEW_WORDS,list.get(position).getWord());  //删除单词
                                }
                                holderTwo.item_word_mean.setVisibility(View.VISIBLE);
                                list.remove(position);
                                list.remove(position - 1);
                            }
                            for (int i = 0; i < mClickList.size(); i++) {
                                if (mClickList.get(i) == holderTwo.item_ll_root.getTag()) {
                                    mClickList.remove(i);
                                }
                            }
                            notifyDataSetChanged();
                        }
                    });
                    //item显示删除复用问题
                    holderTwo.sl_word.setOnSweepListener(new SwipeLayout.OnSweepListener() {
                        @Override
                        public void OnSweepChanged(SwipeLayout view, boolean mIsOpened) {
                            if (mIsOpened) {
                                closeAll();
                                mOpenViews.add(view);
                            } else {
                                mOpenViews.remove(view);
                            }
                        }
                    });

                    //最后一条横线隐藏
                    if (position == list.size()) {
                        holderTwo.item_word_divide.setVisibility(View.GONE);
                    }
                    //解决item显示词义复用问题
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

            //            TextView item_word;    //单词
//            TextView item_word_mean;    //词义
//            LinearLayout item_ll_root;  //报错词义布局
//            ImageView item_word_divide;  //分割线
            TextView item_word_date;  //日期

            public MyViewHolderOne(View view) {
                super(view);
//                item_word = (TextView) view.findViewById(R.id.item_word);
//                item_word_mean = (TextView) view.findViewById(R.id.item_word_mean);
//                item_ll_root = (LinearLayout) view.findViewById(R.id.item_ll_root);
//                item_word_divide = (ImageView) view.findViewById(R.id.item_word_divide);
                item_word_date = (TextView) view.findViewById(R.id.item_word_date);
            }
        }


        public class MyViewHolderTwo extends RecyclerView.ViewHolder {

            TextView item_word;    //单词
            TextView item_word_mean;    //词义
            LinearLayout item_ll_root;  //报错词义布局
            ImageView item_word_divide;  //分割线
            SwipeLayout sl_word;  //滑动删除
            TextView item_delete;  //删除

            public MyViewHolderTwo(View view) {
                super(view);
                item_word = (TextView) view.findViewById(R.id.item_word);
                item_word_mean = (TextView) view.findViewById(R.id.item_word_mean);
                item_ll_root = (LinearLayout) view.findViewById(R.id.item_ll_root);
                item_word_divide = (ImageView) view.findViewById(R.id.item_word_divide);
                sl_word = (SwipeLayout) view.findViewById(R.id.sl_word);
                item_delete = (TextView) view.findViewById(R.id.item_delete);
            }
        }

    }

    /**
     * 关闭所有打开窗口
     */
    public void closeAll() {
        // for循环不安全，用迭代
        ListIterator<SwipeLayout> iterator = mOpenViews.listIterator();
        while (iterator.hasNext()) {
            SwipeLayout next = iterator.next();
            next.closeSwipe();
        }
        mOpenViews.clear();
    }


//
//    @Override
//    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        if (scrollState != AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
//            // 需要关闭
//            closeAll();
//        }
//    }
//
//    @Override
//    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//
//    }

//    @Override
//    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//        if (scrollState != OnScrollListener.SCROLL_STATE_IDLE) {
//            // 需要关闭
//            closeAll();
//        }
//    }


    /**
     * 日期排序
     * * @param   list 数据
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
     * 处理日期，当两个日期不同时，日期插入一个和前者相同的日期，用于显示日期和学期情况
     *
     * @param list 数据
     * @return list
     */
    public List<Book> handleDate(ArrayList<Book> list) {
        ArrayList<Book> books = list;
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                if (!list.get(i).getDate().equals(list.get(i - 1).getDate())) {
                    books.add(i, list.get(i));
                }
            } else if (i == 0) {
                books.add(0, list.get(0));
            }
        }
        return books;
    }

    /**
     * 计算一个集合中同一个日期的个数
     *
     * @param list 数据源
     * @param date 寻找的日期
     * @return int
     */
    public int calculateDate(List<Book> list, String date) {
        int sum = 0;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getDate().equals(date)) {
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
