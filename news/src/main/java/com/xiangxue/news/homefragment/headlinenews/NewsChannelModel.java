package com.xiangxue.news.homefragment.headlinenews;

import com.xiangxue.base.customview.BaseMvvmModel;
import com.xiangxue.network.TecentNetworkApi;
import com.xiangxue.network.observer.BaseObserver;
import com.xiangxue.news.homefragment.api.NewsApiInterface;
import com.xiangxue.news.homefragment.api.NewsChannelsBean;

import java.util.List;

public class NewsChannelModel extends BaseMvvmModel<List<NewsChannelsBean.ChannelList>> {


    public NewsChannelModel() {
        super(false);
    }

        @Override
    public void load(){
        TecentNetworkApi.getService(NewsApiInterface.class)
                .getNewsChannels()
                .compose(TecentNetworkApi.getInstance().applySchedulers(new BaseObserver<NewsChannelsBean>() {
                    @Override
                    public void onSuccess(NewsChannelsBean newsChannelsBean) {
                        notifyResultToListener(newsChannelsBean.showapiResBody.channelList);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        loadFail(e.getMessage());
                    }
                }));
    }
}
