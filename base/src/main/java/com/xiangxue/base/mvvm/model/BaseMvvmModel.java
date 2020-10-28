package com.xiangxue.base.mvvm.model;

import com.google.gson.Gson;
import com.xiangxue.base.mvvm.model.IBaseModelListener;
import com.xiangxue.base.mvvm.model.PagingResult;
import com.xiangxue.base.preference.BasicDataPreferenceUtil;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * NewsListModel
 * NewsChannelModel
 * 把这俩抽离出BaseMvvmModel
 * 1.首先把listener提取到BaseMvvmModel,这个listener是一个弱引用
 * 2.mPage提取到BaseMvvmModel
 * 3.refresh ,load和 loadNextPage提取到BaseMvvmModel
 * 4.处理重复点击刷新，多次加载下一页。isLoading判断是否正在加载数据，如果正在加载数据，则不会发出请求
 * 5.把结果的通知，放到BaseMvvmModel 。定义两个函数
 */
public abstract class BaseMvvmModel<NETWORK_DATA,RESULT_DATA> {
    protected WeakReference<IBaseModelListener> iBaseModelListenerWeakReference;

    protected int mPage = 1;
    private boolean isNeedPaging;//判断是否需要分页
    private final int INIT_PAGE_NUMBER;

    //用来判断在加载的过程当中，其他重复来了请求，网络数据还没回来的话，是不发请求出去的
    private boolean isLoading;
    private String cachePreferenceKey;

    public BaseMvvmModel(boolean isNeedPaging, String cachedPreferenceKey,int ... initPageNo) {
        this.isNeedPaging = isNeedPaging;
        if (isNeedPaging && initPageNo!=null & initPageNo.length>0)
            INIT_PAGE_NUMBER = initPageNo[0];
        else
            INIT_PAGE_NUMBER = -1;

        this.cachePreferenceKey = cachedPreferenceKey;
    }

    public void register(IBaseModelListener listener){
        if (listener != null)
            iBaseModelListenerWeakReference = new WeakReference<>(listener);
    }

    public void refresh(){
        //在这里可以去抛一个异常
        //need to throw if register is not called
        if (!isLoading) {
            if (isNeedPaging) {
                mPage = INIT_PAGE_NUMBER;
            }
            isLoading = true;
            load();
        }
    }

    public abstract void load();

    public void loadNextPage() {
        //在这里可以去抛一个异常
        //need to throw if register is not called
        if (!isLoading){
            isLoading = true;
            load();
        }
    }

    protected void notifyResultToListener(NETWORK_DATA networkData,RESULT_DATA resultData){
        IBaseModelListener listener = iBaseModelListenerWeakReference.get();
        if (listener!=null){
            //notify
            if(isNeedPaging){
                listener.onLoadSuccess(this,resultData,new PagingResult(
                        mPage==INIT_PAGE_NUMBER,
                        resultData==null?true:((List)resultData).isEmpty(),//强转成List判断是否为空
                        ((List)resultData).size()>0));//先这样判断
            } else {
                listener.onLoadSuccess(this,resultData);
            }

            // save resultData to preference
            if(isNeedPaging) {
                if(cachePreferenceKey != null && mPage == INIT_PAGE_NUMBER){//只需要存第一页
                    //这里不能够直接存结果的数据resultData,因为从缓存中取出的时候是要反序列化的
                    //存的时候存的是base类型的，存resultData反序列化的话反序列号不了
                    //所以必须存网络的数据networkData
                    saveDataToPreference(networkData);
                }
            } else {
                if(cachePreferenceKey != null){
                    saveDataToPreference(networkData);
                }
            } //到这里，数据就已经存到了preference

            //update pageNumber
            if (isNeedPaging){
                if (resultData != null &&((List)resultData).size()>0){
                    mPage ++;
                }
            }

        }
        isLoading = false;//网络成功后要把loading置为false

    }

    protected void loadFail(final String errorMessage){
        IBaseModelListener listener = iBaseModelListenerWeakReference.get();
        if (listener!=null){
            if(isNeedPaging){
                listener.onLoadFail(this,errorMessage,
                        new PagingResult(
                        mPage==INIT_PAGE_NUMBER,
                        true,//强转成List判断是否为空
                        false));
            } else {
                listener.onLoadFail(this,errorMessage);
            }
        }
        isLoading = false;//网络成功后要把loading置为false
    }

    protected void saveDataToPreference(NETWORK_DATA data) {
        if(data != null) {
            BaseCachedData<NETWORK_DATA> cachedData = new BaseCachedData<>();
            cachedData.data = data;
            cachedData.updateTimeInMillis = System.currentTimeMillis();
            BasicDataPreferenceUtil.getInstance().setString(cachePreferenceKey, new Gson().toJson(cachedData));
        }
    }
}
