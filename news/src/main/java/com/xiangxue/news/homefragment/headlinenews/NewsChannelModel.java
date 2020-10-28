package com.xiangxue.news.homefragment.headlinenews;

import com.xiangxue.base.mvvm.model.BaseMvvmModel;
import com.xiangxue.network.TecentNetworkApi;
import com.xiangxue.network.observer.BaseObserver;
import com.xiangxue.news.homefragment.api.NewsApiInterface;
import com.xiangxue.news.homefragment.api.NewsChannelsBean;

import java.util.List;

//要做反序列话，所以要知道原始的数据类型 所以泛型中加入NewsChannelsBean
public class NewsChannelModel extends BaseMvvmModel<NewsChannelsBean, List<NewsChannelsBean.ChannelList>> {

    public NewsChannelModel() {
        super(false,"NEWS_CHANNEL_PREF_KEY");
    }

        @Override
    public void load(){
        TecentNetworkApi.getService(NewsApiInterface.class)
                .getNewsChannels()
                .compose(TecentNetworkApi.getInstance().applySchedulers(new BaseObserver<NewsChannelsBean>() {
                    @Override
                    public void onSuccess(NewsChannelsBean newsChannelsBean) {
                        notifyResultToListener(newsChannelsBean,newsChannelsBean.showapiResBody.channelList);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        loadFail(e.getMessage());
                    }
                }));
    }
}
