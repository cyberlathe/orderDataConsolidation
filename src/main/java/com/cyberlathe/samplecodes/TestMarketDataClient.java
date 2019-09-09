package com.cyberlathe.samplecodes;

import com.cyberlathe.samplecodes.MarketDataSubscriber.ExchangeConnection;
import com.cyberlathe.samplecodes.MarketDataSubscriber.MarketDataClient;
import com.cyberlathe.samplecodes.exchangefeed.Exchange;
import com.cyberlathe.samplecodes.exchangefeed.OrderBook;
import com.cyberlathe.samplecodes.exchangefeed.OrderProcessor;
import com.cyberlathe.samplecodes.feedsimulator.FileOrderFeed;
import com.cyberlathe.samplecodes.feedsimulator.OrderFeed;
import com.cyberlathe.samplecodes.pubsub.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TestMarketDataClient {
    private static final Logger logger = LoggerFactory.getLogger(TestMarketDataClient.class);

    public static void main(String[] args) {

        TestMarketDataClient testMarketDataClient = new TestMarketDataClient();
        Exchange exch1 = testMarketDataClient.startExchange("EXCH1", "level2data.csv", 8118, 5);
        Exchange exch2 = testMarketDataClient.startExchange("EXCH2", "level2data.csv", 8119, 5);

        MessageBroker clientSideBroker = new MessageBroker();
        Publisher publisher = new PublisherImpl(clientSideBroker);
        OrderBook orderBook = new OrderBook(publisher);
        OrderProcessor orderProcessor = new OrderProcessor(orderBook);

        InetAddress host = null;
        try {
            host = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        //MarketDataClient to connect to exchange1
        MarketDataClient marketDataClient1 = new MarketDataClient();
        testMarketDataClient.startClient(marketDataClient1, clientSideBroker, orderProcessor, "EXCH1", host.getHostName(), 8118);

        //MarketDataClient to connect to exchange2
        MarketDataClient marketDataClient2 = new MarketDataClient();
        testMarketDataClient.startClient(marketDataClient2, clientSideBroker, orderProcessor, "EXCH2", host.getHostName(), 8119);

        //StartFeed
        exch1.startFeed();
        exch2.startFeed();


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        orderBook.refreshstatus();

        //TODO: Cleanup
    }

    private void startClient(MarketDataClient marketDataClient, MessageBroker messageBroker, OrderProcessor orderProcessor, String exchName, String hostName, int port) {

        //Subscribe to exchange market Data
        ExchangeConnection exchangeConnection = marketDataClient.connect(exchName, hostName, port );
        marketDataClient.subscribe(exchangeConnection.getSocket(), MessageBroker.topicOrderBook);

        //Subscribers for consolidated market data
        Subscriber subscriber = new SubscriberImpl();
        messageBroker.register(MessageBroker.topicOrderLevel, subscriber);

        //Start client to listen on socket
        marketDataClient.start(exchName, exchangeConnection.getSocket(), orderProcessor);
    }

    public Exchange startExchange(String exchName, String feedName, int port, int numConnections ) {
        MessageBroker messageBroker = new MessageBroker();
        Publisher publisher = new PublisherImpl(messageBroker);
        OrderBook orderBook = new OrderBook(publisher);
        OrderProcessor orderProcessor = new OrderProcessor(orderBook);
        OrderFeed orderFeed = new FileOrderFeed(feedName, orderProcessor);
        logger.info("Creating new exchange {}", exchName);
        Exchange exchange = new Exchange(exchName, orderFeed, messageBroker);
        exchange.start(port, numConnections);
        return exchange;
    }
}
