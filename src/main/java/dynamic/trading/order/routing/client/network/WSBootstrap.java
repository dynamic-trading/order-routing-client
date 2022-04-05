package dynamic.trading.order.routing.client.network;

import dynamic.trading.order.routing.client.config.Configuration;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import java.net.URI;

public class WSBootstrap {
    private final int PORT = Configuration.getInstance()
            .getConfig()
            .getPort();

    private final String HOST = Configuration.getInstance()
            .getConfig()
            .getHost();

    private final URI uri;
    private ChannelFuture channel;

    public WSBootstrap() {
        try{
            String url = System.getProperty("url", "ws://" + HOST + "/trading");
            this.uri = new URI(url);
        }catch (Exception ex){
            ex.printStackTrace();
            System.err.println("Failed to create DCBootstrap");
            System.exit(1);
            throw new RuntimeException();
        }
    }

    public void connect(final ResponseHandler responseHandler){
        EventLoopGroup eventLoop = Epoll.isAvailable() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        this.connect(responseHandler, eventLoop);
    }

    public void connect(final ResponseHandler responseHandler, final EventLoopGroup eventLoop){
        Class channelClass = Epoll.isAvailable() ? EpollSocketChannel.class :
                NioSocketChannel.class;

        System.out.printf("Using Channel: %s %n", channelClass.toString());
        WebSocketClientHandshaker webSocketClientHandshaker = WebSocketClientHandshakerFactory.newHandshaker(
                uri,
                WebSocketVersion.V13,
                null,
                false,
                new DefaultHttpHeaders(),
                1655360);

        Bootstrap bootstrap = new Bootstrap();
        WSHandler wsHandler = new WSHandler(responseHandler, webSocketClientHandshaker);

        bootstrap.group(eventLoop)
                .channel(channelClass)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new HttpClientCodec(), new HttpObjectAggregator(8192), wsHandler);
                    }
                });

        this.channel = bootstrap.connect(HOST, PORT).addListener((ChannelFutureListener) future -> {
            if (!future.isSuccess()) {
                System.err.println("Failed to start connection. Shutting down application");
                System.exit(1);
            }
        });

        wsHandler.handshakeFuture();
    }

    public ChannelFuture disconnect() {
        return this.channel.channel().close();
    }

    public ChannelFuture getChannel() {
        return this.channel;
    }
}
