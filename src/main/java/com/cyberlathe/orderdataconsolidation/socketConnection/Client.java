package com.cyberlathe.orderdataconsolidation.socketConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);
    private Socket socket;

    public Client(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            InputStream inputStream = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String message;
            while(true) {
                message = bufferedReader.readLine();
                logger.info("Received Message : {}", message );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
