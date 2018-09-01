package com.yunlinos.eventflyer;

import android.util.Log;

final class BackgroundPoster implements Runnable, Poster {
    private static final String TAG = "BackgroundPoster";
    private final PendingPostQueue queue;
    private final EventFlyer eventFlyer;
    private volatile boolean executorRunning;


    BackgroundPoster(EventFlyer eventFlyer) {
        this.eventFlyer = eventFlyer;
        queue = new PendingPostQueue();
    }

    public void enqueue(Subscription subscription, Object event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
        synchronized (this) {
            queue.enqueue(pendingPost);
            if (!executorRunning) {
                executorRunning = true;
                eventFlyer.getExecutorService().execute(this);
            }
        }
    }

    @Override
    public void run() {
        try {
            try {
                while (true) {
                    PendingPost pendingPost = queue.poll(1000);
                    if (pendingPost == null) {
                        synchronized (this) {
                            // Check again, this time in synchronized
                            pendingPost = queue.poll();
                            if (pendingPost == null) {
                                executorRunning = false;
                                return;
                            }
                        }
                    }
                    eventFlyer.invokeSubscriber(pendingPost);
                }
            } catch (InterruptedException e) {
                Log.e(TAG, Thread.currentThread().getName() + " was interrupted", e);
            }
        } finally {
            executorRunning = false;
        }
    }

}
