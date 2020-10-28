package com.xiangxue.base.mvvm.model;

import com.google.gson.Gson;
import com.xiangxue.base.mvvm.model.IBaseModelListener;
import com.xiangxue.base.mvvm.model.PagingResult;
import com.xiangxue.base.preference.BasicDataPreferenceUtil;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

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

/**
 * model的数据来源一个是网络 一个是sharePrefernece
 * 加上了缓存后的逻辑就是
 * 第一步先去创建model
 * 第二步model里面会去读缓存，缓存因为是在本地，所以很快就读取到了，读取到之后马上通知页面进行数据更新
 * 第三步同时也会根据需要发出网络请求 ，根据网络请求的结果刷新页面的数据
 */
public abstract class BaseMvvmModel<NETWORK_DATA,RESULT_DATA> implements MvvmDataObserver<NETWORK_DATA>{
    private CompositeDisposable compositeDisposable;


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


    //要判断这个数据需不需要缓存，如果是来自缓存的数据，是不需要再一次存到缓存里的
    //必须是来自网络的数据才能缓存，所以这里加一个值来判断
    protected void notifyResultToListener(NETWORK_DATA networkData,RESULT_DATA resultData,boolean isFromCache){
        IBaseModelListener listener = iBaseModelListenerWeakReference.get();
        if (listener!=null){
            //notify
            if(isNeedPaging){
                listener.onLoadSuccess(this,resultData,new PagingResult(
                        mPage==INIT_PAGE_NUMBER,
                        resultData==null?true:((List)resultData).isEmpty(),//强转成List判断是否为空
                        ((List)resultData).size()>0));//先这样判断
            } else {
                listener.onLoadSuccess(this,resultData);//通知view
            }

            // save resultData to preference
            if(isNeedPaging) {
                if(cachePreferenceKey != null && mPage == INIT_PAGE_NUMBER && !isFromCache){//只需要存第一页
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
            if (isNeedPaging && !isFromCache){
                if (resultData != null &&((List)resultData).size()>0){
                    mPage ++;
                }
            }

        }

        //这里也要判断是不是来自缓存，因为发网络请求之前会读缓存，读完了缓存之后网络数据可能还没回来
        if (!isFromCache){
            isLoading = false;//网络成功后要把loading置为false
        }

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

    /**
     * 当页面取消的时候会用viewmodel,viewmodel clear的时候 我们会自动把网络请求取消掉
     */
    public void cancel() {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    public void addDisposable(Disposable d) {
        if (d == null) {
            return;
        }

        if (compositeDisposable == null) {
            compositeDisposable = new CompositeDisposable();
        }

        compositeDisposable.add(d);
    }
}
