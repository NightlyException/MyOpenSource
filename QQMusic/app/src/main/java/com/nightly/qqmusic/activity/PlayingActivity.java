package com.nightly.qqmusic.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nightly.qqmusic.R;
import com.nightly.qqmusic.model.LyricBean;
import com.nightly.qqmusic.myinterface.SongPlayerActivity;
import com.nightly.qqmusic.util.DateTimeUtil;
import com.nightly.qqmusic.util.HttpUtil;
import com.nightly.qqmusic.util.JsonUtil;
import com.nightly.qqmusic.util.LrcUtil;
import com.nightly.qqmusic.util.MediaUtil;
import com.nightly.qqmusic.util.ThreadUtil;
import com.nightly.qqmusic.widget.LrcView;
import com.nightly.qqmusic.widget.TextProgressBar;

import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.nightly.qqmusic.util.MediaUtil.ACTION_PLAYING_SONG_CHANGED;
import static com.nightly.qqmusic.util.MediaUtil.ACTION_PLAYING_STATE_CHANGED;
import static com.nightly.qqmusic.util.MediaUtil.PLAY_MODE;
import static com.nightly.qqmusic.util.MediaUtil.currentPlayingSong;

public class PlayingActivity extends SongPlayerActivity implements LrcView.OnLyricChangedListener {
    @Bind(R.id.sdvCover)
    SimpleDraweeView sdvCover;
    @Bind(R.id.tvBack)
    TextView tvBack;
    @Bind(R.id.tvSongName)
    TextView tvSongName;
    @Bind(R.id.tvMore)
    TextView tvMore;
    @Bind(R.id.lrcView)
    LrcView lrcView;
    @Bind(R.id.llDots)
    LinearLayout llDots;
    @Bind(R.id.tvCurrentTime)
    TextView tvCurrentTime;
    @Bind(R.id.seekbar)
    SeekBar seekbar;
    @Bind(R.id.tvTotalTime)
    TextView tvTotalTime;
    @Bind(R.id.llProgress)
    LinearLayout llProgress;
    @Bind(R.id.tvPrevious)
    TextView tvPrevious;
    @Bind(R.id.tvPlayPause)
    TextView tvPlayPause;
    @Bind(R.id.tvNext)
    TextView tvNext;
    @Bind(R.id.llControl)
    LinearLayout llControl;
    @Bind(R.id.cbFavorite)
    CheckBox cbFavorite;
    @Bind(R.id.tvMode)
    TextView tvMode;
    @Bind(R.id.tvDownloaded)
    TextView tvDownloaded;
    @Bind(R.id.tvShare)
    TextView tvShare;
    @Bind(R.id.tvList)
    TextView tvList;
    @Bind(R.id.llBottom)
    LinearLayout llBottom;
    @Bind(R.id.flRoot)
    FrameLayout flRoot;
    @Bind(R.id.textProgressBar)
    TextProgressBar textProgressBar;
    private final int LYRIC_GOT = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LYRIC_GOT:
                    Log.e(TAG, "handleMessage: " + ((String) msg.obj));
                    showLyric(msg);
                    break;
            }
        }
    };
    private int duration;
    private final String TAG = "Test";
    private Runnable updateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            autoUpdateSeekBar();
        }
    };
    private String lyric;
    private MyReceiver myReceiver;
    private IntentFilter intentFilter;
    private boolean activityStopped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        ButterKnife.bind(this);
        initLrcView();
        initTvMode();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updatePlayState();
        updateLrcView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (myReceiver == null || intentFilter == null) {
            myReceiver = new MyReceiver();
            intentFilter = new IntentFilter();
            intentFilter.addAction(MediaUtil.ACTION_PLAYING_SONG_CHANGED);
            intentFilter.addAction(MediaUtil.ACTION_PLAYING_STATE_CHANGED);
        }
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: ");
        updatePlayState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityStopped = true;
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }

    }

    /**
     * 歌词界面初始化
     */
    private void initLrcView() {
        lrcView.setListener(this);
        textProgressBar.setVisibility(View.VISIBLE);
        textProgressBar.setBgColor(Color.WHITE);
        textProgressBar.setFgColor(getResources().getColor(R.color.toolbarGreen));
        textProgressBar.setTextSize(30);
    }

    /**
     * 显示歌词
     *
     * @param msg
     */
    private void showLyric(Message msg) {
        String json = (String) msg.obj;
        LyricBean lyricBean = JsonUtil.parseLyricBean(json);
        if (lyricBean != null) {
            if (lyric != null) {
                return;
            }
            lyric = lyricBean.getShowapi_res_body().getLyric();
            //歌词处理
            lyric = lyric.replaceAll("&#58;", ":");
            lyric = lyric.replaceAll("&#32;", " ");
            lyric = lyric.replaceAll("&#40;", "(");
            lyric = lyric.replaceAll("&#41;", ")");
            lyric = lyric.replaceAll("&#46;", ".");
            lyric = lyric.replaceAll("&#10;", "\n");
            lyric = lyric.replaceAll("&#38;", "&");
            lyric = lyric.replaceAll("&#45;", "-");
            int duration = MediaUtil.mediaPlayer.getDuration();
            Log.e(TAG, "showLyric: " + lyricBean);
            List<Map<String, Object>> data = LrcUtil.parseLrcStrToData(lyric, duration);
            Log.e(TAG, "showLyric: " + data);
            if (data != null && data.size() > 0) {
                lrcView.setData(data);
                startUpdateLrcView();
            } else {
                Toast.makeText(this, "歌词获取失败。", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 更新播放状态
     */
    private void updatePlayState() {
        if (MediaUtil.mediaPlayer.isPlaying() || MediaUtil.mediaPlayer != null) {
            //获取正在播放的音乐的总时长和当前时长
            duration = MediaUtil.mediaPlayer.getDuration();
            tvCurrentTime.setText(DateTimeUtil.durationMillisToString(MediaUtil.mediaPlayer.getCurrentPosition()));
            tvTotalTime.setText(DateTimeUtil.durationMillisToString(duration));//歌曲总时间
            tvSongName.setText(MediaUtil.currentPlayingSong.getISongName());
            String albumpicBig = MediaUtil.currentPlayingSong.getIAlbumpicBig();
            if (albumpicBig != null) {
                sdvCover.setImageURI(Uri.parse(albumpicBig));
            }
            setTvPlayPauseBG();
            Log.e(TAG, "更新界面");
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    int progress = seekbar.getProgress();
                    MediaUtil.mediaPlayer.seekTo((int) (duration * (progress / 100f)));
                }
            });
            autoUpdateSeekBar();
        }
    }

    /**
     * seekBar进度更新
     */
    private void autoUpdateSeekBar() {
        int currentPosition = MediaUtil.mediaPlayer.getCurrentPosition();
        tvCurrentTime.setText(DateTimeUtil.durationMillisToString(currentPosition));
        int progress = (int) (currentPosition * 100 / (float) duration);
        seekbar.setProgress(progress);
        if (MediaUtil.mediaPlayer.isPlaying() && MediaUtil.mediaPlayer != null && !activityStopped) {
            handler.postDelayed(updateProgressRunnable, 1000);
        }
    }

    /**
     * 更新歌词界面
     */
    private void updateLrcView() {
        if (MediaUtil.currentPlayingSong == null) {
            lyric = null;
            return;
        }
        if (lyric == null) {
            getLyric();
        } else {
            startUpdateLrcView();
        }
    }

    /**
     * 开始更新歌词界面
     */
    private void startUpdateLrcView() {
        lrcView.setCurrentMillis(MediaUtil.mediaPlayer.getCurrentPosition());
        lrcView.invalidate();
        if (!MediaUtil.isPlaying) {
            return;
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startUpdateLrcView();
            }
        }, 100);
    }

    /**
     * 获取歌词
     */
    private void getLyric() {
        ThreadUtil.execute(new Runnable() {
            @Override
            public void run() {
                int songID = currentPlayingSong.getISongID();
                if (songID > 0) {
                    String json = HttpUtil.getLyric(songID);
                    Log.e(TAG, "获取歌词 " + json);
                    Message message = handler.obtainMessage();
                    message.what = LYRIC_GOT;
                    message.obj = json;
                    handler.sendMessage(message);
                }else {

                }
            }
        });
    }

    /**
     * 播放模式
     * @param view
     */
    @OnClick(R.id.tvMode)
    public void onTvModeClick(View view){
        PLAY_MODE=(PLAY_MODE++)%3;
        Log.e(TAG, "onTvModeClick:PLAY_MODE: "+PLAY_MODE);
        initTvMode();
    }

    private void initTvMode() {
        switch (PLAY_MODE){
            case 0:
                tvMode.setBackgroundResource(R.mipmap.all_pressed);
                break;
            case 1:
                tvMode.setBackgroundResource(R.mipmap.random_pressed);
                break;
            case 2:
                tvMode.setBackgroundResource(R.mipmap.single_cycle_pressed);
                break;
        }
    }

    /**
     * 暂停/播放按钮事件
     *
     * @param view
     */
    @OnClick(R.id.tvPlayPause)
    public void onTvPlayPauseClick(View view) {

//        MediaUtil.playOrPause(this);
        if (mediaService != null) {
            mediaService.playOrPause(this);
        }
        handler.removeCallbacks(updateProgressRunnable);
    }

    /**
     * 设置暂停、播放按钮背景
     */
    private void setTvPlayPauseBG() {
        if (MediaUtil.mediaPlayer.isPlaying()) {
            tvPlayPause.setBackgroundResource(R.mipmap.pause_big);
        } else {
            tvPlayPause.setBackgroundResource(R.mipmap.play_big);
        }
    }

    /**
     * 播放下一曲
     *
     * @param view
     */
    @OnClick(R.id.tvNext)
    public void onTvNextClick(View view) {
//        MediaUtil.playNext(this);
        if (mediaService != null) {
            mediaService.playNext(this);
        }
        handler.removeCallbacks(updateProgressRunnable);
    }

    /**
     * 播放上一曲
     *
     * @param view
     */
    @OnClick(R.id.tvPrevious)
    public void onTvPreviousClick(View view) {
//        MediaUtil.playPrevious(this);
        if (mediaService != null) {
            mediaService.playPrevious(this);
        }
        handler.removeCallbacks(updateProgressRunnable);
    }

    /**
     * 返回按钮事件
     *
     * @param view
     */
    @OnClick(R.id.tvBack)
    public void onTvBack(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * 歌词变化
     *
     * @param start
     * @param end
     * @param currentMillis
     * @param text
     */
    @Override
    public void onLyricChanged(long start, long end, long currentMillis, String text) {
        if (text == null) {
            return;
        }
        int progress = (int) ((currentMillis - start) * 100f / (end - start));
        if (progress > 0 && progress < 90) {
            textProgressBar.setVisibility(View.VISIBLE);
            textProgressBar.setText(text);
            textProgressBar.setProgress(progress);
        } else {
            textProgressBar.setVisibility(View.GONE);
            textProgressBar.setText("");
            textProgressBar.setProgress(0);
        }
        textProgressBar.invalidate();
    }

    /**
     * 广播接收器
     */
    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "广播接收器onReceive: " + action);
            switch (action){
                case ACTION_PLAYING_SONG_CHANGED:
                    updateLrcView();
                    updatePlayState();
                    break;
                case ACTION_PLAYING_STATE_CHANGED:
                    lyric = null;
                    getLyric();
                    break;
            }

        }
    }


}
