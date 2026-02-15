package com.BixMex;

import com.BixMex.common.Order;
import com.BixMex.orderBook.OrderBookImpl;
import com.BixMex.common.Side;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Exchange {
    public static void main(String[] args) throws Exception {
        System.out.println("Exchange is initialised.");
        var bufferReader = new BufferedReader(new InputStreamReader(System.in));
        var orderBook = new OrderBookImpl();
        var printService = new PrintService();
        String line;

        while ((line = bufferReader.readLine()) != null && !line.isEmpty()) {
            var p = line.split(",");
            var orderId = p[0];
            var side = p[1].equals("B")? Side.BUY:Side.SELL;
            var price = Integer.parseInt(p[2]);
            var qty = Integer.parseInt(p[3]);
            var visibleQty = p.length == 5? Integer.parseInt(p[4]):qty;

            var order = new Order(orderId,side,price,qty,visibleQty);
            orderBook.process(order);
        }
        printService.printTrades(orderBook.getTrades());
        printService.printCentralLimitOrderBook(orderBook.getBids(),orderBook.getAsks());
    }
}
