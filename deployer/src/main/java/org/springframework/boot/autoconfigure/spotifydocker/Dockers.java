package org.springframework.boot.autoconfigure.spotifydocker;

import com.github.dockerjava.api.DockerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ProcessingException;

public final class Dockers {

    private static final Logger LOG = LoggerFactory.getLogger(Dockers.class);

    public static boolean isConnected(DockerClient docker) {
        try {
            docker.pingCmd().exec();
            return true;
        } catch (ProcessingException e) {
            LOG.info("Can't connect to the Docker server.", e);
            return false;
        }
    }

}
