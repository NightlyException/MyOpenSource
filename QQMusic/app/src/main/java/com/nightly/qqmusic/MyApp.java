package com.nightly.qqmusic;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by Nightly on 2016/10/11.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //初始化壁画框架
        Fresco.initialize(this);
    }
}
