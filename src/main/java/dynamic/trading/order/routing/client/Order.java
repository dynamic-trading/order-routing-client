package dynamic.trading.order.routing.client;

import dynamic.trading.order.routing.client.constant.ExecutionType;
import dynamic.trading.order.routing.client.constant.Side;

public class Order {
    private final long accountId;
    private final long orderId;

    private final Side side;
    private final ExecutionType executionType;

    private final short exchangeId;
    private final short instrumentId;

    private final double orderPx;
    private final double orderQty;

    public Order(final long accountId,
                 final long orderId,
                 final Side side,
                 final short exchangeId,
                 final short instrumentId,
                 final double orderPx,
                 final double orderQty,
                 final ExecutionType executionType) {
        this.accountId = accountId;
        this.orderId = orderId;

        this.side = side;
        this.executionType = executionType;

        this.exchangeId = exchangeId;
        this.instrumentId = instrumentId;

        this.orderPx = orderPx;
        this.orderQty = orderQty;
    }

    public long getAccountId() { return this.accountId; }

    public long getOrderId() {
        return this.orderId;
    }

    public Side getSide() {
        return this.side;
    }

    public short getExchangeId() {
        return this.exchangeId;
    }

    public short getInstrumentId() {
        return this.instrumentId;
    }

    public double getOrderPx() {
        return this.orderPx;
    }

    public double getOrderQty() {
        return this.orderQty;
    }

    public ExecutionType getExecutionType() {
        return this.executionType;
    }
}
