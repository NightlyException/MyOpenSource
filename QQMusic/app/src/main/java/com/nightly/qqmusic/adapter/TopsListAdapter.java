package com.nightly.qqmusic.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.nightly.qqmusic.R;
import com.nightly.qqmusic.model.TopsBean;

import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Nightly on 2016/10/10.
 */

public class TopsListAdapter extends BaseAdapter {
    private List<HashMap<String, Object>> data;
    private Context context;
    private LayoutInflater inflater;

    public TopsListAdapter(Context context, List<HashMap<String, Object>> data) {
        this.data = data;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
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
            convertView = inflater.inflate(R.layout.item_tops, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = ((ViewHolder) convertView.getTag());
        }
        HashMap<String, Object> map = data.get(position);
        String topsName = (String) map.get("name");
        holder.tvName.setText(topsName);
        TopsBean topsBean = (TopsBean) map.get("data");
        if (topsBean == null) {
            return convertView;
        }
        TopsBean.ShowapiResBodyBean.PagebeanBean.SonglistBean firstSong = topsBean.getShowapi_res_body().getPagebean().getSonglist().get(0);
        TopsBean.ShowapiResBodyBean.PagebeanBean.SonglistBean seondSong = topsBean.getShowapi_res_body().getPagebean().getSonglist().get(1);
        TopsBean.ShowapiResBodyBean.PagebeanBean.SonglistBean thirdSong = topsBean.getShowapi_res_body().getPagebean().getSonglist().get(2);

        TextView tvSingleIfoI = (TextView) holder.singleI.findViewById(R.id.tvSingleInfo);
        TextView tvSingleIfoII = (TextView) holder.singleII.findViewById(R.id.tvSingleInfo);
        TextView tvSingleIfoIII = (TextView) holder.singleIII.findViewById(R.id.tvSingleInfo);
        tvSingleIfoI.setText("1 " + firstSong.getSongname() + "-" + firstSong.getSingername());
        tvSingleIfoII.setText("2 " + seondSong.getSongname() + "-" + seondSong.getSingername());
        tvSingleIfoIII.setText("3 " + thirdSong.getSongname() + "-" + thirdSong.getSingername());

        TextView firstArrow = (TextView) holder.singleI.findViewById(R.id.arrow);
        TextView secondArrow = (TextView) holder.singleII.findViewById(R.id.arrow);
        TextView thirdArrow = (TextView) holder.singleIII.findViewById(R.id.arrow);
        firstArrow.setVisibility(View.GONE);
        secondArrow.setVisibility(View.VISIBLE);
        thirdArrow.setVisibility(View.GONE);

        //设置专辑图片
        String imgUrl = firstSong.getAlbumpic_big();
        holder.sdvCover.setImageURI(Uri.parse(imgUrl));
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.sdvCover)
        SimpleDraweeView sdvCover;
        @Bind(R.id.tvName)
        TextView tvName;
        @Bind(R.id.singleI)
        View singleI;
        @Bind(R.id.singleII)
        View singleII;
        @Bind(R.id.singleIII)
        View singleIII;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }


}
