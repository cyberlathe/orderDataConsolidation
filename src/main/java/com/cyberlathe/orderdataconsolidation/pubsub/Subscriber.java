package com.cyberlathe.orderdataconsolidation.pubsub;

public interface Subscriber {
    void onMessage(String msg);
}
