package dynamic.trading.order.routing.client.constant;

public enum Side {
    Buy((byte)49),
    Sell((byte)50),
    NULL_VAL((byte)0);

    private final byte value;

    private Side(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static Side get(byte value) {
        switch(value) {
            case 0:
                return NULL_VAL;
            case 49:
                return Buy;
            case 50:
                return Sell;
            default:
                throw new IllegalArgumentException("Unknown value: " + value);
        }
    }
}
