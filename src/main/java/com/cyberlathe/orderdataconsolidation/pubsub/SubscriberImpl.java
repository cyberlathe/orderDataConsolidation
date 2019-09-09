package com.cyberlathe.orderdataconsolidation.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubscriberImpl implements Subscriber {
    private static final Logger logger = LoggerFactory.getLogger(SubscriberImpl.class);

    @Override
    public void onMessage(String msg) {
        logger.info("Received message: {}", msg);
    }
}