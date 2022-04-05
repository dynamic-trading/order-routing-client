package dynamic.trading.order.routing.client.constant;

public class MessageType {
    public static final byte CONNECTED = 0;

    public static final byte LOG_ON = 1;
    public static final byte LOG_OFF = 2;

    public static final byte BOOTSTRAP = 3;
    public static final byte ROUTING = 4;

    public static final byte MALFORMED_MESSAGE_CODE = 5;
    public static final byte SERVICE_NOT_AVAILABLE_CODE = 6;
}
