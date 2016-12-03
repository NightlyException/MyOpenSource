package com.nightly.qqmusic.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nightly.qqmusic.R;

/**
 * Created by Nightly on 2016/10/9.
 */

public class FindFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(view==null) {
            view = inflater.inflate(R.layout.fragment_find, container, false);
        }
        return view;
    }
}
