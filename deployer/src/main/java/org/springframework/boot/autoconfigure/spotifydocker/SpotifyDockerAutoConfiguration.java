package org.springframework.boot.autoconfigure.spotifydocker;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(DockerClient.class)
@EnableConfigurationProperties(SpotifyDockerProperties.class)
public class SpotifyDockerAutoConfiguration {

    @Bean
    public DockerClient docker(SpotifyDockerProperties dockerProperties) throws DockerCertificateException {
        DefaultDockerClient.Builder dockerBuilder = DefaultDockerClient.fromEnv();
        if (dockerProperties.getUri() != null) {
            dockerBuilder.uri(dockerProperties.getUri());
        }
        dockerBuilder.
                connectionPoolSize(dockerProperties.getConnectionPoolSize()).
                connectTimeoutMillis(dockerProperties.getConnectTimeoutMilis()).
                readTimeoutMillis(dockerProperties.getReadTimeoutMilis());
        return dockerBuilder.build();
    }

}
