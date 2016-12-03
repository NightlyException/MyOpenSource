package com.nightly.qqmusic.fragment.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.nightly.qqmusic.R;
import com.nightly.qqmusic.adapter.TopsListAdapter;
import com.nightly.qqmusic.model.TopsBean;
import com.nightly.qqmusic.myinterface.Song;
import com.nightly.qqmusic.myinterface.SongPlayerFragment;
import com.nightly.qqmusic.util.HttpUtil;
import com.nightly.qqmusic.util.JsonUtil;
import com.nightly.qqmusic.util.ThreadUtil;
import com.nightly.qqmusic.widget.MyListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;

/**
 * Created by Nightly on 2016/10/9.
 */

public class OnlineFragment extends SongPlayerFragment {
    @Bind(R.id.lvTops)
    MyListView lvTops;
    private View view;
    private final String TAG="Test";
    private TopsListAdapter topsListAdapter;
    private List<HashMap<String, Object>> data = new ArrayList<>();
    String[] array = {
            "23-销量",
            "26-热歌",
            "18-民谣",
            "19-摇滚",
            "5-内地",
            "6-港台",
            "3-欧美",
            "16-韩国",
            "17-日本",
    };
    private final int ONLINE_JSON_GOT = 1;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ONLINE_JSON_GOT:
                    //取出数据
                    Bundle bundle = msg.getData();
                    int order = bundle.getInt("order");
                    String json = bundle.getString("json");
                    String name = bundle.getString("name");
                    //json解析
                    TopsBean topsBean = JsonUtil.parseTopsBean(json);
                    if(topsBean==null){
                        Toast.makeText(getActivity(), "出錯了，稍等一下", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //刷新数据
                    HashMap<String, Object> map = data.get(order);
                    map.put("data", topsBean);
                    //通知adapter刷新界面
                    topsListAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_online, container, false);
            ButterKnife.bind(this, view);
            initData();
            topsListAdapter = new TopsListAdapter(getActivity(), data);
            lvTops.setAdapter(topsListAdapter);

            //因为ScrollView的原因给ListView设置match_content时只能占一个item的高度，
//            // 手动给listView设置“match_content”
//            View view = LayoutInflater.from(getActivity()).inflate(R.layout.item_tops, lvTops, false);
//            int itemHeight = view.getLayoutParams().height;
//            lvTops.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,itemHeight*array.length));
        }
        return view;
    }

    private void initData() {
        //数据初始化
        for (int i = 0; i < array.length; i++) {
            String[] temp = array[i].split("-");
            final String topsID = temp[0];
            String topsName = temp[1] + "榜";
            final HashMap<String, Object> map = new HashMap<>();
            map.put("name", topsName);
            map.put("order", i);
            map.put("data", null);
            data.add(map);
            final int order = i;
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    String json = HttpUtil.getTops(topsID);
                    //通知主线程刷新数据和刷新界面
                    Message message = handler.obtainMessage();
                    message.what = ONLINE_JSON_GOT;
                    Bundle bundle = new Bundle();
                    bundle.putInt("order", order);
                    bundle.putString("json", json);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            });

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    //点击热榜item播放该榜单第一首歌曲
    @OnItemClick(R.id.lvTops)
    public void onLvTopsItemClick(AdapterView<?> parent, View view, int position, long id){
        HashMap<String, Object> map = data.get(position);
        TopsBean topsBean = (TopsBean) map.get("data");
        List<TopsBean.ShowapiResBodyBean.PagebeanBean.SonglistBean> songlist = topsBean.getShowapi_res_body().getPagebean().getSonglist();
        List<Song> songs=new ArrayList<>();
        for (int i = 0; i < songlist.size(); i++) {
            songs.add(songlist.get(i));
        }
//        MediaUtil.play(getActivity(),songs,0);
        if(songPlayer!=null){
            songPlayer.play(songs,0);
        }
    }




    private DataFeeder dataFeeder;

    public void setDataFeeder(DataFeeder dataFeeder) {
        this.dataFeeder = dataFeeder;
    }

    public interface DataFeeder{
        List<HashMap<String,Object>> feedData();
    }

}
