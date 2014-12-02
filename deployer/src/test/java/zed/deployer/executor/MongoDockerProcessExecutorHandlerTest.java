package zed.deployer.executor;

import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.spotifydocker.SpotifyDockerAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import zed.deployer.DefaultStatusResolver;
import zed.deployer.DeploymentDescriptor;
import zed.deployer.DeploymentManager;
import zed.deployer.FileSystemDeploymentManager;
import zed.deployer.StatusResolver;

import static org.junit.Assume.assumeTrue;
import static org.springframework.boot.autoconfigure.spotifydocker.Dockers.isConnected;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SpotifyDockerAutoConfiguration.class, MongoDockerProcessExecutorHandlerTestConfiguration.class})
@IntegrationTest
public class MongoDockerProcessExecutorHandlerTest extends Assert {

    @Autowired
    DockerClient docker;

    @Autowired
    DeploymentManager deploymentManager;

    @Autowired
    MongoDockerProcessExecutorHandler mongoDockerProcessExecutorHandler;

    @Autowired
    StatusResolver statusResolver;

    DeploymentDescriptor descriptor;

    @Before
    public void before() {
        assumeTrue(isConnected(docker));
    }

    @Test
    public void shouldStartMongoProcess() throws DockerCertificateException, DockerException, InterruptedException {
        try {
            // Given
            deploymentManager.clear();
            descriptor = deploymentManager.deploy("mongodb:docker");

            // When
            String pid = mongoDockerProcessExecutorHandler.start(descriptor.id());
            descriptor = descriptor.pid(pid);

            // Then
            assertTrue(statusResolver.status(descriptor.id()));
        } finally {
            docker.stopContainer(descriptor.pid(), 15);
            assertFalse(statusResolver.status(descriptor.id()));
        }
    }

}

@Configuration
class MongoDockerProcessExecutorHandlerTestConfiguration {

    @Autowired
    DockerClient docker;

    @Bean
    DeploymentManager deploymentManager() {
        return new FileSystemDeploymentManager(docker);
    }

    @Bean
    MongoDockerProcessExecutorHandler mongoDockerProcessExecutorHandler() {
        return new MongoDockerProcessExecutorHandler(deploymentManager(), docker);
    }

    @Bean
    StatusResolver statusResolver() {
        return new DefaultStatusResolver(deploymentManager(), docker);
    }

}