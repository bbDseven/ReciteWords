package recitewords.apj.com.recitewords.bean;

/**
 * Created by Seven on 2016/12/8.
 */

public class User {
    private String sign_in; //签到
    private int sign_in_continue;  //连续签到天数
    private int cool_money; //酷币

    public String getSign_in() {
        return sign_in;
    }

    public void setSign_in(String sign_in) {
        this.sign_in = sign_in;
    }

    public int getSign_in_continue() {
        return sign_in_continue;
    }

    public void setSign_in_continue(int sign_in_continue) {
        this.sign_in_continue = sign_in_continue;
    }

    public int getCool_money() {
        return cool_money;
    }

    public void setCool_money(int cool_money) {
        this.cool_money = cool_money;
    }
}
