package org.springframework.boot.autoconfigure.spotifydocker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(DockerClient.class)
@EnableConfigurationProperties(SpotifyDockerProperties.class)
public class SpotifyDockerAutoConfiguration {

    @Autowired
    private SpotifyDockerProperties dockerProperties;

    @Bean
    public DockerClient docker() throws DockerCertificateException {
        return DefaultDockerClient.fromEnv().
                connectionPoolSize(dockerProperties.getConnectionPoolSize()).
                build();
    }

}
