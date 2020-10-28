package com.xiangxue.news.homefragment.headlinenews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.xiangxue.base.customview.BaseMvvmModel;
import com.xiangxue.base.mvvm.model.IBaseModelListener;
import com.xiangxue.base.mvvm.model.PagingResult;
import com.xiangxue.news.R;
import com.xiangxue.news.databinding.FragmentHomeBinding;
import com.xiangxue.news.homefragment.api.NewsChannelsBean;

import java.util.List;

public class HeadlineNewsFragment extends Fragment implements IBaseModelListener<List<NewsChannelsBean.ChannelList>> {
    public HeadlineNewsFragmentAdapter adapter;
    FragmentHomeBinding viewDataBinding;
    private NewsChannelModel mNewsChannelModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        adapter = new HeadlineNewsFragmentAdapter(getChildFragmentManager());
        viewDataBinding.tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewDataBinding.viewpager.setAdapter(adapter);
        viewDataBinding.tablayout.setupWithViewPager(viewDataBinding.viewpager);
        viewDataBinding.viewpager.setOffscreenPageLimit(1);
        mNewsChannelModel = new NewsChannelModel();
        mNewsChannelModel.register(this);
        mNewsChannelModel.load();
        return viewDataBinding.getRoot();
    }

    @Override
    public void onLoadSuccess(BaseMvvmModel model, List<NewsChannelsBean.ChannelList> channelLists, PagingResult... results) {
        if(adapter != null) {
            adapter.setChannels(channelLists);
        }
    }

    @Override
    public void onLoadFail(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
