package recitewords.apj.com.recitewords.bean;

/**词库表lexicon的实体类
 * Created by 陈金振 on 2016/11/28 0028.
 */
public class Lexicon {
    private String book_name;//词书名字
    private String book_desc;//词书备注
    private String word_num;//单词个数
    private String word_num_study;//已学习单词个数
    private String word_num_grasp;//已掌握单词个数
    private String word_num_review;//需复习的单词个数
    private int userID;

    public String getWord_num_study() {
        return word_num_study;
    }

    public String getWord_num_review() {
        return word_num_review;
    }

    public String getWord_num_grasp() {
        return word_num_grasp;
    }

    public String getWord_num() {
        return word_num;
    }

    public int getUserID() {
        return userID;
    }

    public String getBook_desc() {
        return book_desc;
    }

    public String getBook_name() {
        return book_name;
    }

    public void setWord_num_study(String word_num_study) {
        this.word_num_study = word_num_study;
    }

    public void setWord_num_review(String word_num_review) {
        this.word_num_review = word_num_review;
    }

    public void setWord_num_grasp(String word_num_grasp) {
        this.word_num_grasp = word_num_grasp;
    }

    public void setWord_num(String word_num) {
        this.word_num = word_num;
    }

    public void setBook_desc(String book_desc) {
        this.book_desc = book_desc;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

}
