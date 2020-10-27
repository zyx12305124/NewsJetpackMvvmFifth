package com.xiangxue.news.homefragment.headlinenews;

import com.xiangxue.base.customview.BaseMvvmModel;
import com.xiangxue.base.mvvm.model.IBaseModelListener;
import com.xiangxue.network.TecentNetworkApi;
import com.xiangxue.network.observer.BaseObserver;
import com.xiangxue.news.homefragment.api.NewsApiInterface;
import com.xiangxue.news.homefragment.api.NewsChannelsBean;

import java.util.List;

public class NewsChannelModel extends BaseMvvmModel {


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
                        iBaseModelListenerWeakReference.get().onLoadSuccess(newsChannelsBean.showapiResBody.channelList);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        iBaseModelListenerWeakReference.get().onLoadFail(e.getMessage());
                    }
                }));
    }
}
