package org.springframework.boot.autoconfigure.spotifydocker;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Dockers {

    private static final Logger LOG = LoggerFactory.getLogger(Dockers.class);

    public static boolean isConnected(DockerClient docker) {
        try {
            return docker.ping().equals("OK");
        } catch (InterruptedException | DockerException e) {
            LOG.info("Can't connect to the Docker server.", e);
            return false;
        }
    }

}
