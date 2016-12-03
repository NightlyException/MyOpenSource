package com.nightly.qqmusic.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nightly on 2016/10/13.
 */

public class LrcUtil {
    /**
     * 解析歌词
     *
     * @param lyric
     * @param duration
     * @return
     */
    public static List<Map<String, Object>> parseLrcStrToData(String lyric, int duration) {
        List<Map<String, Object>> data = new ArrayList<>();
        int lineCount = 0;

        String[] temps = lyric.split("\n");
        for (int i = 0; i < temps.length; i++) {
            String temp = temps[i];
            if (temp.startsWith("[0")) {
                String timeStr = temp.substring(temp.indexOf("[")+1, temp.indexOf("]"));
                long start = DateTimeUtil.parseTimeStrToLong(timeStr);
                String text = temp.substring(temp.indexOf("]") + 1);
                HashMap<String, Object> map = new HashMap<>();
                map.put("start", start);
                map.put("text", text);
                map.put("end", null);
                if (i == temps.length - 1) {
                    map.put("end", ((long) duration));
                }
                if (lineCount > 0) {
                    data.get(lineCount - 1).put("end", start);
                }
                data.add(map);
                lineCount++;
            }
        }
        return data;
    }
}
