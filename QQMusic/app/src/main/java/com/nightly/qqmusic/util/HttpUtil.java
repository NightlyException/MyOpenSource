package com.nightly.qqmusic.util;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.nightly.qqmusic.model.QueryBean;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by Nightly on 2016/10/8.
 */

public class HttpUtil {
    public static final String APP_ID = "24885";
    public static final String APP_SIGN = "59a814c5da6a450b96e951786ee8df57";
    public static final int TOPS_EUROPE_AND_AMERICA = 3;//欧美
    public static final int TOPS_CHINA = 4;   //内地
    public static final int TOPS_HONGKONG_AND_TAIWAN = 6;//港台
    public static final int TOPS_KOREA = 16;//韩国
    public static final int TOPS_JAPEN = 17;//日本
    public static final int TOPS_BALLAD = 18;//民谣
    public static final int TOPS_ROCK = 19;//摇滚
    public static final int TOPS_SALE = 23;//销量
    public static final int TOPS_HOT = 26;//热歌
    private static OkHttpClient okHttpClient;
    private final static String TAG = "Test";

    /**
     * 获取音乐榜单
     *
     * @param topsID 3=欧美 5=内地 6=港台 16=韩国 17=日本 18=民谣 19=摇滚 23=销量  26=热歌
     * @return
     */
    public static String getTops(String topsID) {
        String url = "https://route.showapi.com/213-4?showapi_appid=" + APP_ID + "&topid=" + topsID + "&showapi_sign=" + APP_SIGN + "";
        return getStringByOkHttp(url);
    }

    /**
     * 根据歌曲ID获取歌词
     *
     * @param songID 歌曲id
     * @return
     */
    public static String getLyric(int songID) {
        String url = "https://route.showapi.com/213-2?musicid=" + songID + "&showapi_appid=" + APP_ID + "&showapi_sign=" + APP_SIGN + "";
        return getStringByOkHttp(url);
    }

    public static String query(String keyword) {
        return query(keyword,1);
    }
    public static String query(String keyword,int page) {
        String url = "https://route.showapi.com/213-1?keyword=" + keyword + "&page="+page+"&showapi_appid=" + APP_ID + "&showapi_sign=" + APP_SIGN + "";
        return getStringByOkHttp(url);
    }

    @NonNull
    public static String getStringByOkHttp(String url) {
        String json = "";
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        Request request = new Request.Builder()
                .tag("Tops")
                .url(url)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                json = response.body().string();
            } else {
                json = "response is not successful";
            }
        } catch (IOException e) {
            json = "IOException";
            e.printStackTrace();
        }
        return json;
    }

    public static boolean downloadMusic(final Context context, final QueryBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean song, String dirPath, Handler handler) {
        boolean downloadSuccess = false;
        InputStream inputStream = null;
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }

        Request request = new Request.Builder()
                .tag("tag")
                .url(song.getDownUrl())
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                inputStream = response.body().byteStream();
                downloadSuccess = MyFileUtil.writeFile(context, inputStream, dirPath, song.getSongname() + ".mp3");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        final boolean finalDownloadSuccess = downloadSuccess;
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, finalDownloadSuccess ? song.getSongname() + "下载成功" : song.getSongname() + "下载失败", Toast.LENGTH_SHORT).show();
            }
        });

        return downloadSuccess;
    }

}
