package com.cyberlathe.samplecodes.pubsub;

public interface Publisher {
    void publish(String topic, String message);
}
