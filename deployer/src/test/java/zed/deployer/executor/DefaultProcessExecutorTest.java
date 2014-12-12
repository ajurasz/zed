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
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.DeploymentDescriptor;
import zed.deployer.manager.FileSystemDeployablesManager;

import static org.junit.Assume.assumeTrue;
import static org.springframework.boot.autoconfigure.spotifydocker.Dockers.isConnected;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SpotifyDockerAutoConfiguration.class, DefaultProcessExecutorTestConfiguration.class})
@IntegrationTest
public class DefaultProcessExecutorTest extends Assert {

    @Autowired
    DockerClient docker;

    @Autowired
    DeployablesManager deployableManager;

    @Autowired
    ProcessExecutor defaultProcessExecutor;

    String pid;

    @Before
    public void before() {
        assumeTrue(isConnected(docker));
    }

    @Test
    public void shouldSupportMongoDocker() {
        try {
            // Given
            DeploymentDescriptor descriptor = deployableManager.deploy("mongodb:docker");

            // When
            pid = defaultProcessExecutor.start(descriptor.id());

            // Then
            assertNotNull(pid);
        } finally {
            if (pid != null) {
                docker.stopContainerCmd(pid).exec();
            }
        }
    }

}

@Configuration
class DefaultProcessExecutorTestConfiguration {

    @Autowired
    DockerClient docker;

    @Bean
    DeployablesManager deploymentManager() {
        return new FileSystemDeployablesManager(docker);
    }

    @Bean
    ProcessExecutor processExecutor() {
        return new DefaultProcessExecutor(deploymentManager(), docker);
    }

}