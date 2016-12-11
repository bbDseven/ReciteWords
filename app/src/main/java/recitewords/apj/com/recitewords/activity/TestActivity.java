package recitewords.apj.com.recitewords.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import recitewords.apj.com.recitewords.R;
import recitewords.apj.com.recitewords.view.SwipeLayout;

public class TestActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        final SwipeLayout swipeLayout = (SwipeLayout) findViewById(R.id.sl_delete);
        TextView ll_ha = (TextView) findViewById(R.id.tv_ha);
        ll_ha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestActivity.this, "点击了", Toast.LENGTH_SHORT).show();
            }
        });

        (findViewById(R.id.delete_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.closeSwipe();
                Toast.makeText(TestActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
