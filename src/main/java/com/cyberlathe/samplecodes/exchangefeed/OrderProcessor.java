package com.cyberlathe.samplecodes.exchangefeed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class OrderProcessor implements Processor {
    private static final Logger logger = LoggerFactory.getLogger(OrderProcessor.class);
    private OrderBook orderBook;

    public OrderProcessor(OrderBook orderBook) {
        this.orderBook = orderBook;
    }

    public void process(Order order) {
        switch (order.getOrderType()) {
            case NEW:
                orderBook.processNew(order);
                break;
            case CANCEL:
                orderBook.processCancel(order);
                break;
            case AMEND:
                orderBook.processAmend(order);
                break;
            default:
                processUnIdentifiedMessage(order);
        }
    }

    public void process(String message) {
 //       throw new NotImplementedException();
    }

    private void processUnIdentifiedMessage(Order order) {
//        throw new NotImplementedException();
    }

}
