package com.yunlinos.eventflyer.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.ServiceManager;

public class FlyerService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return FlyerServiceBinder.getInstance();
    }
}
