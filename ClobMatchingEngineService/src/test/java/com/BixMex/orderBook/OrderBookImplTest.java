package com.BixMex.orderBook;

import com.BixMex.common.Order;
import com.BixMex.common.Side;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class OrderBookImplTest {
    private OrderBookImpl orderBook = new OrderBookImpl();
    private Order order1 = new Order("10000", Side.BUY, 98, 25500);
    private Order order2 = new Order("10005", Side.SELL, 101, 20000);
    private Order order3 = new Order("10002", Side.SELL, 100, 10000);
    private Order order4 = new Order("10001", Side.SELL, 100, 7500);
    private Order order5 = new Order("10003", Side.BUY, 99, 50000);
    private Order orderIce1 = new Order("ice1", Side.BUY, 100, 100000, 10000);
    private Order order6 = new Order("10007", Side.SELL, 100, 500);
    private Order order7 = new Order("10004", Side.SELL, 103, 100);
    private Order order8 = new Order("10006", Side.BUY, 105, 16000);
    private Order order9 = new Order("100051", Side.SELL, 105, 20000);

    @Test
    void processRegularTradesWithTradeBuyOrderWithExpectSellOrderBook() {
        orderBook.process(order1);
        orderBook.process(order9);
        orderBook.process(order6);
        orderBook.process(order3);
        orderBook.process(order5);
        orderBook.process(order7);
        orderBook.process(order8);
        assertThat(orderBook.getBids().size()).isEqualTo(2);
        assertThat(orderBook.getAsks().size()).isEqualTo(1);
        assertThat(orderBook.getAsks().firstEntry().getKey()).isEqualTo(105);
        assertThat(orderBook.getAsks().firstEntry().getValue().size()).isEqualTo(1);
        assertThat(orderBook.getAsks().firstEntry().getValue().getFirst().remainingQty).isEqualTo(14600);
        assertThat(orderBook.getTrades().size()).isEqualTo(4);
    }

    @Test
    void processAnIceBergBuyOrderWithExpectSellOrderBook() {
        orderBook.process(order1);
        orderBook.process(order2);
        orderBook.process(order3);
        orderBook.process(order4);
        orderBook.process(order5);
        orderBook.process(orderIce1);
        assertThat(orderBook.getBids().size()).isEqualTo(3);
        assertThat(orderBook.getAsks().size()).isEqualTo(1);
        assertThat(orderBook.getAsks().firstEntry().getKey()).isEqualTo(101);
        assertThat(orderBook.getAsks().firstEntry().getValue().size()).isEqualTo(1);
        assertThat(orderBook.getAsks().firstEntry().getValue().getFirst().remainingQty).isEqualTo(20000);
        assertThat(orderBook.getTrades().size()).isEqualTo(2);
    }
}