package recitewords.apj.com.recitewords.bean;

/**
 * 词书表book的实体类
 * Created by cjz on 2016/11/28 0028.
 */
public class Book {
    private String word;//英语单词
    private String soundmark_american;//美式音标
    private String pronounce_american;//美式发音
    private String soundmark_british;//英式音标
    private String pronounce_british;//英式发音
    private String word_property;//词性
    private String word_mean;//词义
    private String word_img;//单词相关背景图片
    private String word_is_study;//是否已学习
    private String word_is_grasp;//是否已掌握
    private String book_name;//词书名字
    private int userID;

    public Book(){
    }

    public Book(String word,String soundmark_american,String soundmark_british,String word_mean){
        this.word = word;
        this.soundmark_american = soundmark_american;
        this.soundmark_british = soundmark_british;
        this.word_mean = word_mean;
    }

    public String getBook_name() {
        return book_name;
    }

    public String getPronounce_american() {
        return pronounce_american;
    }

    public int getUserID() {
        return userID;
    }

    public String getPronounce_british() {
        return pronounce_british;
    }

    public String getSoundmark_american() {
        return soundmark_american;
    }

    public String getSoundmark_british() {
        return soundmark_british;
    }

    public String getWord() {
        return word;
    }

    public String getWord_img() {
        return word_img;
    }

    public String getWord_is_grasp() {
        return word_is_grasp;
    }

    public String getWord_is_study() {
        return word_is_study;
    }

    public String getWord_mean() {
        return word_mean;
    }

    public String getWord_property() {
        return word_property;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public void setPronounce_american(String pronounce_american) {
        this.pronounce_american = pronounce_american;
    }

    public void setPronounce_british(String pronounce_british) {
        this.pronounce_british = pronounce_british;
    }

    public void setSoundmark_american(String soundmark_american) {
        this.soundmark_american = soundmark_american;
    }

    public void setSoundmark_british(String soundmark_british) {
        this.soundmark_british = soundmark_british;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public void setWord_img(String word_img) {
        this.word_img = word_img;
    }

    public void setWord_is_grasp(String word_is_grasp) {
        this.word_is_grasp = word_is_grasp;
    }

    public void setWord_is_study(String word_is_study) {
        this.word_is_study = word_is_study;
    }

    public void setWord_mean(String word_mean) {
        this.word_mean = word_mean;
    }

    public void setWord_property(String word_property) {
        this.word_property = word_property;
    }
}
