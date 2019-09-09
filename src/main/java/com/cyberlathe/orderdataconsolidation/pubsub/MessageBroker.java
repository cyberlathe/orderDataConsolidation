package com.cyberlathe.orderdataconsolidation.pubsub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MessageBroker {
    public static String topicTopOfTheBook = "TOP_OF_THE_BOOK";
    public static String topicOrderBook = "ORDER_BOOK";
    public static String topicOrderLevel = "ORDER_LEVEL";

    Map<String, Set<Subscriber>> subscriberTopicMap = new HashMap<>();

    public boolean register(String topic, Subscriber subscriber) {
        boolean returnVal;

        if (subscriberTopicMap.containsKey(topic)) {
            returnVal = subscriberTopicMap.get(topic).add(subscriber);
        } else {
            Set<Subscriber> sub = new HashSet<>();
            returnVal = sub.add(subscriber);
            subscriberTopicMap.put(topic, sub);
        }
        return returnVal;
    }

    public boolean deregister(String topic, Subscriber subscriber) {
        final Set<Subscriber> subs = this.subscriberTopicMap.get(topic);
        return subs.remove(subscriber);
    }

    public void publish(String topic, String message) {
        if(subscriberTopicMap.containsKey(topic)) {
            subscriberTopicMap.get(topic).parallelStream().forEach(subscriber -> subscriber.onMessage(message));
        }
    }
}
