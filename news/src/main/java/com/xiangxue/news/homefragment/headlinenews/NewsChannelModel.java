package com.xiangxue.news.homefragment.headlinenews;

import com.xiangxue.base.mvvm.model.IBaseModelListener;
import com.xiangxue.network.TecentNetworkApi;
import com.xiangxue.network.observer.BaseObserver;
import com.xiangxue.news.homefragment.api.NewsApiInterface;
import com.xiangxue.news.homefragment.api.NewsChannelsBean;

import java.util.List;

public class NewsChannelModel {
    IBaseModelListener<List<NewsChannelsBean.ChannelList>> listener;

    public NewsChannelModel(IBaseModelListener listener){
        this.listener = listener;
    }

    public void load(){
        TecentNetworkApi.getService(NewsApiInterface.class)
                .getNewsChannels()
                .compose(TecentNetworkApi.getInstance().applySchedulers(new BaseObserver<NewsChannelsBean>() {
                    @Override
                    public void onSuccess(NewsChannelsBean newsChannelsBean) {
                        listener.onLoadSuccess(newsChannelsBean.showapiResBody.channelList);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        listener.onLoadFail(e.getMessage());
                    }
                }));
    }
}
