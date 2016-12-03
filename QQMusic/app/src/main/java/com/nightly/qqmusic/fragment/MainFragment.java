package com.nightly.qqmusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nightly.qqmusic.R;
import com.nightly.qqmusic.adapter.MyFragmentAdapter;
import com.nightly.qqmusic.fragment.main.FindFragment;
import com.nightly.qqmusic.fragment.main.MineFragment;
import com.nightly.qqmusic.fragment.main.OnlineFragment;
import com.nightly.qqmusic.myinterface.SongPlayer;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnPageChange;

/**
 * Created by Nightly on 2016/10/9.
 */

public class MainFragment extends Fragment {
    @Bind(R.id.tvMenu)
    TextView tvMenu;
    @Bind(R.id.tvSearch)
    TextView tvSearch;
    @Bind(R.id.rbMine)
    RadioButton rbMine;
    @Bind(R.id.rbOnline)
    RadioButton rbOnline;
    @Bind(R.id.rbFind)
    RadioButton rbFind;
    @Bind(R.id.rg)
    RadioGroup rg;
    @Bind(R.id.vpFragment)
    ViewPager vpFragment;
    @Bind(R.id.vMask)
    public View vMask;
    private View view;
    private MineFragment mineFragment;
    private OnlineFragment onlineFragment;
    private FindFragment findFragment;
    private List<Fragment> fragments = new ArrayList<>();
    private MyFragmentAdapter vpAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_main, container, false);
            ButterKnife.bind(this, view);
        }
        mineFragment = new MineFragment();
        mineFragment.setSongPlayer(((SongPlayer) getActivity()));
        onlineFragment = new OnlineFragment();
        onlineFragment.setSongPlayer(((SongPlayer) getActivity()));
        findFragment = new FindFragment();
        fragments.add(mineFragment);
        fragments.add(onlineFragment);
        fragments.add(findFragment);
        vpAdapter = new MyFragmentAdapter(getChildFragmentManager(), fragments);
        vpFragment.setAdapter(vpAdapter);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbMine:
                        vpFragment.setCurrentItem(0);
                        break;
                    case R.id.rbOnline:
                        vpFragment.setCurrentItem(1);
                        break;
                    case R.id.rbFind:
                        vpFragment.setCurrentItem(2);
                        break;
                }
            }
        });
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnPageChange(R.id.vpFragment)
    public void onVpPageSelected(int position) {
        for (int i = 0; i < rg.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) rg.getChildAt(i);
            if (i == position) {
                radioButton.setChecked(true);
                radioButton.setTextAppearance(getContext(), R.style.MainTab_Checked);
            } else {
                radioButton.setChecked(false);
                radioButton.setTextAppearance(getContext(), R.style.MainTab);
            }
        }
    }

    @OnClick(R.id.tvMenu)
    public void tvMainMenuClick(View view) {
        if (listener != null) {
            listener.onMainFragmentTvMenuClick();
        }
    }

    @OnClick(R.id.tvSearch)
    public void tvMainSearchClick(View view) {
        if (listener != null) {
            listener.onMainFragmentTvSearchClick();
        }
    }

    public void setListener(onMainFragmentClickListener listener) {
        this.listener = listener;
    }

    onMainFragmentClickListener listener;

    public interface onMainFragmentClickListener {
        void onMainFragmentTvMenuClick();

        void onMainFragmentTvSearchClick();
    }
}
