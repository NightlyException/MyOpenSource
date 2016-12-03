package com.nightly.qqmusic.util;

import android.util.Log;

/**
 * Created by Nightly on 2016/10/12.
 */

public class DateTimeUtil {
    public static final String TAG = "Test";
    public static String durationMillisToString(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long secs = seconds % 60;

        String secsStr = secs < 10 ? "0" + secs : "" + secs;
        String minutesStr = minutes < 10 ? "0" + minutes : "" + minutes;
        return minutesStr + ":" + secsStr;
    }

    public static long parseTimeStrToLong(String timeStr) {
        Log.e(TAG, "parseTimeStrToLong: "+timeStr);
        long minMillis = Long.valueOf(timeStr.substring(0, 2)) * 60 * 1000;
        long secMills = Long.valueOf(timeStr.substring(timeStr.indexOf(":") + 1, timeStr.indexOf("."))) * 1000;
        long oddMillis = Long.valueOf(timeStr.substring(timeStr.indexOf(".") + 1)) * 10;
        return minMillis + secMills + oddMillis;
    }
}
