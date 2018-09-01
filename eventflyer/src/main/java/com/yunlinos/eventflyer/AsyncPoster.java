package com.yunlinos.eventflyer;

class AsyncPoster implements Runnable, Poster {
    private final PendingPostQueue queue;
    private final EventFlyer eventFlyer;

    AsyncPoster(EventFlyer eventFlyer) {
        this.eventFlyer = eventFlyer;
        queue = new PendingPostQueue();
    }

    public void enqueue(Subscription subscription, Object event) {
        PendingPost pendingPost = PendingPost.obtainPendingPost(subscription, event);
        queue.enqueue(pendingPost);
        eventFlyer.getExecutorService().execute(this);
    }

    @Override
    public void run() {
        PendingPost pendingPost = queue.poll();
        if (pendingPost == null) {
            throw new IllegalStateException("No pending post available");
        }
        eventFlyer.invokeSubscriber(pendingPost);
    }

}
