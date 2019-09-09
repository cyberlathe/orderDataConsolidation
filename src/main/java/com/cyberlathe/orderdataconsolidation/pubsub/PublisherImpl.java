package com.cyberlathe.orderdataconsolidation.pubsub;

public class PublisherImpl implements Publisher {
    private MessageBroker messageBroker;

    public PublisherImpl(MessageBroker messageBroker) {
        this.messageBroker = messageBroker;
    }

    @Override
    public void publish(String topic, String message) {
        messageBroker.publish(topic, message);
    }
}
