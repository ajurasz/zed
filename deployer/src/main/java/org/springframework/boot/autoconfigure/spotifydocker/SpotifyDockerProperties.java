package org.springframework.boot.autoconfigure.spotifydocker;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.spotifydocker")
public class SpotifyDockerProperties {

    private int connectionPoolSize = 20;

    public int getConnectionPoolSize() {
        return connectionPoolSize;
    }

    public void setConnectionPoolSize(int connectionPoolSize) {
        this.connectionPoolSize = connectionPoolSize;
    }

}
