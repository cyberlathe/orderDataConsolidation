package com.cyberlathe.orderdataconsolidation.exchangefeed;

public interface Processor {
    void process(Order order);
    void process(String message);
}
