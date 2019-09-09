package com.cyberlathe.orderdataconsolidation.exchangefeed;

import com.cyberlathe.orderdataconsolidation.pubsub.MessageBroker;
import com.cyberlathe.orderdataconsolidation.pubsub.Publisher;

import java.util.*;

public class OrderBookHash {
    //Map<String, Map<String,Map<Double, Map<Integer, Map<String, Order>>>>> orderbook = new HashMap<>();
    //Symbol, price, orderSet
    Map<String, SortedMap<Double, OrderSet>> buyOrderBook = new HashMap<>();
    Map<String, SortedMap<Double, OrderSet>> sellOrderBook = new HashMap<>();
    Map<String, Order> orderIdMap = new HashMap<> ();

    private Publisher publisher;

    public OrderBookHash(Publisher publisher) {
        this.publisher = publisher;
    }

    public void processNew(Order order) {
        Map<String, SortedMap<Double, OrderSet>> orderBook = buyOrderBook;
        if(order.getSide() == Order.Side.SELL) {
            orderBook = sellOrderBook;
        }

        if (orderBook.containsKey(order.getSymbol())) {
            SortedMap<Double, OrderSet> priceOrderMap = orderBook.get(order.getSymbol());
            if (priceOrderMap.containsKey(order.getPrice())) {
                priceOrderMap.get(order.getPrice()).add(order);
            } else {
                OrderSet orderSet = new OrderSet();
                orderSet.add(order);
                priceOrderMap.put(order.getPrice(), orderSet);
            }
        } else {
            OrderSet orderSet = new OrderSet();
            orderSet.add(order);
            SortedMap<Double, OrderSet> priceOrderMap = null;
            if(order.getSide() == Order.Side.BUY) {
                priceOrderMap = new TreeMap<>();
            } else {
                priceOrderMap = new TreeMap<>(Collections.reverseOrder());
            }
            priceOrderMap.put(order.getPrice(), orderSet);
            orderBook.put(order.getSymbol(), priceOrderMap);
        }

        orderIdMap.put(order.getOrderId(), order);
        publishUpdate(order);
    }

    public void processAmend(Order order) {
        Order parentOrder = orderIdMap.get(order.getOrderId());

        Map<String, SortedMap<Double, OrderSet>> orderBook = buyOrderBook;
        if(parentOrder.getSide() == Order.Side.SELL) {
            orderBook = sellOrderBook;
        }

        SortedMap<Double, OrderSet> priceOrderMap = orderBook.get(order.getSymbol());
        OrderSet orderSet = priceOrderMap.get(order.getPrice());
        orderSet.modify(order);

        publishUpdate(order);
    }

    public void processCancel(Order order) {
        Order parentOrder = orderIdMap.get(order.getOrderId());

        Map<String, SortedMap<Double, OrderSet>> orderBook = buyOrderBook;
        if(parentOrder.getSide() == Order.Side.SELL) {
            orderBook = sellOrderBook;
        }

        SortedMap<Double, OrderSet> priceOrderMap = orderBook.get(order.getSymbol());
        OrderSet orderSet = priceOrderMap.get(order.getPrice());
        orderSet.delete(order);

        orderIdMap.remove(order.getOrderId());

        publishUpdate(order);
    }

    public String getTopOfTheBook(String symbol, Map<String, SortedMap<Double, OrderSet>> orderBook) {
        StringBuilder stringBuilder = new StringBuilder();
        if(orderBook.containsKey(symbol)) {
            SortedMap<Double, OrderSet> priceOrderMap = orderBook.get(symbol);
            Double price = priceOrderMap.firstKey();
            int quantity = priceOrderMap.get(price).getTotalQuantity();
            stringBuilder.append(price);
            stringBuilder.append(",").append(quantity);
        }
        return stringBuilder.toString();
    }

    public String getTopOfTheBook(String symbol) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(symbol);
        stringBuilder.append(",").append(getTopOfTheBook(symbol, buyOrderBook));
        stringBuilder.append(",").append(getTopOfTheBook(symbol, sellOrderBook));
        return stringBuilder.toString();
    }

    public String getLevel2Data(String symbol, int levels) {
        SortedMap<Integer, OrderLevelFeed> levelFeedMap = new TreeMap<>();
        for(int i=0; i<levels;i++) {
            levelFeedMap.put(i,new OrderLevelFeed(symbol,i));
        }

        SortedMap<Double, OrderSet> priceOrderMap = buyOrderBook.get(symbol);

        int buyCount = 0;
        for (Map.Entry<Double, OrderSet> mapEntry : priceOrderMap.entrySet()) {
            OrderLevelFeed levelFeed;
            levelFeed = levelFeedMap.get(buyCount);

            levelFeed.setBestBidPrice(mapEntry.getKey());
            levelFeed.setBestBidSize(mapEntry.getValue().getTotalQuantity());
            buyCount++;
            if(buyCount == levels) { break; }
        }

        int sellCount = 0;
        for (Map.Entry<Double, OrderSet> mapEntry : priceOrderMap.entrySet()) {
            OrderLevelFeed levelFeed;
            levelFeed = levelFeedMap.get(sellCount);

            levelFeed.setBestOfferPrice(mapEntry.getKey());
            levelFeed.setBestOfferSize(mapEntry.getValue().getTotalQuantity());
            sellCount++;
            if(sellCount == levels) { break; }
        }

        int max = buyCount > sellCount ? buyCount:sellCount;

        StringJoiner stringJoiner = new StringJoiner(",");
        for(Map.Entry<Integer, OrderLevelFeed> mapEntry: levelFeedMap.entrySet()) {
            stringJoiner.add(mapEntry.getValue().toString());
        }

        return stringJoiner.toString();
    }

    public void publishUpdate(Order order) {
        publisher.publish(MessageBroker.topicOrderBook,order.toString());
        publisher.publish(MessageBroker.topicTopOfTheBook,getTopOfTheBook(order.getSymbol()));
    }
}
