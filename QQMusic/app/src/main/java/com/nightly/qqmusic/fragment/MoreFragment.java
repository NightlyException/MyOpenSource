package com.nightly.qqmusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nightly.qqmusic.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Nightly on 2016/10/9.
 */

public class MoreFragment extends Fragment {
    @Bind(R.id.tvBack)
    TextView tvBack;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_more, container, false);
            ButterKnife.bind(this, view);
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
    @OnClick(R.id.tvBack)
    public void onTvBackClick(View view){
        if(listener !=null){
            listener.onMoreFragmentTvBackClick();
        }
    }
    onMoreFragmentClickListener listener;

    public void setListener(onMoreFragmentClickListener listener) {
        this.listener = listener;
    }

    public interface onMoreFragmentClickListener {
        void onMoreFragmentTvBackClick();
    }
}
