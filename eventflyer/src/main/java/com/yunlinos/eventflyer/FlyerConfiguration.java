package com.yunlinos.eventflyer;

import android.content.Context;

public class FlyerConfiguration {
    private final Context applicationContext;
    private final String remoteServiceAction;
    private final String remoteServicePkgName;
    private final String remoteServiceClassName;

    private FlyerConfiguration(Builder builder) {
        applicationContext = builder.applicationContext;
        remoteServiceAction = builder.remoteServiceAction;
        remoteServicePkgName = builder.remoteServicePkgName;
        remoteServiceClassName = builder.remoteServiceClassName;
    }

    public Context getApplicationContext() {
        return applicationContext;
    }

    public String getRemoteServiceAction() {
        return remoteServiceAction;
    }

    public String getRemoteServicePkgName() {
        return remoteServicePkgName;
    }

    public String getRemoteServiceClassName() {
        return remoteServiceClassName;
    }

    public static final class Builder {
        private Context applicationContext;
        private String remoteServiceAction;
        private String remoteServicePkgName;
        private String remoteServiceClassName;

        public Builder() {
        }

        public Builder applicationContext(Context val) {
            applicationContext = val;
            return this;
        }

        public Builder remoteServiceAction(String val) {
            remoteServiceAction = val;
            return this;
        }

        public Builder remoteServicePkgName(String val) {
            remoteServicePkgName = val;
            return this;
        }

        public Builder remoteServiceClassName(String val) {
            remoteServiceClassName = val;
            return this;
        }

        public FlyerConfiguration build() {
            return new FlyerConfiguration(this);
        }
    }
}
