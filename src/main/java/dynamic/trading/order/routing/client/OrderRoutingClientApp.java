package dynamic.trading.order.routing.client;

import dynamic.trading.order.routing.client.buffer.Buffer;
import dynamic.trading.order.routing.client.config.Configuration;
import dynamic.trading.order.routing.client.constant.*;
import dynamic.trading.order.routing.client.network.ResponseHandler;
import dynamic.trading.order.routing.client.network.WSBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

public class OrderRoutingClientApp {
    private static final int ACCOUNT_ID = 0;
    private static final long CORRELATION_ID_1 = 1111L;

    private static final short BITMEX = 1000;
    private static final short INSTRUMENT_ID = 7129;

    private static final long ORDER_ID = 1;

    public static void main(String[] args) {
        try{
            Configuration.getInstance().load();
            WSBootstrap wsBootstrap = new WSBootstrap();
            wsBootstrap.connect(responseHandler());
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private static ResponseHandler responseHandler(){
        return (byteBuf, ctx) -> {
            byte msgType = byteBuf.getByte(0);
            switch (msgType){
                case MessageType.CONNECTED:
                    logonRequest(ctx);
                    break;
                case MessageType.LOG_ON:
                    logonResponse(byteBuf);
                    bootstrapRequest(ctx);
                    break;
                case MessageType.LOG_OFF:
                    System.out.println("Logged off");
                    break;
                case MessageType.BOOTSTRAP:
                    bootstrapResponse(byteBuf);
                    newOrder(ctx);
                    break;
                case MessageType.ROUTING:
                    orderResponse(byteBuf, ctx);
                    break;
                case MessageType.MALFORMED_MESSAGE_CODE:
                    System.err.println("Malformed message received");
                    break;
                case MessageType.SERVICE_NOT_AVAILABLE_CODE:
                    System.err.println("Routing service is not available");
                    break;
            }
        };
    }

    private static void newOrder(final ChannelHandlerContext ctx){
        Order order = new Order(ACCOUNT_ID,
                ORDER_ID,
                Side.Buy,
                BITMEX,
                INSTRUMENT_ID,
                20_000,
                100,
                ExecutionType.newOrder);
        ByteBuf orderBuffer = Buffer.order(order);
        ctx.writeAndFlush(new BinaryWebSocketFrame(orderBuffer));
    }

    private static void orderResponse(final ByteBuf byteBuf, final ChannelHandlerContext ctx){
        int offset = Byte.BYTES;

        long accountId = byteBuf.getLong(offset);
        offset += Long.BYTES;

        long orderId = byteBuf.getLong(offset);
        offset += Long.BYTES;

        short exchangeId = byteBuf.getShort(offset);
        offset += Short.BYTES;

        short instrumentId = byteBuf.getShort(offset);
        offset += Short.BYTES;

        byte executionType = byteBuf.getByte(offset);
        offset += Byte.BYTES;

        double tradePrice = byteBuf.getDouble(offset);
        offset += Double.BYTES;

        double tradeQuantity = byteBuf.getDouble(offset);
        offset += Double.BYTES;

        if (ExecutionType.newOrder.value() == executionType){
            System.out.println("New Order Received");
            Order order = new Order(accountId, orderId, Side.Buy, exchangeId, instrumentId, 0, 0, ExecutionType.cancelOrder);
            ByteBuf orderBuffer = Buffer.order(order);
            ctx.writeAndFlush(new BinaryWebSocketFrame(orderBuffer));
        }else if (ExecutionType.cancelOrder.value() == executionType){
            System.out.println("Cancel Order Received");
        }else if (ExecutionType.fullFill.value() == executionType){
            System.out.println("Order fully filled");
        }else if (ExecutionType.partialFill.value() == executionType){
            System.out.println("Order partially filled");
        }else if (ExecutionType.rejected.value() == executionType){
            System.out.println("Order Rejected");
        }
    }

    private static void logonRequest(final ChannelHandlerContext ctx){
        ByteBuf logonBuffer = Buffer.logon(ACCOUNT_ID);
        ctx.writeAndFlush(new BinaryWebSocketFrame(logonBuffer));
    }

    private static void logonResponse(final ByteBuf byteBuf){
        int offset = Byte.BYTES;
        long accountId = byteBuf.getLong(offset);

        offset += Long.BYTES;
        byte responseType = byteBuf.getByte(offset);

        if (responseType != ResponseType.successful.value()){
            System.err.println("Failed to log on with user account id " + accountId);
            System.exit(1);
        }
    }

    private static void bootstrapRequest(final ChannelHandlerContext ctx){
        ByteBuf bootstrapAddBuffer = Buffer.bootstrap(CORRELATION_ID_1, BITMEX, ACCOUNT_ID, BootstrapType.add);
        ctx.writeAndFlush(new BinaryWebSocketFrame(bootstrapAddBuffer));
    }

    private static void bootstrapResponse(final ByteBuf byteBuf){
        int offset = Byte.BYTES;

        long accountId = byteBuf.getLong(offset);
        offset += Long.BYTES;

        BootstrapType bootstrapType = BootstrapType.get(byteBuf.getByte(offset));
        offset += Byte.BYTES;

        short exchangeId = byteBuf.getShort(offset);
        offset += Short.BYTES;

        long correlationId = byteBuf.getLong(offset);

        if (BootstrapType.add != bootstrapType){
            System.err.printf("Failed to bootstrap exchange %d for accountId %d correlationId %n" + exchangeId, accountId, correlationId);
            System.exit(1);
        }
    }
}
