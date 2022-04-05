package dynamic.trading.order.routing.client.network;

import dynamic.trading.order.routing.client.buffer.Buffer;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;

public class WSHandler extends SimpleChannelInboundHandler<Object> {
    private ChannelPromise handshakeFuture;
    private final ResponseHandler responseHandler;
    private final WebSocketClientHandshaker handshaker;

    public WSHandler(final ResponseHandler responseHandler, final WebSocketClientHandshaker handshaker) {
        this.handshaker = handshaker;
        this.responseHandler = responseHandler;
    }

    public ChannelFuture handshakeFuture() {
        return this.handshakeFuture;
    }

    @Override
    public void handlerAdded(final ChannelHandlerContext ctx) {
        this.handshakeFuture = ctx.newPromise();
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        this.handshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(final ChannelHandlerContext ctx) {
        System.out.println("Channel is inactive");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        final Channel ch = ctx.channel();
        if (!this.handshaker.isHandshakeComplete()) {
            this.handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            this.handshakeFuture.setSuccess();
            this.responseHandler.onResponse(Buffer.connected(), ctx);
            return;
        }
        final WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof BinaryWebSocketFrame) {
            this.responseHandler.onResponse(frame.content(), ctx);
        }
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        cause.printStackTrace();
        if (!this.handshakeFuture.isDone()) {
            this.handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
}
