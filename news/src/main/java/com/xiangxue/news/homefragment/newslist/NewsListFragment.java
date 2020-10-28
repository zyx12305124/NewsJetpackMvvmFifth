package com.xiangxue.news.homefragment.newslist;

import androidx.annotation.NonNull;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xiangxue.base.customview.BaseMvvmModel;
import com.xiangxue.base.mvvm.model.IBaseModelListener;
import com.xiangxue.base.mvvm.model.PagingResult;
import com.xiangxue.network.TecentNetworkApi;
import com.xiangxue.network.observer.BaseObserver;
import com.xiangxue.news.R;
import com.xiangxue.news.databinding.FragmentNewsBinding;
import com.xiangxue.news.homefragment.api.NewsApiInterface;
import com.xiangxue.news.homefragment.api.NewsListBean;
import com.xiangxue.base.customview.BaseCustomViewModel;
import com.xiangxue.common.views.picturetitleview.PictureTitleViewModel;
import com.xiangxue.common.views.titleview.TitleViewModel;
import com.xiangxue.news.homefragment.headlinenews.NewsChannelModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Allen on 2017/7/20.
 * 保留所有版权，未经允许请不要分享到互联网和其他人
 */
public class NewsListFragment extends Fragment implements IBaseModelListener<List<BaseCustomViewModel>> {
    private NewsListRecyclerViewAdapter mAdapter;
    private FragmentNewsBinding viewDataBinding;
    private NewsListModel mNewsListModel;

    protected final static String BUNDLE_KEY_PARAM_CHANNEL_ID = "bundle_key_param_channel_id";
    protected final static String BUNDLE_KEY_PARAM_CHANNEL_NAME = "bundle_key_param_channel_name";

    public static NewsListFragment newInstance(String channelId, String channelName) {
        NewsListFragment fragment = new NewsListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_PARAM_CHANNEL_ID, channelId);
        bundle.putString(BUNDLE_KEY_PARAM_CHANNEL_NAME, channelName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_news, container, false);
        mAdapter = new NewsListRecyclerViewAdapter();
        viewDataBinding.listview.setHasFixedSize(true);
        viewDataBinding.listview.setLayoutManager(new LinearLayoutManager(getContext()));
        viewDataBinding.listview.setAdapter(mAdapter);
        mNewsListModel = new NewsListModel(getArguments().getString(BUNDLE_KEY_PARAM_CHANNEL_ID),getArguments().getString(BUNDLE_KEY_PARAM_CHANNEL_NAME));
        mNewsListModel.register(this);
        mNewsListModel.refresh();
        viewDataBinding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                mNewsListModel.refresh();
            }
        });
        viewDataBinding.refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                mNewsListModel.loadNextPage();
            }
        });
        return viewDataBinding.getRoot();
    }
    private List<BaseCustomViewModel> viewModels = new ArrayList<>();

    @Override
    public void onLoadSuccess(BaseMvvmModel model, List<BaseCustomViewModel> baseCustomViewModels, PagingResult... results) {
        if(results != null && results.length > 0 && results[0].isFirstPage) {
            viewModels.clear();
        }
        viewModels.addAll(baseCustomViewModels);
        mAdapter.setData(viewModels);
        viewDataBinding.refreshLayout.finishRefresh();
        viewDataBinding.refreshLayout.finishLoadMore();
    }

    @Override
    public void onLoadFail(BaseMvvmModel model,String message, PagingResult... results) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
