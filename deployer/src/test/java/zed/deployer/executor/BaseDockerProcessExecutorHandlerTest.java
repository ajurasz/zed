package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.spotifydocker.SpotifyDockerAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import zed.deployer.DefaultStatusResolver;
import zed.deployer.StatusResolver;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.DeploymentDescriptor;
import zed.deployer.manager.FileSystemDeployablesManager;
import zed.deployer.manager.ZedHome;

import java.io.File;

import static com.google.common.io.Files.createTempDir;
import static org.junit.Assume.assumeTrue;
import static org.springframework.boot.autoconfigure.spotifydocker.Dockers.isConnected;
import static zed.deployer.handlers.DeployableHandlers.allDeployableHandlers;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SpotifyDockerAutoConfiguration.class, BaseDockerProcessExecutorHandlerTestConfiguration.class})
@IntegrationTest
public class BaseDockerProcessExecutorHandlerTest extends Assert {

    static String TEST_IMAGE = "ajurasz/busybox:latest";

    @Autowired
    DockerClient docker;

    @Autowired
    DeployablesManager deployableManager;

    @Autowired
    BaseDockerProcessExecutorHandler baseDockerProcessExecutorHandler;

    @Autowired
    StatusResolver statusResolver;

    DeploymentDescriptor descriptor;

    @Before
    public void before() {
        assumeTrue(isConnected(docker));
    }

    @Test
    public void shouldStartDockerProcess() {
        try {
            // Given
            deployableManager.clear();
            descriptor = deployableManager.deploy("docker:" + TEST_IMAGE);

            // When
            String pid = baseDockerProcessExecutorHandler.start(descriptor.id());
            descriptor = descriptor.pid(pid);

            // Then
            assertTrue(statusResolver.status(descriptor.id()));
        } finally {
            docker.stopContainerCmd(descriptor.pid()).exec();
            docker.removeContainerCmd(descriptor.pid()).withForce().exec();
            assertFalse(statusResolver.status(descriptor.id()));
        }
    }

}

@SpringBootApplication
class BaseDockerProcessExecutorHandlerTestConfiguration {

    @Autowired
    DockerClient docker;

    @Bean
    DeployablesManager deploymentManager(ZedHome zedHome) {
        File workspace = createTempDir();
        return new FileSystemDeployablesManager(zedHome, workspace, allDeployableHandlers(workspace, docker));
    }

    @Bean
    BaseDockerProcessExecutorHandler baseDockerProcessExecutorHandler(DeployablesManager deployablesManager) {
        return new BaseDockerProcessExecutorHandler(deployablesManager, docker);
    }

    @Bean
    StatusResolver statusResolver(DeployablesManager deployablesManager) {
        return new DefaultStatusResolver(deployablesManager, docker);
    }

}