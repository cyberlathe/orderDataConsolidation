package com.cyberlathe.samplecodes.pubsub;

public interface Subscriber {
    void onMessage(String msg);
}
