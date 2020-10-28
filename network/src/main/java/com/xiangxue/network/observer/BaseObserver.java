package com.xiangxue.network.observer;

import com.xiangxue.base.mvvm.model.BaseMvvmModel;
import com.xiangxue.base.mvvm.model.MvvmDataObserver;
import com.xiangxue.network.errorhandler.ExceptionHandle;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class BaseObserver<T> implements Observer<T> {
    BaseMvvmModel baseModel;
    MvvmDataObserver<T> mvvmDataObserver;

    public BaseObserver(BaseMvvmModel baseModel, MvvmDataObserver<T> mvvmDataObserver) {
        this.baseModel = baseModel;
        this.mvvmDataObserver = mvvmDataObserver;
    }

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T t) {
//        onSuccess(t);
        mvvmDataObserver.onSuccess(t,false);
        //网络请求成功的时候这个值传false

    }

    @Override
    public void onError(Throwable e) {
//        onFailure(e);
        if(e instanceof ExceptionHandle.ResponeThrowable){
            mvvmDataObserver.onFailure(e);
        } else {
            mvvmDataObserver.onFailure(new ExceptionHandle.ResponeThrowable(e, ExceptionHandle.ERROR.UNKNOWN));
        }
    }

    @Override
    public void onComplete() {

    }
//
//    public abstract void onSuccess(T t);
//    public abstract void onFailure(Throwable e);
}
