package com.yunlinos.eventflyer.server;

import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.yunlinos.eventflyer.IEventCallback;
import com.yunlinos.eventflyer.IEventManager;
import com.yunlinos.eventflyer.RemoteEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FlyerServiceBinder extends IEventManager.Stub {
    private static final String TAG = "FlyerServiceBinder";

    private final Map<String, String> mClientMap;
    private final Map<String, List<String>> mEventTypeBySubscriber;
    private RemoteCallbackList<IEventCallback> mRemoteCallbackList;
    private static FlyerServiceBinder sInstance = new FlyerServiceBinder();

    public FlyerServiceBinder() {
        mClientMap = new HashMap<>();
        mEventTypeBySubscriber = new HashMap<>();
        mRemoteCallbackList = new RemoteCallbackList<>();
    }

    public static FlyerServiceBinder getInstance() {
        return sInstance;
    }

    @Override
    public String getToken(String packageName) {
        Log.d(TAG, "getToken: " + packageName);
        String cacheToken = mClientMap.get(packageName);
        if (TextUtils.isEmpty(cacheToken)) {
            String token = UUID.randomUUID().toString();
            mClientMap.put(packageName, token);
            return token;
        } else {
            return cacheToken;
        }
    }

    @Override
    public void postMessage(String token, RemoteEvent remoteEvent) {
        Log.d(TAG, "postMessage: " + remoteEvent.getEventType());
        int length = mRemoteCallbackList.beginBroadcast();
        for (int i = 0; i < length; i++) {
            String cookie = (String) mRemoteCallbackList.getBroadcastCookie(i);
            if (!cookie.equals(token)) {
                try {
                    mRemoteCallbackList.getBroadcastItem(i).callback(remoteEvent);
                } catch (RemoteException e) {
                    Log.e(TAG, "postMessage", e);
                }
            }
        }
        mRemoteCallbackList.finishBroadcast();
    }

    @Override
    public void registerCallback(String token, IEventCallback callback) {
        mRemoteCallbackList.register(callback, token);
    }

    @Override
    public void unregisterCallback(String token, IEventCallback callback) {
        mRemoteCallbackList.unregister(callback);
    }

    @Override
    public void registerEventType(String token, String eventType) {
        Log.d(TAG, "registerEventType token: " + token + " eventType: " + eventType);
        List<String> mEventTypes = mEventTypeBySubscriber.get(token);
        if (mEventTypes == null) {
            mEventTypes = new ArrayList<>();
        }
        mEventTypes.add(eventType);
        mEventTypeBySubscriber.put(token, mEventTypes);
    }

    @Override
    public void unregisterEventType(String token, String eventType) {
        Log.d(TAG, "unregisterEventType token: " + token + " eventType: " + eventType);
        List<String> mEventTypes = mEventTypeBySubscriber.get(token);
        if (mEventTypes != null && mEventTypes.contains(eventType)) {
            mEventTypes.remove(eventType);
            mEventTypeBySubscriber.put(token, mEventTypes);
        }
    }
}
