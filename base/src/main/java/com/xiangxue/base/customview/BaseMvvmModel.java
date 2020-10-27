package com.xiangxue.base.customview;

import com.xiangxue.base.mvvm.model.IBaseModelListener;

import java.lang.ref.WeakReference;

/**
 * NewsListModel
 * NewsChannelModel
 * 把这俩抽离出BaseMvvmModel
 * 1.首先把listener提取到BaseMvvmModel,这个listener是一个弱引用
 * 2.mPage提取到BaseMvvmModel
 * 3.refresh ,load和 loadNextPage提取到BaseMvvmModel
 * 4.处理重复点击刷新，多次加载下一页。isLoading判断是否正在加载数据，如果正在加载数据，则不会发出请求
 */
public abstract class BaseMvvmModel {
    protected WeakReference<IBaseModelListener> iBaseModelListenerWeakReference;

    protected int mPage = 1;
    private boolean isPaging;//判断是否需要分页
    private final int INIT_PAGE_NUMBER;

    //用来判断在加载的过程当中，其他重复来了请求，网络数据还没回来的话，是不发请求出去的
    private boolean isLoading;

    public BaseMvvmModel(boolean isPaging,int ... initPageNo) {
        this.isPaging = isPaging;
        if (isPaging && initPageNo!=null & initPageNo.length>0)
            INIT_PAGE_NUMBER = initPageNo[0];
        else
            INIT_PAGE_NUMBER = -1;
    }

    public void register(IBaseModelListener listener){
        if (listener != null)
            iBaseModelListenerWeakReference = new WeakReference<>(listener);
    }

    public void refresh(){
        if (!isLoading) {
            if (isPaging) {
                mPage = INIT_PAGE_NUMBER;
            }
            isLoading = true;
            load();
        }
    }

    public abstract void load();

    public void loadNextPage() {
        if (!isLoading){
            isLoading = true;
            load();
        }
    }

}
