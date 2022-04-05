package dynamic.trading.order.routing.client.config;

public class Config {
    private final String host;
    private final int port;

    public Config(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }
}
