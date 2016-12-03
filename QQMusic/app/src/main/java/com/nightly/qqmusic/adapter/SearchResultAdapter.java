package com.nightly.qqmusic.adapter;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.nightly.qqmusic.Config;
import com.nightly.qqmusic.R;
import com.nightly.qqmusic.model.QueryBean;
import com.nightly.qqmusic.util.HttpUtil;
import com.nightly.qqmusic.util.ThreadUtil;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Nightly on 2016/10/10.
 */

public class SearchResultAdapter extends BaseAdapter {
    private Context context;
    private QueryBean queryBean;
    private LayoutInflater inflater;
    private List<QueryBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean> contentlist;
    private static final String TAG="Test";
    public SearchResultAdapter(Context context, QueryBean queryBean) {
        this.context = context;
        this.queryBean = queryBean;
        inflater = LayoutInflater.from(context);

        if (queryBean != null) {
            contentlist = queryBean.getShowapi_res_body().getPagebean().getContentlist();
        }
    }
    public void setQueryBean(QueryBean queryBean,boolean append) {
        this.queryBean = queryBean;
        if (queryBean != null) {
            if(append){
                contentlist.addAll(queryBean.getShowapi_res_body().getPagebean().getContentlist());
            }else {
                contentlist = queryBean.getShowapi_res_body().getPagebean().getContentlist();
            }
            notifyDataSetChanged();
        }
    }
    @Override
    public int getCount() {
        if (contentlist != null) {
            return contentlist.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_searh_result, parent, false);
            holder=new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder=((ViewHolder) convertView.getTag());
        }
        final QueryBean.ShowapiResBodyBean.PagebeanBean.ContentlistBean song = contentlist.get(position);
        holder.tvName.setText(song.getSongname());
        holder.tvInfo.setText(song.getAlbumname()+" "+ song.getSingername());

        //下载歌曲
        holder.tvPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, song.getSongname()+"开始下载。。。", Toast.LENGTH_SHORT).show();
                final Handler handler = new Handler();
                ThreadUtil.execute(new Runnable() {
                    @Override
                    public void run() {
//                        Log.e(TAG, "歌曲下载路径: "+context.getExternalFilesDir(null).getAbsolutePath());
//                        final boolean success = HttpUtil.downloadMusic(context, song, Config.DOWNLOAD_DIR_PATH, handler);
                        boolean success = HttpUtil.downloadMusic(context, song, Config.DOWNLOAD_DIR_PATH, handler);
                    }
                });
            }
        });

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tvMore)
        TextView tvMore;
        @Bind(R.id.tvPlus)
        TextView tvPlus;
        @Bind(R.id.tvName)
        TextView tvName;
        @Bind(R.id.tvDownloaded)
        TextView tvDownloaded;
        @Bind(R.id.tvInfo)
        TextView tvInfo;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
