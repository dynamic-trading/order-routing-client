package dynamic.trading.order.routing.client.buffer;

import dynamic.trading.order.routing.client.Order;
import dynamic.trading.order.routing.client.constant.BootstrapType;
import dynamic.trading.order.routing.client.constant.MessageType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class Buffer {
    private final static PooledByteBufAllocator allocator;

    static {
        allocator = new PooledByteBufAllocator(true);
    }

    public static ByteBuf newByteBuf(int size){
        return allocator.ioBuffer(size);
    }

    public static ByteBuf connected(){
        ByteBuf byteBuf = newByteBuf(2);
        byteBuf.writeByte(MessageType.CONNECTED);
        return byteBuf;
    }

    public static ByteBuf bootstrap(final long correlationId,
                                    final short exchangeId,
                                    final long accountId,
                                    final BootstrapType bootstrapType){
        ByteBuf byteBuf = newByteBuf(20);

        byteBuf.writeByte(MessageType.BOOTSTRAP);
        byteBuf.writeLong(correlationId);

        byteBuf.writeByte(bootstrapType.value());
        byteBuf.writeShort(exchangeId);

        byteBuf.writeLong(accountId);
        return byteBuf;
    }

    public static ByteBuf logon(final long accountId){
        ByteBuf byteBuf = newByteBuf(9);
        byteBuf.writeByte(MessageType.LOG_ON);
        byteBuf.writeLong(accountId);
        return  byteBuf;
    }

    public static ByteBuf order(final Order order){
        int size = 39;

        ByteBuf byteBuf = newByteBuf(size);
        byteBuf.writeByte(MessageType.ROUTING);

        byteBuf.writeLong(order.getAccountId());
        byteBuf.writeLong(order.getOrderId());

        byteBuf.writeByte(order.getSide().value());
        byteBuf.writeByte(order.getExecutionType().value());

        byteBuf.writeShort(order.getExchangeId());
        byteBuf.writeShort(order.getInstrumentId());

        byteBuf.writeDouble(order.getOrderPx());
        byteBuf.writeDouble(order.getOrderQty());

        return byteBuf;
    }
}
