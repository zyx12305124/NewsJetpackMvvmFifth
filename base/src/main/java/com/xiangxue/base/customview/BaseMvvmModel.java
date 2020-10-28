package com.xiangxue.base.customview;

import com.xiangxue.base.mvvm.model.IBaseModelListener;
import com.xiangxue.base.mvvm.model.PagingResult;

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
public abstract class BaseMvvmModel<RESULT_DATA> {
    protected WeakReference<IBaseModelListener> iBaseModelListenerWeakReference;

    protected int mPage = 1;
    private boolean isNeedPaging;//判断是否需要分页
    private final int INIT_PAGE_NUMBER;

    //用来判断在加载的过程当中，其他重复来了请求，网络数据还没回来的话，是不发请求出去的
    private boolean isLoading;

    public BaseMvvmModel(boolean isNeedPaging,int ... initPageNo) {
        this.isNeedPaging = isNeedPaging;
        if (isNeedPaging && initPageNo!=null & initPageNo.length>0)
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
            if (isNeedPaging) {
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

    protected void notifyResultToListener(RESULT_DATA data){
        IBaseModelListener listener = iBaseModelListenerWeakReference.get();
        if (listener!=null){
            //notify
            if(isNeedPaging){
                listener.onLoadSuccess(this,data,new PagingResult(
                        mPage==INIT_PAGE_NUMBER,
                        data==null?true:((List)data).isEmpty(),//强转成List判断是否为空
                        ((List)data).size()>0));//先这样判断
            } else {
                listener.onLoadSuccess(this,data);
            }
            //update pageNumber
            if (isNeedPaging){
                if (data != null &&((List)data).size()>0){
                    mPage ++;
                }
            }

        }
        isLoading = false;//网络成功后要把loading置为false

    }

    protected void loadFail(final String errorMessage){
        IBaseModelListener listener = iBaseModelListenerWeakReference.get();
//        if (listener != null)
    }

}
