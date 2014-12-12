package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
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
import zed.deployer.StatusResolver;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.DeploymentDescriptor;
import zed.deployer.manager.FileSystemDeployablesManager;

import static org.junit.Assume.assumeTrue;
import static org.springframework.boot.autoconfigure.spotifydocker.Dockers.isConnected;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SpotifyDockerAutoConfiguration.class, MongoDockerProcessExecutorHandlerTestConfiguration.class})
@IntegrationTest
public class MongoDockerProcessExecutorHandlerTest extends Assert {

    @Autowired
    DockerClient docker;

    @Autowired
    DeployablesManager deployableManager;

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
    public void shouldStartMongoProcess() {
        try {
            // Given
            deployableManager.clear();
            descriptor = deployableManager.deploy("mongodb:docker");

            // When
            String pid = mongoDockerProcessExecutorHandler.start(descriptor.id());
            descriptor = descriptor.pid(pid);

            // Then
            assertTrue(statusResolver.status(descriptor.id()));
        } finally {
            docker.stopContainerCmd(descriptor.pid()).exec();
            assertFalse(statusResolver.status(descriptor.id()));
        }
    }

}

@Configuration
class MongoDockerProcessExecutorHandlerTestConfiguration {

    @Autowired
    DockerClient docker;

    @Bean
    DeployablesManager deploymentManager() {
        return new FileSystemDeployablesManager(docker);
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