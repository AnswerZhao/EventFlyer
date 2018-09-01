package com.yunlinos.eventflyerserver;

import android.util.Log;

import com.yunlinos.eventflyer.server.FlyerService;

public class RemoteServer extends FlyerService {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v("yunlinos", "RemoteServer onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("yunlinos", "RemoteServer onDestroy");
    }
}
