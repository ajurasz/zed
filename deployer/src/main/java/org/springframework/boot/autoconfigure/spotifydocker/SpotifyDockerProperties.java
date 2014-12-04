package org.springframework.boot.autoconfigure.spotifydocker;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.spotifydocker")
public class SpotifyDockerProperties {

    private String uri;

    private int readTimeoutMilis = 30 * 1000;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getReadTimeoutMilis() {
        return readTimeoutMilis;
    }

    public void setReadTimeoutMilis(int readTimeoutMilis) {
        this.readTimeoutMilis = readTimeoutMilis;
    }

}
