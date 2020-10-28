package com.xiangxue.base.mvvm.model;


//定义一个mvvm的data model
public interface MvvmDataObserver<F> {
    //第一个参数，数据是什么类型的，第二个参数，来不来自于缓存
    void onSuccess(F t, boolean isFromCache);
    void onFailure(Throwable e);
}
