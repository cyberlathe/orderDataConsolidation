package com.cyberlathe.orderdataconsolidation.pubsub;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkSubscriber implements Subscriber {
    private static final Logger logger = LoggerFactory.getLogger(NetworkSubscriber.class);
    Socket socket;

    public NetworkSubscriber(Socket socket) {
        logger.info("New socket connection established {}", socket);
        this.socket = socket;
    }

    @Override
    public void onMessage(String msg) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream, true);
            //printWriter.println("Test message to validate successful Client connection");
            printWriter.println(msg);

            logger.info("Publishing message: {} to socket: {} ", msg, socket);
        } catch (Exception e) {
            logger.error("Error publishing message to socket: {}", socket, e);
        }
    }
}
