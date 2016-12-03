package com.nightly.qqmusic.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.nightly.qqmusic.model.LyricBean;
import com.nightly.qqmusic.model.QueryBean;
import com.nightly.qqmusic.model.TopsBean;

/**
 * Created by Nightly on 2016/10/8.
 */

public class JsonUtil {
    public static TopsBean parseTopsBean(String json){
        TopsBean topsBean;
        try {
            //在某些情况下，服务器可能返回的不是标准的topsBean（服务器出错、连接失败等）
            topsBean = new Gson().fromJson(json, TopsBean.class);
        } catch (Exception e){
            //当解析出错的时候，将topsBean设为null
            topsBean=null;
            e.printStackTrace();
        }
        return topsBean;
    }
    public static QueryBean parseQueryBean(String json){
        QueryBean queryBean = null;
        try {
            queryBean = new Gson().fromJson(json, QueryBean.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return queryBean;
    }
    public static LyricBean parseLyricBean(String json){
        LyricBean lyricBean = null;
        try {
            lyricBean = new Gson().fromJson(json, LyricBean.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return lyricBean;
    }
}
