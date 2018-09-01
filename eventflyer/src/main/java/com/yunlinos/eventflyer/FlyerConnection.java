package com.yunlinos.eventflyer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

public class FlyerConnection {
    private static final String TAG = "FlyerConnection";
    private static final int MESSAGE_RECONNECT = 100;
    private static final int MESSAGE_REGISTER_TYPE = 101;
    private static final int MESSAGE_UNREGISTER_TYPE = 102;
    private static final int MESSAGE_POST = 103;
    private static final int DELAY_PROCESS = 20;
    private static final int DELAY_RECONNECT = 1000;
    private static final int MAX_RETRY_TIMES = 5;

    private Context mContext;
    private FlyerConfiguration mFlyerConfiguration;
    private IEventManager mEventManager;
    private String mToken;

    private Handler mControlHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MESSAGE_RECONNECT) {
                connectRemoteServer();
            } else if (msg.arg1 < MAX_RETRY_TIMES) {
                processMessage(msg);
            }
        }

        private void processMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_REGISTER_TYPE:
                    register((String) msg.obj, msg.arg1);
                    break;
                case MESSAGE_UNREGISTER_TYPE:
                    unregister((String) msg.obj, msg.arg1);
                    break;
                case MESSAGE_POST:
                    postMessage(msg.obj, msg.arg1);
                    break;
                default:
                    break;
            }
        }
    };

    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.e(TAG, "Remote Connect died");
            clearConnection();
            mControlHandler.sendEmptyMessageDelayed(MESSAGE_RECONNECT, DELAY_RECONNECT);
        }
    };

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            configurationConnection(binder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, "onServiceDisconnected");
            clearConnection();
        }
    };

    private IEventCallback.Stub mEventCallback = new IEventCallback.Stub() {
        @Override
        public void callback(RemoteEvent remoteEvent) {
            Log.d(TAG, "RemoteEvent callback: " + remoteEvent.getEventType());
            Object event = null;
            try {
                event = JSON.parseObject(remoteEvent.getEventContent(), Class.forName(remoteEvent.getEventType()));
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Event callback from remote: ", e);
            }
            if (event != null) {
                EventFlyer.getDefault().dispatchEvent(event);
            } else {
                Log.d(TAG, "translate event fail!");
            }
        }
    };

    public FlyerConnection(FlyerConfiguration flyerConfiguration) {
        mFlyerConfiguration = flyerConfiguration;
        mContext = flyerConfiguration.getApplicationContext();
    }

    public synchronized void connectRemoteServer() {
        if (mEventManager == null) {
            Intent intent = new Intent();
            if (!TextUtils.isEmpty(mFlyerConfiguration.getRemoteServiceAction())) {
                intent.setAction(mFlyerConfiguration.getRemoteServiceAction());
            } else {
                intent.setClassName(mFlyerConfiguration.getRemoteServicePkgName(), mFlyerConfiguration.getRemoteServiceClassName());
            }
            intent.setPackage(mFlyerConfiguration.getRemoteServicePkgName());
            mContext.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }

    private void configurationConnection(IBinder binder) {
        try {
            binder.linkToDeath(mDeathRecipient, 0);
            mEventManager = IEventManager.Stub.asInterface(binder);
            mToken = mEventManager.getToken(mContext.getPackageName());
            mEventManager.registerCallback(mToken, mEventCallback);
            Log.d(TAG, "onServiceDisconnected, Token: " + mToken);
        } catch (RemoteException e) {
            Log.e(TAG, "connectRemoteServer", e);
        }
    }

    public synchronized void disconnectRemoteServer() {
        if (mEventManager != null) {
            mContext.unbindService(mServiceConnection);
        }
    }

    private void clearConnection() {
        mEventManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
        try {
            mEventManager.unregisterCallback(mToken, mEventCallback);
        } catch (RemoteException e) {
            Log.e(TAG, "disconnectRemoteServer", e);
        }
        mEventManager = null;
    }

    public void register(String eventType) {
        register(eventType, 0);
    }

    private void register(String eventType, int retryTimes) {
        if (mEventManager == null) {
            Log.d(TAG, "register try: " + retryTimes);
            postDelayProcessMessage(eventType, MESSAGE_REGISTER_TYPE, retryTimes);
        } else {
            try {
                mEventManager.registerEventType(mToken, eventType);
                Log.d(TAG, "register success: " + eventType);
            } catch (RemoteException e) {
                Log.e(TAG, "register error: ", e);
            }
        }
    }

    public void unregister(String eventType) {
        unregister(eventType, 0);
    }

    private void unregister(String eventType, int retryTimes) {
        if (mEventManager == null) {
            Log.d(TAG, "unregister try: " + retryTimes);
            postDelayProcessMessage(eventType, MESSAGE_UNREGISTER_TYPE, retryTimes);
        } else {
            try {
                mEventManager.unregisterEventType(mToken, eventType);
                Log.d(TAG, "unregister success: " + eventType);
            } catch (RemoteException e) {
                Log.e(TAG, "unregister error: ", e);
            }
        }
    }

    public void postMessage(Object event) {
        postMessage(event, 0);
    }

    private void postMessage(Object event, int retryTimes) {
        if (mEventManager == null) {
            Log.d(TAG, "postMessage try: " + retryTimes);
            postDelayProcessMessage(event, MESSAGE_POST, retryTimes);
        } else {
            try {
                String eventType = event.getClass().getName();
                String eventContent = JSON.toJSONString(event);
                mEventManager.postMessage(mToken, new RemoteEvent(eventType, eventContent));
                Log.d(TAG, "postMessage success: " + eventType);
            } catch (RemoteException e) {
                Log.e(TAG, "postMessage: ", e);
            }
        }
    }

    private void postDelayProcessMessage(Object eventType, int messageWhat, int retryTimes) {
        Message message = mControlHandler.obtainMessage();
        message.what = messageWhat;
        message.arg1 = ++retryTimes;
        message.obj = eventType;
        mControlHandler.sendMessageDelayed(message, DELAY_PROCESS);
    }
}