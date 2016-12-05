package recitewords.apj.com.recitewords.util;

import android.content.Context;
import android.media.MediaPlayer;

import java.io.IOException;

import recitewords.apj.com.recitewords.R;

/**
 * Created by Seven on 2016/12/1.
 */

public class MediaUtils {
    private static MediaPlayer player;
    //音频数组
    private static int[] media = new int[]{R.raw.abandon, R.raw.ability, R.raw.able, R.raw.aboard, R.raw.about,
    R.raw.abroad, R.raw.absorb, R.raw.angry, R.raw.animal, R.raw.anniversary, R.raw.announce,
    R.raw.bankrupt, R.raw.barber, R.raw.computer, R.raw.economic, R.raw.election, R.raw.murder, R.raw.progress,
    R.raw.religious, R.raw.smart};

    //播放音频， 传入上下文对象以及音频数组对应的下标，播放对应的单词
    public static void playWord(Context context, int id){
        if (player == null) {
            player = MediaPlayer.create(context, media[id]);
        }
        if (player.isPlaying()){
            player.stop();
            try {
                player.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            player.start();
        }
    }

    /**
     * 获取当前单词的语音位置
     * @return
     */
    public int getPronounceIndex(){
        return  0;
    }
}
