package com.yunlinos.eventclientb;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yunlinos.eventflyer.EventFlyer;
import com.yunlinos.eventflyer.RemoteSubscribe;
import com.yunlinos.eventflyer.ThreadMode;
import com.yunlinos.eventmodel.OnControlEvent;

public class EventListenServer extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventFlyer.getDefault().bindRemoteServer();
        EventFlyer.getDefault().register(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventFlyer.getDefault().unregister(this);
        EventFlyer.getDefault().unBindRemoteServer();
    }

    @RemoteSubscribe(threadMode = ThreadMode.MAIN)
    public void onEventGet(OnControlEvent onControlEvent) {
        Log.v("yunlinos", "EventListenServer onEventGet: " + onControlEvent.getEvent());
    }
}
