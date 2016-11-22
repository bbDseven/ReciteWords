package recitewords.apj.com.recitewords.activity;

import android.app.Activity;
import android.view.View;

/**
 * Created by Seven on 2016/11/18.
 */

public class BaseActivity extends Activity {
    public <E extends View>E findViewByIds(int id){
        return (E)findViewById(id);
    }
}
