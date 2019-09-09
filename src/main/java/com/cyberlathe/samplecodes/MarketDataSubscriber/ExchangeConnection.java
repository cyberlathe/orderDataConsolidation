package com.cyberlathe.samplecodes.MarketDataSubscriber;

import java.net.Socket;

public class ExchangeConnection {
    public String exchName;
    public Socket socket;
    public String hostName;
    public int port;

    public ExchangeConnection(String exchName, Socket socket, String hostName, int port) {
        this.exchName = exchName;
        this.socket = socket;
        this.hostName = hostName;
        this.port = port;
    }

    public String getExchName() {
        return exchName;
    }

    public void setExchName(String exchName) {
        this.exchName = exchName;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
