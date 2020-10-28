package com.xiangxue.base.mvvm.model;

import com.xiangxue.base.customview.BaseMvvmModel;

public interface IBaseModelListener<DATA> {
//    void onLoadSuccess(DATA data, PagingResult... result);
    //这里新增了 一个model参数，便于判断是来自哪个model的通知
    void onLoadSuccess(BaseMvvmModel model,DATA data, PagingResult... result);
    void onLoadFail(String message);
}
