package com.xiangxue.news.homefragment.headlinenews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.android.material.tabs.TabLayout;
import com.xiangxue.base.mvvm.model.BaseMvvmModel;
import com.xiangxue.base.mvvm.model.IBaseModelListener;
import com.xiangxue.base.mvvm.model.PagingResult;
import com.xiangxue.news.R;
import com.xiangxue.news.databinding.FragmentHomeBinding;
import com.xiangxue.news.homefragment.api.NewsChannelsBean;

import java.util.List;

//这里不需要实现这个接口了，不是它的职责
//public class HeadlineNewsFragment extends Fragment implements IBaseModelListener<List<NewsChannelsBean.ChannelList>> {
public class HeadlineNewsFragment extends Fragment  {
    public HeadlineNewsFragmentAdapter adapter;
    FragmentHomeBinding viewDataBinding;
//    private NewsChannelModel mNewsChannelModel;
    private HeadlineNewsViewModel viewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        adapter = new HeadlineNewsFragmentAdapter(getChildFragmentManager());
        viewDataBinding.tablayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        viewDataBinding.viewpager.setAdapter(adapter);
        viewDataBinding.tablayout.setupWithViewPager(viewDataBinding.viewpager);
        viewDataBinding.viewpager.setOffscreenPageLimit(1);
//        mNewsChannelModel = new NewsChannelModel();
//        mNewsChannelModel.register(this);
//        mNewsChannelModel.load();
//        mNewsChannelModel.getCachedDataAndLoad();
        viewModel = new HeadlineNewsViewModel();
        viewModel.dataList.observe(this, new Observer<List<NewsChannelsBean.ChannelList>>() {
            @Override
            public void onChanged(List<NewsChannelsBean.ChannelList> channelLists) {
                adapter.setChannels(channelLists);
            }
        });
        return viewDataBinding.getRoot();
    }
//
//    @Override
//    public void onLoadSuccess(BaseMvvmModel model, List<NewsChannelsBean.ChannelList> channelLists, PagingResult... results) {
//        if(adapter != null) {
//            adapter.setChannels(channelLists);
//        }
//    }
//
//    @Override
//    public void onLoadFail(BaseMvvmModel model,String message, PagingResult... results) {
//        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
//    }
}
