package com.cyberlathe.samplecodes.exchangefeed;

public class Order {
    public enum Side {
        BUY('B'), SELL('S'), UNKNOWN('U');
        private char value = 'U';

        Side(char value) {
            this.value = value;
        }

        public char getValue() {
            return value;
        }
    };

    public enum OrderType {
        NEW('D'), CANCEL('F'), AMEND('G'), UNKNOWN('U');
        private char value = 'U';

        OrderType(char value) {
            this.value = value;
        }
        public char getValue() {
            return value;
        }
    };

    OrderType orderType;
    String orderId;
    int quantity;
    Side side;
    String symbol;
    double price;

    public Order() {}

    public Order(OrderType orderType, String orderId, String symbol, double price, Side side, int quantity) {
        this.orderType = orderType;
        this.orderId = orderId;
        this.symbol = symbol;
        this.price = price;
        this.side = side;
        this.quantity = quantity;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public void setOrderType(char orderType) {
        switch (orderType){
            case 'D':
                this.orderType = OrderType.NEW;
                break;
            case 'G':
                this.orderType = OrderType.AMEND;
                break;
            case 'F':
                this.orderType = OrderType.CANCEL;
                break;
            default:
                break;
        }
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Side getSide() {
        return side;
    }

    public void setSide(Side side) {
        this.side = side;
    }

    public void setSide(char side) {
        switch(side) {
            case 'B':
                this.side = Side.BUY;
                break;
            case 'S':
                this.side = Side.SELL;
                break;
            default:
                break;
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getOrderType().getValue());
        stringBuilder.append(",").append(getOrderId());

        switch(getOrderType()) {
            case NEW:
                stringBuilder.append(",").append(getQuantity());
                stringBuilder.append(",").append(getSide().getValue());
                stringBuilder.append(",").append(getSymbol());
                stringBuilder.append(",").append(getPrice());
                break;
            case AMEND:
                stringBuilder.append(",").append(getQuantity());
                stringBuilder.append(",,,");
                break;
            case CANCEL:
                stringBuilder.append(",,,,");
                break;
            default:
                break;
        }

        return stringBuilder.toString();
    }
}
