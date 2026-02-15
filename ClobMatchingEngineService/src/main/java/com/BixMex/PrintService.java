package com.BixMex;

import com.BixMex.common.Order;
import com.BixMex.common.Side;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.TreeMap;

public class PrintService {
    public void printTrades(List<String> trades) {
        trades.forEach(System.out::println);
    }

    public void printCentralLimitOrderBook(TreeMap<Integer, Deque<Order>> bids, TreeMap<Integer, Deque<Order>> asks) {
        var bidIt = flatten(bids).iterator();
        var askIt = flatten(asks).iterator();

        while (bidIt.hasNext() || askIt.hasNext()) {
            var bidStr = bidIt.hasNext() ? formatQtyPrice(bidIt.next(), Side.BUY) : "                 |";
            var askStr = askIt.hasNext() ? formatQtyPrice(askIt.next(), Side.SELL) : "";
            System.out.println(bidStr + askStr);
        }
    }

    private Iterable<Order> flatten(TreeMap<Integer, Deque<Order>> orderBook) {
        var result = new ArrayList<Order>();
        for (var order : orderBook.values()) {
            result.addAll(order);
        }
        return result;
    }

    private String formatQtyPrice(Order order, Side side) {
        return side == Side.BUY ? String.format("%,9d %6d |", order.executableQuantity(), order.price) : String.format(" %6d %,9d", order.price, order.executableQuantity());
    }
}
