package recitewords.apj.com.recitewords.util;

import android.util.Log;

import java.util.Random;

/**
 * Created by Greetty on 2016/12/6.
 * <p/>
 * 获取随机数工具类
 */
public class NumUtil {

    /**
     * 生成0-length的随机数
     *
     * @param length //随机数最大值
     * @return 随机数
     */
    public static int random(int length) {
        Random r = new Random();
        int num = r.nextInt(length);
        return num;
    }

    /**
     * 生成三个0-length的随机数
     *
     * @param length 最大值
     * @param num    不能等于该值
     * @return 随机数数组
     */
    public static int[] random3(int length, int num) {
        int[] random = new int[3];
        Random r = new Random();
        //第一个随机数
        int num1 = r.nextInt(length);
        while (num1 == num) {
            num1 = r.nextInt(length);
        }
        random[0] = num1;
        //第二个随机数
        int num2 = r.nextInt(length);
        while (num2 == num1) {
            num2 = r.nextInt(length);
        }
        while (num2 == num) {
            num2 = r.nextInt(length);
        }
        random[1] = num2;
        //第三个随机数
        int num3 = r.nextInt(length);
        while (num3 == num1) {
            num3 = r.nextInt(length);
        }
        while (num3 == num2) {
            num3 = r.nextInt(length);
        }
        while (num3 == num) {
            num3 = r.nextInt(length);
        }
        random[2] = num3;
        return random;
    }
}
