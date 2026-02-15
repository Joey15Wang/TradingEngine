package com.BixMex.orderBook;

import com.BixMex.common.Order;

import java.util.Deque;
import java.util.List;
import java.util.TreeMap;

public interface OrderBook {
    void process(Order order);
}
