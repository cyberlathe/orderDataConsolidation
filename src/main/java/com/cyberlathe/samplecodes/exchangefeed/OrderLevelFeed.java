package com.cyberlathe.samplecodes.exchangefeed;

public class OrderLevelFeed {
    String symbol;
    int level;
    double bestBidPrice;
    int bestBidSize;
    double bestOfferPrice;
    int bestOfferSize;


    public OrderLevelFeed(String symbol, int i) {
        this.symbol = symbol;
        level = i;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getBestBidPrice() {
        return bestBidPrice;
    }

    public void setBestBidPrice(double bestBidPrice) {
        this.bestBidPrice = bestBidPrice;
    }

    public double getBestBidSize() {
        return bestBidSize;
    }

    public void setBestBidSize(int bestBidSize) {
        this.bestBidSize = bestBidSize;
    }

    public double getBestOfferSize() {
        return bestOfferSize;
    }

    public void setBestOfferSize(int bestOfferSize) {
        this.bestOfferSize = bestOfferSize;
    }

    public double getBestOfferPrice() {
        return bestOfferPrice;
    }

    public void setBestOfferPrice(double bestOfferPrice) {
        this.bestOfferPrice = bestOfferPrice;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(level);
        stringBuilder.append(",").append(bestBidSize);
        stringBuilder.append(",").append(bestBidPrice);
        stringBuilder.append(",").append(bestOfferPrice);
        stringBuilder.append(",").append(bestOfferSize);
        return stringBuilder.toString();
    }
}
