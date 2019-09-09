package com.cyberlathe.orderdataconsolidation.exchangefeed;

import com.cyberlathe.orderdataconsolidation.feedsimulator.OrderFeed;
import com.cyberlathe.orderdataconsolidation.pubsub.MessageBroker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.sleep;

public class Exchange {
    private static final Logger logger = LoggerFactory.getLogger(Exchange.class);
    private OrderFeed orderFeed;
    private String exchName;
    private MessageBroker messageBroker;

    public Exchange(String name, OrderFeed orderFeed, MessageBroker messageBroker) {
        this.exchName = name;
        this.orderFeed = orderFeed;
        this.messageBroker = messageBroker;
    }

    public void start(int port, int numThreads) {
        logger.info("Exchange {} started. Listening on port {}", exchName, port);
        //Server server = new Server();
        //server.start(port);
        startSocketServer(port, numThreads);
    }

    public void startFeed() {
        new Thread(() -> {
            logger.info("Staring Exchange Feed Simulator : {}", exchName);
            orderFeed.startFeed();
        }).start();
    }

    public void startSocketServer(int portNumber, int numThreads) {
        logger.info("Exch {} waiting for clients to connect", exchName);

        new Thread( () -> {
            try (ServerSocket listener = new ServerSocket(portNumber)) {
                while (true) {
                    Socket socket = listener.accept();
                    logger.info("Accepting a new client connection");
                    new NetworkClient(socket, messageBroker).start();
                    //pool.execute(() -> new NetworkClient(socket, messageBroker));
                }
            } catch (IOException e) {
                logger.error("Socket connection error", e);
            }
        }).start();
    }
};