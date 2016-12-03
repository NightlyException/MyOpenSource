package com.nightly.qqmusic.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.nightly.qqmusic.model.LocalMusic;
import com.nightly.qqmusic.myinterface.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by Nightly on 2016/10/11.
 */

public class MediaUtil {
    public static final String ACTION_PLAYING_STATE_CHANGED = "com.nightly.app.action.playingStateChanged";
    public static final String ACTION_PLAYING_SONG_CHANGED = "com.nightly.app.action.playingSongChanged";
    public static int PLAY_MODE = 0;
    private final static String TAG = "nightly";
    public static Song currentPlayingSong = null;
    private static List<Song> currentList;
    private static int currentPosition = -1;
    public static MediaPlayer mediaPlayer;
    public static boolean isPlaying = false;

    private static void sendPlayStateChangeBroadcast(Context context) {
        //发送广播
        Intent intent1 = new Intent(MediaUtil.ACTION_PLAYING_SONG_CHANGED);
        Intent intent2 = new Intent(MediaUtil.ACTION_PLAYING_STATE_CHANGED);
        context.sendBroadcast(intent1);
        context.sendBroadcast(intent2);
    }

    public static void play(final Context context, List<Song> songs, final int position, final Handler handler) {
        Log.e(TAG, "play: 当前播放的歌曲的位置：" + position);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        Log.e(TAG, "play: 1");
        currentList = songs;
        currentPosition = position;
        currentPlayingSong = songs.get(position);
        isPlaying = true;
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
        }
        Log.e(TAG, "play: 2");
        //播放音乐
        try {
            mediaPlayer.reset();
            Log.e(TAG, "play: 3");
            if (currentPlayingSong.isIDownloaded()) {
                //播放本地文件
                mediaPlayer.setDataSource(currentPlayingSong.getIFilePath());
                Log.e(TAG, "本地文件路径: " + currentPlayingSong.getIFilePath());
            } else {
                //在线播放
                mediaPlayer.setDataSource(context, Uri.parse(currentPlayingSong.getIUrl()));
            }
            Log.e(TAG, "play: 4");
            mediaPlayer.prepare();
            Log.e(TAG, "play: 5");
            mediaPlayer.start();
            Log.e(TAG, "play: 6");
            isPlaying = true;
            //发送广播
            sendPlayStateChangeBroadcast(context);
            //当播放结束的时候自动播放下一首
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    context.sendBroadcast(new Intent(ACTION_PLAYING_STATE_CHANGED));
                    currentPlayingSong = null;
                    isPlaying = false;
                    playNext(context, handler);
                }
            });
            Log.e(TAG, "play: 7");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            Log.e(TAG, "非法栈异常");
            e.printStackTrace();
        }
    }


    /**
     * 播放、暂停
     *
     * @param context
     */
    public static void playOrPause(final Context context, Handler handler) {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                isPlaying = false;
            } else {
                isPlaying = true;
                mediaPlayer.start();
            }
            context.sendBroadcast(new Intent(ACTION_PLAYING_STATE_CHANGED));
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "播放器未就绪。", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    /**
     * 播放下一首
     *
     * @param context
     */
    public static void playNext(final Context context, Handler handler) {
        int newPosition = 0;
        switch (PLAY_MODE) {
            case 0:
                //顺序播放模式
                newPosition = (currentPosition + 1) % currentList.size();
                break;
            case 1:
                //随机播放模式
                while ((newPosition = new Random().nextInt(currentList.size())) == currentPosition) {
                }
                break;
            case 2://单曲循环模式
                newPosition = currentPosition;
                break;
        }
        if (currentList != null) {
            switchSong(context, handler, newPosition);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "播放列表为空。", Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    /**
     * 播放上一首
     *
     * @param context
     */
    public static void playPrevious(final Context context, Handler handler) {
        int newPosition = 0;
        switch (PLAY_MODE) {
            case 0:
                //顺序播放模式
                if (currentPosition == 0) {
                    Toast.makeText(context, "当前已经是第一首歌曲。", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    newPosition = currentPosition - 1;
                }
                break;
            case 1:
                //随机播放模式
                while ((newPosition = new Random().nextInt(currentList.size())) == currentPosition) {
                }
                break;
            case 2://单曲循环模式
                newPosition = currentPosition;
                break;
        }
        if (currentList != null) {
            while ((newPosition = new Random().nextInt(currentList.size())) == currentPosition) {
            }
            switchSong(context, handler, newPosition);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "播放列表为空。", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private static void switchSong(Context context, Handler handler, int newPosition) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        play(context, currentList, newPosition, handler);
    }

    /**
     * 读取本地音乐
     *
     * @param context
     * @param dirPath
     * @return
     */
    public static List<LocalMusic> getLocalMusic(Context context, String dirPath) {
        List<LocalMusic> musics = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Audio.Media.DATA + " like ?",
                new String[]{dirPath + "%"},
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER
        );
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID)); // 音乐id
            String title = cursor.getString((cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))); // 音乐标题
            String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)); // 艺术家
            String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)); // 专辑
            String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
            long albumId = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
            long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)); // 时长
            long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小
            String filePath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径
            int isMusic = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media.IS_MUSIC)); // 是否为音乐

            if (isMusic != 0) {
                LocalMusic music = new LocalMusic(id, title, artist, album, displayName, albumId, duration, size, filePath);
                musics.add(music);
            }
        }
        return musics;
    }
}
