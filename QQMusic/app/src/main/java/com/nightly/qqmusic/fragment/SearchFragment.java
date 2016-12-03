package com.nightly.qqmusic.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nightly.qqmusic.R;
import com.nightly.qqmusic.adapter.SearchResultAdapter;
import com.nightly.qqmusic.model.QueryBean;
import com.nightly.qqmusic.myinterface.Song;
import com.nightly.qqmusic.myinterface.SongPlayerFragment;
import com.nightly.qqmusic.util.HttpUtil;
import com.nightly.qqmusic.util.JsonUtil;
import com.nightly.qqmusic.util.ThreadUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by Nightly on 2016/10/9.
 */

public class SearchFragment extends SongPlayerFragment {

    @Bind(R.id.tvBack)
    TextView tvBack;
    @Bind(R.id.edInput)
    EditText edInput;
    @Bind(R.id.tvSearch)
    TextView tvSearch;
    @Bind(R.id.lvRecent)
    ListView lvRecent;
    @Bind(R.id.lvResult)
    ListView lvResult;
    private View view;
    private QueryBean queryBean = null;
    private final int QUERY_JSON_GOT = 1;
    private SearchResultAdapter searchResultAdapter;
    private final String TAG = "Test";
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case QUERY_JSON_GOT:
                    //解析json
                    String json = (String) msg.obj;
                    queryBean = JsonUtil.parseQueryBean(json);
                    if (queryBean != null) {
                        searchResultAdapter.setQueryBean(queryBean, msg.arg1 > 0 ? true : false);
                        Toast.makeText(getActivity(), "加载完毕。", Toast.LENGTH_SHORT).show();
                        if (msg.arg1 > 0 && footerView != null) {
                            lvResult.removeFooterView(footerView);
                        }
                    } else {
                        Toast.makeText(getActivity(), "数据解析失败", Toast.LENGTH_SHORT);
                    }
                    break;
            }
        }
    };
    private View footerView;
    private int currentPage = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_search, container, false);
            ButterKnife.bind(this, view);
        }
        searchResultAdapter = new SearchResultAdapter(getActivity(), queryBean);
        lvResult.setAdapter(searchResultAdapter);

        //最后一个item可见且滚动停止，开始查询下一页数据
        footerView = LayoutInflater.from(getActivity()).inflate(R.layout.footer_search_result, lvResult, false);
        lvResult.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int lastVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (queryBean != null && queryBean.getShowapi_res_body().getPagebean() != null && searchResultAdapter.getCount() - 1 == lastVisibleItem) {
                        //显示footerView
                        lvResult.addFooterView(footerView);
                        doSearch(++currentPage, true);
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lastVisibleItem = firstVisibleItem + visibleItemCount - 1;
            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    /**
     * 搜索列表点击播放歌曲事件
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @OnItemClick(R.id.lvResult)
    public void onLvResultItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            List<QueryBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> contentlist = queryBean.getShowapi_res_body().getPagebean().getContentlist();
            List<Song> songs = new ArrayList<>();
            for (int i = 0; i < contentlist.size(); i++) {
                songs.add(contentlist.get(i));
            }
//            MediaUtil.play(getContext(),songs,position);
            if (songPlayer != null) {
                songPlayer.play(songs, position);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "播放失败。", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 搜索事件
     *
     * @param view
     */
    @OnClick(R.id.tvSearch)
    public void onTvSearchClick(View view) {
        doSearch(1, false);
    }

    /**
     * 搜索
     *
     * @param page
     * @param append
     */
    private void doSearch(final int page, final boolean append) {
        final String keyword = edInput.getText().toString();
        if (!"".equals(keyword.trim()) || keyword != null) {
            ThreadUtil.execute(new Runnable() {
                @Override
                public void run() {
                    //请求数据
                    String queryJson;
                    if (append) {
                        queryJson = HttpUtil.query(keyword, page);
                    } else {
                        queryJson = HttpUtil.query(keyword, 1);
                    }
                    Message message = Message.obtain();
                    //发消息给handler
                    message.what = QUERY_JSON_GOT;
                    message.obj = queryJson;
                    if (append) {
                        message.arg1 = 1;
                    } else {
                        message.arg1 = -1;
                    }
                    handler.sendMessage(message);
                }
            });
        } else {
            Toast.makeText(getActivity(), "请输入搜索内容。", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * 点击返回
     *
     * @param view
     */
    @OnClick(R.id.tvBack)
    public void onTvBackClick(View view) {
        if (listener != null) {
            listener.onSearchFragmentClick();
        }
    }

    onSearchFragmentClickListener listener;

    public void setListener(onSearchFragmentClickListener listener) {
        this.listener = listener;
    }

    public interface onSearchFragmentClickListener {
        void onSearchFragmentClick();
    }
}
