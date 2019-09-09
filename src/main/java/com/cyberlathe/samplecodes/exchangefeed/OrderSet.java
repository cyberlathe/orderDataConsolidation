package com.cyberlathe.samplecodes.exchangefeed;

import java.util.SortedMap;
import java.util.TreeMap;


public class OrderSet {
    private SortedMap<String, Order> orderMap = new TreeMap<>();
    private int totalQuantity;

    public void add(Order order) {
        orderMap.put(order.getOrderId(), order);
        totalQuantity += order.getQuantity();
    }

    public boolean modify(Order order) {
        if (orderMap.containsKey(order.getOrderId())) {
            Order parentOrder = orderMap.get(order.getOrderId());
            totalQuantity -= parentOrder.getQuantity();
            parentOrder.setQuantity(order.getQuantity());
            totalQuantity += order.getQuantity();
            return true;
        }
        return false;
    }

    public boolean delete(Order order) {
        if (orderMap.containsKey(order.getOrderId())) {
            Order parentOrder = orderMap.get(order.getOrderId());
            totalQuantity -= parentOrder.getQuantity();
            orderMap.remove(order.getOrderId());
            return true;
        }
        return false;
    }

    public int getTotalQuantity() {
        return totalQuantity;
    }
}
