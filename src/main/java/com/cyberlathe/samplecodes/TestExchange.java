package com.cyberlathe.samplecodes;

import com.cyberlathe.samplecodes.exchangefeed.Exchange;
import com.cyberlathe.samplecodes.exchangefeed.OrderBook;
import com.cyberlathe.samplecodes.exchangefeed.OrderProcessor;
import com.cyberlathe.samplecodes.feedsimulator.FileOrderFeed;
import com.cyberlathe.samplecodes.feedsimulator.OrderFeed;
import com.cyberlathe.samplecodes.pubsub.MessageBroker;
import com.cyberlathe.samplecodes.pubsub.Publisher;
import com.cyberlathe.samplecodes.pubsub.PublisherImpl;

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
