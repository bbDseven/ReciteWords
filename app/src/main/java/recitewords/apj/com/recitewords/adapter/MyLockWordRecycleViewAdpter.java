package recitewords.apj.com.recitewords.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.bean.WordStudy;

/**
 * Created by cjz on 2016/12/22 0022.
 */
public class MyLockWordRecycleViewAdpter extends RecyclerView.Adapter<MyLockWordRecycleViewAdpter.ViewHolders>{

    private Context context;
    private ArrayList<WordStudy> list;

    public MyLockWordRecycleViewAdpter(Context context, ArrayList<WordStudy> list){
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lock_learn_word,parent,false);
        ViewHolders myView = new ViewHolders(view);
        return myView;
    }

    @Override
    public void onBindViewHolder(ViewHolders holder, final int position) {
        holder.lock_lean_word.setText(list.get(position).getWord());
        holder.sound_mark.setText(list.get(position).getSoundmark_american());
        holder.word_mean.setText(list.get(position).getAnswer_right());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolders extends RecyclerView.ViewHolder{

        LinearLayout ll_word;
        TextView lock_lean_word,sound_mark,word_mean;

        public ViewHolders(View itemView) {
            super(itemView);
            ll_word = (LinearLayout) itemView.findViewById(R.id.ll_word);
            lock_lean_word = (TextView)itemView.findViewById(R.id.lock_lean_word);//显示单词
            sound_mark = (TextView)itemView.findViewById(R.id.sound_mark);// 显示音标
            word_mean = (TextView)itemView.findViewById(R.id.word_mean);//显示词义
        }
    }

}