package com.yunlinos.eventflyer;

public class EventException extends RuntimeException {

    public EventException() {
    }

    public EventException(String detailMessage) {
        super(detailMessage);
    }

    public EventException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public EventException(Throwable throwable) {
        super(throwable);
    }
}
