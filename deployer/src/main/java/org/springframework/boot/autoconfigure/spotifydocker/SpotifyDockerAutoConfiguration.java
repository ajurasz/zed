package org.springframework.boot.autoconfigure.spotifydocker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(DockerClient.class)
@EnableConfigurationProperties(SpotifyDockerProperties.class)
public class SpotifyDockerAutoConfiguration {

    @Bean
    public DockerClient docker(SpotifyDockerProperties dockerProperties) {
        DockerClientConfig.DockerClientConfigBuilder config = DockerClientConfig.createDefaultConfigBuilder();
        config.withVersion(dockerProperties.getVersion());
        if (dockerProperties.getUri() != null) {
            config.withUri(dockerProperties.getUri());
        }
        config.withReadTimeout(dockerProperties.getReadTimeoutMilis());
        return DockerClientBuilder.getInstance(config).build();
    }

}
