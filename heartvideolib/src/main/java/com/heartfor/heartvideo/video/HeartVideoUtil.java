package com.heartfor.heartvideo.video;

/**
 * Created by admin on 2018/4/30.
 */

public class HeartVideoUtil {

    /**
     * 当前播放时间/总时间格式化
     */
    public static String getTimeProgressFormat(int currentPosition, int duration) {
        int[] currentPosition_array = getMinuteAndSecond(currentPosition);
        int[] duration_array = getMinuteAndSecond(duration);
        return String.format("%02d:%02d/%02d:%02d", currentPosition_array[0], currentPosition_array[1], duration_array[0], duration_array[1]);
    }

    private static int[] getMinuteAndSecond(int mils) {
        mils /= 1000;
        int[] time = new int[2];
        time[0] = mils / 60;
        time[1] = mils % 60;
        return time;
    }

}
