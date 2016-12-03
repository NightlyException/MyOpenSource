package com.nightly.qqmusic.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.nightly.qqmusic.R;
import com.nightly.qqmusic.activity.MainActivity;
import com.nightly.qqmusic.myinterface.Song;
import com.nightly.qqmusic.util.MediaUtil;
import com.nightly.qqmusic.util.ThreadUtil;

import java.util.List;

/**
 * Created by Nightly on 2016/10/13.
 */

public class MediaService extends Service {
    private Handler handler;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        Notification notification = initNotification();
        startForeground(1, notification);
    }

    private Notification initNotification() {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, new Intent(this, MainActivity.class), PendingIntent.FLAG_ONE_SHOT);
        Notification notification = new NotificationCompat.Builder(this)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.favorite)
                .setTicker("ticker")
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.all_pressed))
                .setContentTitle("Content Title")
                .setContentText("Content text")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(1, notification);
        return notification;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    public void play(final Context context, final List<Song> songs, final int position) {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                MediaUtil.play(context, songs, position, handler);
            }
        });
    }

    /**
     * 播放、暂停
     *
     * @param context
     */
    public void playOrPause(final Context context) {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                MediaUtil.playOrPause(context, handler);
            }
        });
    }

    /**
     * 播放下一首
     *
     * @param context
     */
    public void playNext(final Context context) {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                MediaUtil.playNext(context, handler);
            }
        });
    }

    /**
     * 播放上一首
     *
     * @param context
     */
    public void playPrevious(final Context context) {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                MediaUtil.playPrevious(context, handler);
            }
        });
    }

    public class MyBinder extends Binder {
        public MediaService getService() {
            return MediaService.this;
        }
    }
}
