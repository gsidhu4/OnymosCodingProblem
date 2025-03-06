import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;


public class StockTradingEngine {
    private static final int NUM_TICKERS = 1024; // fixed number of stocks for the order book
    private final List<ConcurrentLinkedQueue<Order>> orderBook; // List of queues for each stocks orders.

    public StockTradingEngine() {
        orderBook = new ArrayList<>(NUM_TICKERS); // creating array list for the order book
        for (int i = 0; i < NUM_TICKERS; i++) {
            orderBook.add(new ConcurrentLinkedQueue<>()); // Adds an empty queue for each ticker.
        }
    }

    // Maps a ticker symbol to an index in the order book
    private int getTickerIndex(String ticker) {
        return Math.abs(ticker.hashCode() % NUM_TICKERS);
    }

    // Adds an order to the order book
    public void addOrder(Order.Type orderType, String ticker, int quantity, double price) {
        Order order = new Order(orderType, ticker, quantity, price); // Creates a new order.
        int index = getTickerIndex(ticker); // Finds the order book index for the ticker.
        orderBook.get(index).add(order); // Adds the order to the respective ticker’s queue.
        System.out.println("Order Added: " + order); // Logs the added order.
        matchOrders(ticker); // Attempts to match buy and sell orders.
    }

    // Matches buy and sell orders for a specific stock
    public void matchOrders(String ticker) {
        int index = getTickerIndex(ticker); // Finds the order book index for the ticker.
        ConcurrentLinkedQueue<Order> orders = orderBook.get(index); // Gets the orders for the ticker.

        List<Order> buyOrders = new ArrayList<>(); // Temporary list for buy orders.
        List<Order> sellOrders = new ArrayList<>(); // Temporary list for sell orders.

        // Separates buy and sell orders.
        for (Order order : orders) {
            if (order.orderType == Order.Type.BUY) {
                buyOrders.add(order);
            } else {
                sellOrders.add(order);
            }
        }

        int i = 0, j = 0;

        // Matches buy and sell orders based on price and quantity.
        while (i < buyOrders.size() && j < sellOrders.size()) {
            Order buy = buyOrders.get(i);
            Order sell = sellOrders.get(j);

            if (buy.price >= sell.price) { // Checks if the buy price meets the sell price.
                int matchedQuantity = Math.min(buy.quantity, sell.quantity); // Finds the minimum quantity available.
                System.out.println("Matched: " + matchedQuantity + " " + buy.ticker + " @ " + sell.price);

                buy.quantity -= matchedQuantity; // Reduces buy order quantity.
                sell.quantity -= matchedQuantity; // Reduces sell order quantity.


                if (buy.quantity == 0) i++; // Moves to next buy order if fully matched.
                if (sell.quantity == 0) j++; // Moves to next sell order if fully matched.
            } else {
                break; // Stops if prices don’t align.
            }
        }
    }

    public static void main(String[] args) {
        StockTradingEngine engine = new StockTradingEngine(); // Creates the trading engine.
        String[] tickers = {"AAPL", "GOOG", "MSFT", "TSLA","NVDA","VOO","META","AMZN"};  //sample stocks

        Runnable simulator = () -> {
            while (true) {
                // random type,ticker,quantity, and price
                Order.Type type = ThreadLocalRandom.current().nextBoolean() ? Order.Type.BUY : Order.Type.SELL;
                String ticker = tickers[ThreadLocalRandom.current().nextInt(tickers.length)];
                int quantity = ThreadLocalRandom.current().nextInt(1, 101);
                double price = ThreadLocalRandom.current().nextDouble(100, 1000);

                //adds the order to the engine
                engine.addOrder(type, ticker, quantity, price);

                // wait 1 second between orders
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        Thread simulationThread = new Thread(simulator); // creates a thread for the simulation
        simulationThread.start(); // starts the simulation thread
    }
}
