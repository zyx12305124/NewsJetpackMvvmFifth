package com.xiangxue.news.homefragment.newslist;

import com.xiangxue.base.customview.BaseCustomViewModel;
import com.xiangxue.base.customview.BaseMvvmModel;
import com.xiangxue.base.mvvm.model.IBaseModelListener;
import com.xiangxue.base.mvvm.model.PagingResult;
import com.xiangxue.common.views.picturetitleview.PictureTitleViewModel;
import com.xiangxue.common.views.titleview.TitleViewModel;
import com.xiangxue.network.TecentNetworkApi;
import com.xiangxue.network.observer.BaseObserver;
import com.xiangxue.news.homefragment.api.NewsApiInterface;
import com.xiangxue.news.homefragment.api.NewsListBean;

import java.util.ArrayList;
import java.util.List;

public class NewsListModel  extends BaseMvvmModel {
    private String mChannelId;
    private String mChannelName;

    public NewsListModel( String channelId, String channelName){
        super(true,1);
        mChannelId = channelId;
        mChannelName = channelName;
    }

    @Override
    public void load() {
        TecentNetworkApi.getService(NewsApiInterface.class)
                .getNewsList(mChannelId,
                        mChannelName, String.valueOf(mPage))
                .compose(TecentNetworkApi.getInstance().applySchedulers(new BaseObserver<NewsListBean>() {
                    @Override
                    public void onSuccess(NewsListBean newsChannelsBean) {
                        List<BaseCustomViewModel> viewModels = new ArrayList<>();
                        for(NewsListBean.Contentlist contentlist:newsChannelsBean.showapiResBody.pagebean.contentlist){
                            if(contentlist.imageurls != null && contentlist.imageurls.size() > 0){
                                PictureTitleViewModel pictureTitleViewModel = new PictureTitleViewModel();
                                pictureTitleViewModel.pictureUrl = contentlist.imageurls.get(0).url;
                                pictureTitleViewModel.jumpUri = contentlist.link;
                                pictureTitleViewModel.title = contentlist.title;
                                viewModels.add(pictureTitleViewModel);
                            } else {
                                TitleViewModel titleViewModel = new TitleViewModel();
                                titleViewModel.jumpUri = contentlist.link;
                                titleViewModel.title = contentlist.title;
                                viewModels.add(titleViewModel);
                            }
                        }
                        iBaseModelListenerWeakReference.get().onLoadSuccess(viewModels, new PagingResult(mPage == 1, viewModels.isEmpty(), viewModels.size() >= 10));
                        mPage ++;
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        e.printStackTrace();
                    }
                }));
    }
}
