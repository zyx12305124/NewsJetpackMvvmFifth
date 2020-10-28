package com.xiangxue.base.mvvm.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BaseCachedData<DATA> {
    //加上注解，不能混淆
    @SerializedName("updateTimeInMillis")
    @Expose
    public long updateTimeInMillis;

    @SerializedName("data")
    @Expose
    public DATA data;
}
