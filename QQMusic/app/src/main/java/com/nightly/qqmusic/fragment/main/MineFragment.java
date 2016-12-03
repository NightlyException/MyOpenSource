package com.nightly.qqmusic.fragment.main;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.nightly.qqmusic.Config;
import com.nightly.qqmusic.R;
import com.nightly.qqmusic.adapter.LocalMusicAdapter;
import com.nightly.qqmusic.model.LocalMusic;
import com.nightly.qqmusic.myinterface.Song;
import com.nightly.qqmusic.myinterface.SongPlayerFragment;
import com.nightly.qqmusic.util.MediaUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by Nightly on 2016/10/9.
 */

public class MineFragment extends SongPlayerFragment {
    @Bind(R.id.lvLocalMusic)
    ListView lvLocalMusic;
    private View view;
    private List<LocalMusic> localMusics=new ArrayList<>();
    private LocalMusicAdapter adapter;
    private final String TAG="Test";
    private PtrFrameLayout ptrFrameLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "搜索路径 "+ Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath());
        getData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_mine, container, false);
            ButterKnife.bind(this, view);
            adapter = new LocalMusicAdapter(getActivity(),localMusics);
            lvLocalMusic.setAdapter(adapter);
            /**
             * 下拉刷新设置
             */
            initPtrFrameLayout();
        }
        return view;
    }

    private void initPtrFrameLayout() {
        ptrFrameLayout = ((PtrFrameLayout) view.findViewById(R.id.ptrFrameLayout));
        PtrClassicDefaultHeader header = new PtrClassicDefaultHeader(getActivity());
        ptrFrameLayout.addPtrUIHandler(header);
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                doRefresh();
            }
        });
    }

    private void doRefresh() {
        Toast.makeText(getActivity(), "开始刷新...",Toast.LENGTH_SHORT).show();
        getData();
        ptrFrameLayout.refreshComplete();
        Toast.makeText(getActivity(), "数据已刷新...", Toast.LENGTH_SHORT).show();
    }

    private void getData() {
        List<LocalMusic> list = MediaUtil.getLocalMusic(getActivity(), Config.DOWNLOAD_DIR_PATH);
        Log.e(TAG, "getData: "+list);
        localMusics.clear();
        localMusics.addAll(list);
        if(adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    //点击列表播放歌曲
    @OnItemClick(R.id.lvLocalMusic)
    public void onLvLocalMusicItemClick(AdapterView<?> parent, View view, final int position, long id) {
        List<Song> songs=new ArrayList<>();
        for (int i = 0; i < localMusics.size(); i++) {
            songs.add(localMusics.get(i));
        }
//        MediaUtil.play(getActivity(),songs,position);
        if(songPlayer!=null){
            Log.e(TAG, "onLvLocalMusicItemClick: 歌曲列表位置："+position);
            songPlayer.play(songs,position);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
