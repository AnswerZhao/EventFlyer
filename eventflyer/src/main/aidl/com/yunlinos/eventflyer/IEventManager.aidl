package com.yunlinos.eventflyer;

import com.yunlinos.eventflyer.IEventCallback;
import com.yunlinos.eventflyer.RemoteEvent;

interface IEventManager {
    String getToken(String packageName);

    void postMessage(String token, in RemoteEvent remoteEvent);

    void registerCallback(String token, IEventCallback callback);

    void unregisterCallback(String token, IEventCallback callback);

    void registerEventType(String token, String eventType);

    void unregisterEventType(String token, String eventType);
}