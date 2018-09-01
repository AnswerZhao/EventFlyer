package com.yunlinos.eventflyer;

import android.os.Parcel;
import android.os.Parcelable;

public class RemoteEvent implements Parcelable {
    private String eventType;
    private String eventContent;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.eventType);
        dest.writeString(this.eventContent);
    }

    public void readFromParcel(Parcel source) {
        eventType = source.readString();
        eventContent = source.readString();
    }

    public RemoteEvent() {
    }

    public RemoteEvent(String eventType, String eventContent) {
        this.eventType = eventType;
        this.eventContent = eventContent;
    }

    protected RemoteEvent(Parcel in) {
        this.eventType = in.readString();
        this.eventContent = in.readString();
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventContent() {
        return eventContent;
    }

    public static final Parcelable.Creator<RemoteEvent> CREATOR = new Parcelable.Creator<RemoteEvent>() {
        @Override
        public RemoteEvent createFromParcel(Parcel source) {
            return new RemoteEvent(source);
        }

        @Override
        public RemoteEvent[] newArray(int size) {
            return new RemoteEvent[size];
        }
    };
}