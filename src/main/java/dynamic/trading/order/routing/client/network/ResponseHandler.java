package dynamic.trading.order.routing.client.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

@FunctionalInterface
public interface ResponseHandler {
    void onResponse(final ByteBuf byteBuf, final ChannelHandlerContext ctx);
}
