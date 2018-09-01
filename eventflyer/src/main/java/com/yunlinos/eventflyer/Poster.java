package com.yunlinos.eventflyer;

interface Poster {
    void enqueue(Subscription subscription, Object event);
}
