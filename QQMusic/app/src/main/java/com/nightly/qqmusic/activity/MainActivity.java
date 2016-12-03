package com.nightly.qqmusic.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nightly.qqmusic.R;
import com.nightly.qqmusic.fragment.MainFragment;
import com.nightly.qqmusic.fragment.MoreFragment;
import com.nightly.qqmusic.fragment.SearchFragment;
import com.nightly.qqmusic.myinterface.Song;
import com.nightly.qqmusic.myinterface.SongPlayer;
import com.nightly.qqmusic.myinterface.SongPlayerActivity;
import com.nightly.qqmusic.util.MediaUtil;
import com.nightly.qqmusic.widget.CircleProgress;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends SongPlayerActivity implements MainFragment.onMainFragmentClickListener,
        MoreFragment.onMoreFragmentClickListener,
        SearchFragment.onSearchFragmentClickListener,
        SongPlayer {

    private static final String TAG = "Test";
    @Bind(R.id.flContainer)
    FrameLayout flContainer;
    @Bind(R.id.sdvCover)
    SimpleDraweeView sdvCover;
    @Bind(R.id.tvSongName)
    TextView tvSongName;
    @Bind(R.id.tvSingerAlbum)
    TextView tvSingerAlbum;
    @Bind(R.id.tvPlayPause)
    CircleProgress tvPlayPause;
    @Bind(R.id.tvList)
    TextView tvList;
    @Bind(R.id.llPlaying)
    LinearLayout llPlaying;
    private Fragment currentFragment;
    private MainFragment mainFragment;
    private MoreFragment moreFragment;
    private SearchFragment searchFragment;
    private int currentSongID;//当前播放的歌曲ID
    private MyReceiver myReceiver;
    private IntentFilter intentFilter;
    private ObjectAnimator rotationAnimator;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (handler == null) {
            handler = new Handler();
        }
        initFragment();
        initRotationAnim();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //默认展示第一屏
    private void initFragment() {
        mainFragment = new MainFragment();
        mainFragment.setListener(this);
        currentFragment = mainFragment;
        showFragment(mainFragment, false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //动态注册广播
        myReceiver = new MyReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(MediaUtil.ACTION_PLAYING_STATE_CHANGED);
        registerReceiver(myReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //注销广播
        unregisterReceiver(myReceiver);
    }

    //点击底部播放状态栏启动PlayActivity
    @OnClick(R.id.llPlaying)
    public void onLlPlayingClick(View view) {
        Intent intent = new Intent(this, PlayingActivity.class);
        intent.putExtra("currentSongID", currentSongID);
        startActivity(intent);
    }

    //播放暂停按钮事件
    @OnClick(R.id.tvPlayPause)
    public void onClick(View view) {
        if (mediaService != null) {
            mediaService.playOrPause(this);
        }
//
//        if(MediaUtil.mediaPlayer.isPlaying()){
//            tvPlayPause.setBackgroundResource(R.mipmap.pause_big);
//        }else {
//            tvPlayPause.setBackgroundResource(R.mipmap.play_big);
//        }
    }

    /**
     * fragment切换
     *
     * @param fragment
     * @param isUseAnim
     */
    private void showFragment(Fragment fragment, boolean isUseAnim) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (!fragment.isAdded()) {
            fragmentTransaction.add(R.id.flContainer, fragment);
        }
        if (isUseAnim) {
            if (fragment instanceof MainFragment) {
                fragmentTransaction.setCustomAnimations(R.anim.enter_rightward, R.anim.exit_rightward);
                mainFragment.vMask.setVisibility(View.GONE);
            } else {
                fragmentTransaction.setCustomAnimations(R.anim.enter_leftward, R.anim.exit_leftward);
                mainFragment.vMask.setVisibility(View.VISIBLE);
            }
        }
        fragmentTransaction.hide(currentFragment).show(fragment);
        fragmentTransaction.commit();
        currentFragment = fragment;
    }


    @Override
    public void onMainFragmentTvMenuClick() {
        if (moreFragment == null) {
            moreFragment = new MoreFragment();
            moreFragment.setListener(this);
        }
        showFragment(moreFragment, true);
    }

    @Override
    public void onMainFragmentTvSearchClick() {
        if (searchFragment == null) {
            searchFragment = new SearchFragment();
            searchFragment.setListener(this);
            searchFragment.setSongPlayer(this);
        }
        showFragment(searchFragment, true);
    }

    @Override
    public void onMoreFragmentTvBackClick() {
        showFragment(mainFragment, true);
    }

    @Override
    public void onSearchFragmentClick() {
        showFragment(mainFragment, true);
    }


    /**
     * 专辑图片旋转效果
     */
    private void initRotationAnim() {
        rotationAnimator = ObjectAnimator.ofFloat(sdvCover, "rotation", 0, 360 * 10000);
        rotationAnimator.setDuration(3000 * 10000);
        rotationAnimator.setRepeatCount(ValueAnimator.INFINITE);
        rotationAnimator.setInterpolator(new LinearInterpolator());
    }

    /**
     * 播放歌曲
     *
     * @param songs
     * @param position
     */
    @Override
    public void play(List<Song> songs, final int position) {
        if (mediaService != null) {
            mediaService.play(this, songs, position);
        }

    }

    private void updatePlayState() {
        int currentPosition = MediaUtil.mediaPlayer.getCurrentPosition();
        int duration = MediaUtil.mediaPlayer.getDuration();
        tvPlayPause.setCurrentMills(currentPosition);
        tvPlayPause.setDuration(duration);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                updatePlayState();
            }
        }, 100);
    }

    /**
     * 广播接收器
     */
    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "MainActivity,onReceive: 广播接收器:" + action);
            switch (action) {
                case MediaUtil.ACTION_PLAYING_STATE_CHANGED:
                    updatePlayState();
                    try {
                        Song currentPlayingSong = MediaUtil.currentPlayingSong;
                        tvSongName.setText(currentPlayingSong.getISongName());
                        tvSingerAlbum.setText(currentPlayingSong.getISingerName());
                        String imgUrl = currentPlayingSong.getIAlbumpicSmall();
                        if (imgUrl != null) {
                            sdvCover.setImageURI(Uri.parse(imgUrl));
                        }
                        if (MediaUtil.mediaPlayer.isPlaying()) {
                            rotationAnimator.start();
                            tvPlayPause.setBackgroundResource(R.mipmap.pause_big);
                        } else {
                            rotationAnimator.cancel();
                            tvPlayPause.setBackgroundResource(R.mipmap.play_big);
//                        Log.e(TAG, "目前API: "+Build.VERSION.SDK_INT);
//                        if(Build.VERSION.SDK_INT>19){
//                            rotationAnimator.pause();
//                        }else {
//                            rotationAnimator.cancel();
//                        }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case MediaUtil.ACTION_PLAYING_SONG_CHANGED:

                    break;
            }
        }
    }
}
