package com.cyberlathe.orderdataconsolidation.exchangefeed;

import com.cyberlathe.orderdataconsolidation.pubsub.MessageBroker;
import com.cyberlathe.orderdataconsolidation.pubsub.NetworkSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

public class NetworkClient extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(NetworkClient.class);
    private Socket socket;
    private NetworkSubscriber subscriber;
    private MessageBroker messageBroker;

    public NetworkClient(Socket socket, MessageBroker messageBroker) {
        this.socket = socket;
        subscriber = new NetworkSubscriber(this.socket);
        this.messageBroker = messageBroker;
    }

    public void run() {
        logger.info("Starting a new NetworkClient");

        try {
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String message;
            while(true) {
                message = reader.readLine();
                processMessage(message);
            }
        } catch (IOException e) {
            logger.error("IOException", e);
        }

//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//            String line;
//            while((line = reader.readLine()) != null ) {
//                processMessage(line);
//            }
//
//        } catch (IOException e) {
//            logger.error("IOException", e);
//        }
    }

    public void processMessage(String message) {
        String[] strings = message.split("\\s+");
        if(strings[0].equals("subscribe")) {
            logger.info("Subscribing client to MessageBroker instance {}", messageBroker);
            messageBroker.register(strings[1], subscriber);
        } else if (strings[0].equals("unsubscribe")) {
            messageBroker.deregister(strings[1], subscriber);
        }
    }

}
