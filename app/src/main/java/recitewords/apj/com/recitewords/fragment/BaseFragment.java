package recitewords.apj.com.recitewords.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by CGT on 2016/11/22.
 *
 * Fragment基类
 */
public abstract class BaseFragment extends Fragment {

    public Activity mActivity;  //承载Fragment的Activity

    // fragment创建
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    // 处理fragment的布局
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return initView();
    }

    // 承载Fragment的Activity创建完成
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initEvent();
        initData();
    }

    public <E extends View>E findViewByIds(View view, int id){
        return (E) view.findViewById(id);
    }


    /**
     * 子类必须实现初始化布局的方法
     *
     * @return
     */
    public abstract View initView();

    /**
     * 初始化事件，可根据需要是否实现
     */
    public void initEvent(){

    }

    /**
     * 初始化数据，可根据需要是否实现
     */
    public void initData() {

    }

}
