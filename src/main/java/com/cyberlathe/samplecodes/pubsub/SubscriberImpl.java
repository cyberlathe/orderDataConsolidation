package com.cyberlathe.samplecodes.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SubscriberImpl implements Subscriber {
    private static final Logger logger = LoggerFactory.getLogger(SubscriberImpl.class);

    @Override
    public void onMessage(String msg) {
        logger.info("Received message: {}", msg);
    }
}