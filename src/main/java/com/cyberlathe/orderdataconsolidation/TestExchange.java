package com.cyberlathe.orderdataconsolidation;

import com.cyberlathe.orderdataconsolidation.exchangefeed.Exchange;
import com.cyberlathe.orderdataconsolidation.exchangefeed.OrderBook;
import com.cyberlathe.orderdataconsolidation.exchangefeed.OrderProcessor;
import com.cyberlathe.orderdataconsolidation.feedsimulator.FileOrderFeed;
import com.cyberlathe.orderdataconsolidation.feedsimulator.OrderFeed;
import com.cyberlathe.orderdataconsolidation.pubsub.MessageBroker;
import com.cyberlathe.orderdataconsolidation.pubsub.Publisher;
import com.cyberlathe.orderdataconsolidation.pubsub.PublisherImpl;

public class TestExchange {
    public static void main(String [] args) {
        MessageBroker messageBroker = new MessageBroker();
        Publisher publisher = new PublisherImpl(messageBroker);
        OrderBook orderBook = new OrderBook(publisher);
        OrderProcessor orderProcessor = new OrderProcessor(orderBook);
        OrderFeed orderFeed = new FileOrderFeed("level2data.csv", orderProcessor);
        Exchange exchange = new Exchange("NYSE", orderFeed, messageBroker);
        exchange.start(8118,5);
    }
}
