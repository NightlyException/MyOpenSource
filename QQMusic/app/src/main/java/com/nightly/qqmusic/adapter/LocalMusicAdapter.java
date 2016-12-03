package com.nightly.qqmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nightly.qqmusic.R;
import com.nightly.qqmusic.model.LocalMusic;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Nightly on 2016/10/10.
 */

public class LocalMusicAdapter extends BaseAdapter {
    private Context context;
    private List<LocalMusic> localMusics;
    private LayoutInflater inflater;
    private static final String TAG = "Test";

    public LocalMusicAdapter(Context context, List<LocalMusic> localMusics) {
        this.context = context;
        this.localMusics = localMusics;
        inflater = LayoutInflater.from(context);
    }

    public void setLocalMusics(List<LocalMusic> localMusics) {
        this.localMusics = localMusics;
    }

    @Override
    public int getCount() {
        return localMusics == null ? 0 : localMusics.size();
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
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = ((ViewHolder) convertView.getTag());
        }
        LocalMusic localMusic = localMusics.get(position);
        holder.tvName.setText(localMusic.getTitle());
        holder.tvInfo.setText(localMusic.getAlbum() + " " + localMusic.getArtist());


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
