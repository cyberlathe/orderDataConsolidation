package com.cyberlathe.orderdataconsolidation.pubsub;

public interface Publisher {
    void publish(String topic, String message);
}
