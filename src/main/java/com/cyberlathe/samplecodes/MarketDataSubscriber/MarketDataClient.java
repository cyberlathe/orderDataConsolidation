package com.cyberlathe.samplecodes.MarketDataSubscriber;

import com.cyberlathe.samplecodes.exchangefeed.Order;
import com.cyberlathe.samplecodes.exchangefeed.OrderProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ParseChar;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MarketDataClient {
    private static final Logger logger = LoggerFactory.getLogger(MarketDataClient.class);
    List<ExchangeConnection> exchangeConnectionList = new ArrayList<>();

    public ExchangeConnection connect(String exchName, String hostName, int port) {
        try {
            Socket socket = new Socket(hostName, port);
            ExchangeConnection exchangeConnection = new ExchangeConnection(exchName, socket, hostName, port);
            exchangeConnectionList.add(exchangeConnection);
//            try {
//                OutputStream outputStream = socket.getOutputStream();
//                PrintWriter printWriter = new PrintWriter(outputStream, true);
//                printWriter.println("Test message to validate successful Client connection");
//            } catch (IOException e) {
//                logger.error("Error in sending message to server", e);
//            }
            return exchangeConnection;
        } catch (IOException e) {
            logger.error("Unable to connect to server @ {}:{}", hostName, port, e);
        }
        return null;
    }

    public void subscribe(Socket socket, String topicName) {
        sendMessage(socket, "subscribe " + topicName);
    }

    public void sendMessage(Socket socket, String message) {
        try {
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream, true);
            printWriter.println(message);
        } catch (IOException e) {
            logger.error("Error in sending message to server", e);
        }
    }

    public void unsubscribe(Socket socket, String topicName) {
        sendMessage(socket, "unsubscribe " + topicName);
    }

    public void start(String exchName, Socket socket, OrderProcessor orderProcessor) {
        new Thread(() -> {
            CellProcessor[] processors = new CellProcessor[] {
                new NotNull(new ParseChar()), // orderType
                new NotNull(), // orderId
                new Optional(new ParseInt()), // quantity
                new Optional(new ParseChar()), // side
                new Optional(), // symbol
                new Optional(new ParseDouble()) // price
            };

            try {
                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                ICsvBeanReader beanReader = new CsvBeanReader(reader, CsvPreference.STANDARD_PREFERENCE);
                Order order = null;
                String[] header = getFeedHeader();
                logger.info("Client connection established.  Reading input message");
                while ((order = beanReader.read(Order.class, header, processors)) != null) {
                    order.setOrderId(exchName + "_" + order.getOrderId());
                    orderProcessor.process(order);
                }
            } catch (IOException e) {
                logger.error("Error in reading message from server", e);
            }
        }).start();
    }

    private String[] getFeedHeader() {
        String[] header = new String[6];
        header[0] = "OrderType";
        header[1] = "OrderId";
        header[2] = "Quantity";
        header[3] = "Side";
        header[4] = "Symbol";
        header[5] = "price";

        return header;
    }
}
