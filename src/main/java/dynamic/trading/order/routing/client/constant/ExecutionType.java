package dynamic.trading.order.routing.client.constant;

public enum ExecutionType {
    newOrder((byte)78),
    replaceOrder((byte)82),
    cancelOrder((byte)67),
    partialFill((byte)80),
    fullFill((byte)70),
    rejected((byte)74),
    NULL_VAL((byte)0);

    private final byte value;

    private ExecutionType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static ExecutionType get(byte value) {
        switch(value) {
            case 0:
                return NULL_VAL;
            case 67:
                return cancelOrder;
            case 70:
                return fullFill;
            case 74:
                return rejected;
            case 78:
                return newOrder;
            case 80:
                return partialFill;
            case 82:
                return replaceOrder;
            default:
                throw new IllegalArgumentException("Unknown value: " + value);
        }
    }
}

