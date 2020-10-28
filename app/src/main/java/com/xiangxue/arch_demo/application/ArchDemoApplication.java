package com.xiangxue.arch_demo.application;

import android.app.Application;
import com.xiangxue.network.base.NetworkApi;
import com.xiangxue.base.preference.PreferencesUtil;

/**
 * Created by Allen on 2017/7/20.
 * 保留所有版权，未经允许请不要分享到互联网和其他人
 */
public class ArchDemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        NetworkApi.init(new NetworkRequestInfo(this));
        PreferencesUtil.init(this);
    }
}
