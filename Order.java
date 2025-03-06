class Order {
    enum Type { BUY, SELL } // types of order

    Type orderType; //type of order
    String ticker; // name of stock
    int quantity; // quantity of stock
    double price; // price of stock

    //constructor for an order
    public Order(Type orderType, String ticker, int quantity, double price) {
        this.orderType = orderType;
        this.ticker = ticker;
        this.quantity = quantity;
        this.price = price;
    }

    //string representing an order
    @Override
    public String toString() {
        return orderType + " " + ticker + " " + quantity + " @ " + price;
    }
}
