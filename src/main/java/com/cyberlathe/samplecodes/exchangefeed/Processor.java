package com.cyberlathe.samplecodes.exchangefeed;

public interface Processor {
    void process(Order order);
    void process(String message);
}
