package dynamic.trading.order.routing.client.constant;

public enum ResponseType {
    successful((byte)83),
    failure((byte)70),
    NULL_VAL((byte)0);

    private final byte value;

    private ResponseType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static ResponseType get(byte value) {
        switch(value) {
            case 0:
                return NULL_VAL;
            case 70:
                return failure;
            case 83:
                return successful;
            default:
                throw new IllegalArgumentException("Unknown value: " + value);
        }
    }
}
