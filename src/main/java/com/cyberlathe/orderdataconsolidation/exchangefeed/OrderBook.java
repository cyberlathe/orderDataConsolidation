package com.cyberlathe.orderdataconsolidation.exchangefeed;

import com.cyberlathe.orderdataconsolidation.pubsub.MessageBroker;
import com.cyberlathe.orderdataconsolidation.pubsub.Publisher;

import java.util.*;

public class OrderBook {
    Map<Order.Side, Map<String, SortedMap<Double, Integer>>> orderBook = new HashMap<>();
    final Map<String, Order> orderIdMap = new HashMap<> ();

    private Publisher publisher;

    public OrderBook(Publisher publisher) {
        this.publisher = publisher;

        orderBook.put(Order.Side.SELL, new HashMap<>());
        orderBook.put(Order.Side.BUY, new HashMap<>());
    }

    public void processNew(Order order) {
        synchronized (this) {
            Map<String, SortedMap<Double, Integer>> innerBook = orderBook.get(order.getSide());

            if (innerBook.containsKey(order.getSymbol())) {
                SortedMap<Double, Integer> priceQtyMap = innerBook.get(order.getSymbol());
                if (priceQtyMap.containsKey(order.getPrice())) {
                    //add qty to existing qty
                    priceQtyMap.put(order.getPrice(), priceQtyMap.get(order.getPrice()) + order.getQuantity());
                } else {
                    priceQtyMap.put(order.getPrice(), order.getQuantity());
                }
            } else {
                SortedMap<Double, Integer> priceQtyMap = null;
                if (order.getSide() == Order.Side.BUY) {
                    priceQtyMap = new TreeMap<>();
                } else {
                    priceQtyMap = new TreeMap<>(Collections.reverseOrder());
                }
                priceQtyMap.put(order.getPrice(), order.getQuantity());
                innerBook.put(order.getSymbol(), priceQtyMap);
            }

            orderIdMap.put(order.getOrderId(), order);

            publishUpdate(order);
            publishUpdate(order.getSymbol());
        }
    }

    public void processAmend(Order order) {
        synchronized (this) {
            //TODO: Handle error checkss
            if (orderIdMap.containsKey(order.getOrderId())) {
                Order parentOrder = orderIdMap.get(order.getOrderId());
                int aggQty = orderBook.get(parentOrder.getSide()).get(parentOrder.getSymbol()).get(parentOrder.getPrice());
                int oldQty = parentOrder.getQuantity();
                int diff = oldQty - order.getQuantity();

                //change qty in map
                orderBook.get(parentOrder.getSide()).get(parentOrder.getSymbol()).put(parentOrder.getPrice(), aggQty - diff);
                parentOrder.setQuantity(order.getQuantity());

                publishUpdate(order);
                publishUpdate(parentOrder.getSymbol());
            }
        }
    }

    public void processCancel(Order order) {
        synchronized (this) {
            if (orderIdMap.containsKey(order.getOrderId())) {
                Order parentOrder = orderIdMap.get(order.getOrderId());

                //TODO: Handle error checks
                int oldQty = orderBook.get(parentOrder.getSide()).get(parentOrder.getSymbol()).get(parentOrder.getPrice());

                if (oldQty == parentOrder.getQuantity()) {
                    //remove the price entry
                    orderBook.get(parentOrder.getSide()).get(parentOrder.getSymbol()).remove(parentOrder.getPrice());
                } else {
                    orderBook.get(parentOrder.getSide()).get(parentOrder.getSymbol()).put(parentOrder.getPrice(), oldQty - parentOrder.getQuantity());
                }

                orderIdMap.remove(order.getOrderId());

                publishUpdate(order);
                publishUpdate(parentOrder.getSymbol());
            }
        }
    }

    public String getTopOfTheBook(String symbol, Order.Side side) {
        StringBuilder stringBuilder = new StringBuilder();
        if(orderBook.get(side).containsKey(symbol)) {
            SortedMap<Double, Integer> priceOrderMap =  orderBook.get(side).get(symbol);
            Double price = priceOrderMap.firstKey();
            int quantity = priceOrderMap.get(price);
            stringBuilder.append(price);
            stringBuilder.append(",").append(quantity);
        }
        return stringBuilder.toString();
    }

    public String getTopOfTheBook(String symbol) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(symbol);
        stringBuilder.append(",").append(getTopOfTheBook(symbol, Order.Side.BUY));
        stringBuilder.append(",").append(getTopOfTheBook(symbol, Order.Side.SELL));
        return stringBuilder.toString();
    }

    public String getLevel2Data(String symbol, int level) {
        SortedMap<Integer, OrderLevelFeed> levelFeedMap = new TreeMap<>();
        for(int i=0; i<level;i++) {
            levelFeedMap.put(i,new OrderLevelFeed(symbol,i));
        }

        if(orderBook.get(Order.Side.BUY).containsKey(symbol)) {
            int count = 0;

            for (Map.Entry<Double, Integer> mapEntry : orderBook.get(Order.Side.BUY).get(symbol).entrySet()) {
                OrderLevelFeed levelFeed;

                levelFeed = levelFeedMap.get(count);

                levelFeed.setBestBidPrice(mapEntry.getKey());
                levelFeed.setBestBidSize(mapEntry.getValue());
                count++;
                if(count == level) { break; }
            }
        }

        if(orderBook.get(Order.Side.SELL).containsKey(symbol)) {
            int count = 0;

            for (Map.Entry<Double, Integer> mapEntry : orderBook.get(Order.Side.SELL).get(symbol).entrySet()) {
                OrderLevelFeed levelFeed;

                levelFeed = levelFeedMap.get(count);

                levelFeed.setBestOfferPrice(mapEntry.getKey());
                levelFeed.setBestOfferSize(mapEntry.getValue());
                count++;
                if(count == level) { break; }
            }
        }

        StringJoiner stringJoiner = new StringJoiner(",");
        stringJoiner.add(symbol);
        for(Map.Entry<Integer, OrderLevelFeed> mapEntry: levelFeedMap.entrySet()) {
            stringJoiner.add(mapEntry.getValue().toString());
        }

        return stringJoiner.toString();
    }

    public void publishUpdate(String symbol) {
        publisher.publish(MessageBroker.topicTopOfTheBook,getTopOfTheBook(symbol));
        publisher.publish(MessageBroker.topicOrderLevel,getLevel2Data(symbol,5));
    }

    public void publishUpdate(Order order) {
        publisher.publish(MessageBroker.topicOrderBook,order.toString());
    }

    public void refreshstatus() {
        for(String symbol:orderBook.get(Order.Side.BUY).keySet()) {
            publisher.publish(MessageBroker.topicOrderLevel,getLevel2Data(symbol,5));
        }

        for(String symbol:orderBook.get(Order.Side.SELL).keySet()) {
            publisher.publish(MessageBroker.topicOrderLevel,getLevel2Data(symbol,5));
        }
    }
}
