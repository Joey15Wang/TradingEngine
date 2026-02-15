package com.BixMex.orderBook;

import com.BixMex.common.Order;
import com.BixMex.common.Side;

import java.util.*;

public class OrderBookImpl implements OrderBook {
    //Buy common.Order Book in descending order
    private final TreeMap<Integer, Deque<Order>> bids = new TreeMap<>(Comparator.reverseOrder());
    //Sell common.Order Book in ascending order
    private final TreeMap<Integer, Deque<Order>> asks = new TreeMap<>();

    private final List<String> trades = new ArrayList<>();

    @Override
    public void process(Order incoming) {

        if (incoming.side == Side.BUY) {
            match(incoming, asks, bids);
        } else {
            match(incoming, bids, asks);
        }
    }

    public List<String> getTrades() {
        return this.trades;
    }

    public TreeMap<Integer, Deque<Order>> getAsks() {
        return this.asks;
    }

    public TreeMap<Integer, Deque<Order>> getBids() {
        return this.bids;
    }


    private void match(Order newOrder, TreeMap<Integer, Deque<Order>> oppositeOrderBook, TreeMap<Integer, Deque<Order>> sameSideOrderBook) {
        while (newOrder.remainingQty > 0 && !oppositeOrderBook.isEmpty()) {
            // Price priority
            //1. get best price from opposite side book
            var best = oppositeOrderBook.firstEntry();
            var bestPrice = best.getKey();
            //2. Check whether price overlapped between new order and opposite book best price.
            //No action if price is not overlapped.
            if (!priceOverlapped(newOrder, bestPrice)) {
                break;
            }

            // Time Priority
            var subQueue = best.getValue();
            while (newOrder.remainingQty > 0 && !subQueue.isEmpty()) {
                // 3. loop through all sub orders in the same price level from the oldest order
                var oldestOrder = subQueue.peekFirst();
                // 4. get the lowest trade-able quantity
                var qty = Math.min(newOrder.remainingQty, oldestOrder.executableQuantity());
                // 5. update the quantity in relevant both side orders
                oldestOrder.consume(qty);
                newOrder.remainingQty -= qty;
                // log the trade
                trades.add(String.format("trade %s,%s,%d,%d", newOrder.orderId, oldestOrder.orderId, oldestOrder.price, qty));

                if (oldestOrder.isFilled()) {
                    // remove the oldest filled order in opposite side order book in current price level
                    subQueue.pollFirst();
                } else if (oldestOrder.icebergNeedRefresh()) {
                    // if iceberg order is not fully filled, but one tranche of iceberg is filled.
                    // 1. remove it from queue top
                    subQueue.pollFirst();
                    // 2. refresh the iceberg order internal quantity
                    oldestOrder.refreshIceberg();
                    // 3. add the order to the back of the queue in the same price level
                    // Kept the price priority, move this updated order to lower time priority
                    subQueue.offerLast(oldestOrder);
                }
            }
            if (subQueue.isEmpty()){
                // all orders in current price level are filled
                oppositeOrderBook.pollFirstEntry();
            }
        }
        if (newOrder.remainingQty>0){
            // the new order is not fully filled. need to update the remaining qty into the order book.
            addToBook(newOrder,sameSideOrderBook);
        }

    }

    private void addToBook(Order newOrder, TreeMap<Integer, Deque<Order>> sameSideOrderBook) {
        sameSideOrderBook.computeIfAbsent(newOrder.price, p-> new ArrayDeque<>())
                .offerLast(newOrder);
    }

    /**
     * @param newOrder
     * @param bestPrice
     * @return | common.Order In   | Match against   | Price condition              |
     * |------------|-----------------|------------------------------|
     * | Buy (bid)  | SELL (ask) book | sell book price <= buy price |
     * | Sell (ask) | BUY (bid) book  | buy book price >= sell price |
     */
    private boolean priceOverlapped(Order newOrder, Integer bestPrice) {
        return newOrder.side == Side.BUY ? bestPrice <= newOrder.price : bestPrice >= newOrder.price;
    }

}
