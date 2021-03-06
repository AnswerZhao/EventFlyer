package com.yunlinos.eventclienta;

import android.app.Application;

import com.yunlinos.eventflyer.EventFlyer;
import com.yunlinos.eventflyer.FlyerConfiguration;

public class ClientAApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        EventFlyer.getDefault().init(new FlyerConfiguration.Builder()
                .applicationContext(this)
                .remoteServicePkgName("com.yunlinos.eventflyerserver")
                .remoteServiceClassName("com.yunlinos.eventflyerserver.RemoteServer")
                .build());
    }
}
