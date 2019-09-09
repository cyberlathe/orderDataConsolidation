package com.cyberlathe.orderdataconsolidation;

import com.cyberlathe.orderdataconsolidation.socketConnection.Server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestSocketConnection {

    public static void main(String[] args) {
        Server server = new Server();
        server.start(8118);

        InetAddress host = null;
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            Socket socket = new Socket(host.getHostName(), 8118);
            OutputStream output = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(output, true);

            printWriter.println("hello server");

        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Socket socket = new Socket(host.getHostName(), 8118);
            OutputStream output = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(output, true);

            printWriter.println("hello server2");

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
