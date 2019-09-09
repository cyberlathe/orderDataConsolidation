package com.cyberlathe.samplecodes.socketConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    public void start(int port) {
        new Thread( () -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                logger.info("Server started and listening on port {}", port);
                while (true) {
                    Socket socket = serverSocket.accept();
                    logger.info("New client connected on port {}", port);
                    new Client(socket).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
