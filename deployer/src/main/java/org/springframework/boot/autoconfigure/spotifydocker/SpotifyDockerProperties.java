package org.springframework.boot.autoconfigure.spotifydocker;

import org.springframework.boot.context.properties.ConfigurationProperties;

import static java.util.concurrent.TimeUnit.SECONDS;

@ConfigurationProperties(prefix = "spring.spotifydocker")
public class SpotifyDockerProperties {

    private String uri;

    private int connectionPoolSize = 20;

    private long connectTimeoutMilis = SECONDS.toMillis(5);

    private long readTimeoutMilis = SECONDS.toMillis(30);

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getConnectTimeoutMilis() {
        return connectTimeoutMilis;
    }

    public void setConnectTimeoutMilis(long connectTimeoutMilis) {
        this.connectTimeoutMilis = connectTimeoutMilis;
    }

    public long getReadTimeoutMilis() {
        return readTimeoutMilis;
    }

    public void setReadTimeoutMilis(long readTimeoutMilis) {
        this.readTimeoutMilis = readTimeoutMilis;
    }

}
