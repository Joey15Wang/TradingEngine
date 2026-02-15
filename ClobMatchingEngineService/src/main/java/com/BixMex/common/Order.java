package com.BixMex.common;

public class Order {
    public final String orderId;
    public final Side side;
    public final int price;
    public final int totalQty;
    public final int displayQty;
    public int remainingQty;
    int visibleQty;

    /**
     * @param orderId
     * @param side
     * @param price
     * @param qty
     * @param visibleQty - for iceberg order
     */
    public Order(String orderId, Side side, int price, int qty, int visibleQty) {
        this.orderId = orderId;
        this.side = side;
        this.price = price;
        this.totalQty = qty;
        this.displayQty = visibleQty > 0 ? Math.min(qty, visibleQty) : qty;
        this.visibleQty = displayQty;
        this.remainingQty = qty;
    }

    /**
     * constructor for regular order - not iceberg order
     *
     * @param orderId
     * @param side
     * @param price
     * @param qty
     */
    public Order(String orderId, Side side, int price, int qty) {
        this(orderId, side, price, qty, 0);
    }

    public int executableQuantity() {
        return Math.min(remainingQty, visibleQty);
    }

    public boolean isFilled() {
        return remainingQty == 0;
    }

    public void consume(int quantity) {
        remainingQty -= quantity;
        visibleQty -= quantity;
    }

    public boolean icebergNeedRefresh() {
        return visibleQty == 0 && remainingQty > 0;
    }

    public void refreshIceberg() {
        visibleQty = Math.min(displayQty, remainingQty);
    }

}
