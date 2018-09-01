package com.yunlinos.eventflyer;

import com.yunlinos.eventflyer.RemoteEvent;

interface IEventCallback {
    void callback(in RemoteEvent remoteEvent);
}