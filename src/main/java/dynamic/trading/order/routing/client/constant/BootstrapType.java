package dynamic.trading.order.routing.client.constant;

public enum BootstrapType {
    add((byte)65),
    remove((byte)82),
    NULL_VAL((byte)0);

    private final byte value;

    private BootstrapType(byte value) {
        this.value = value;
    }

    public byte value() {
        return this.value;
    }

    public static BootstrapType get(byte value) {
        switch(value) {
            case 0:
                return NULL_VAL;
            case 65:
                return add;
            case 82:
                return remove;
            default:
                throw new IllegalArgumentException("Unknown value: " + value);
        }
    }
}

