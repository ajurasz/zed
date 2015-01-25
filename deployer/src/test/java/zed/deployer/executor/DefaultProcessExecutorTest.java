package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.NotFoundException;
import org.junit.After;
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
import zed.deployer.handlers.DeployableHandlers;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.DeploymentDescriptor;
import zed.deployer.manager.FileSystemDeployablesManager;
import zed.deployer.manager.ZedHome;

import java.io.File;

import static com.google.common.io.Files.createTempDir;
import static org.junit.Assume.assumeTrue;
import static org.springframework.boot.autoconfigure.spotifydocker.Dockers.isConnected;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SpotifyDockerAutoConfiguration.class, DefaultProcessExecutorTestConfiguration.class})
@IntegrationTest
public class DefaultProcessExecutorTest extends Assert {

    static String TEST_IMAGE = "ajurasz/busybox:latest";

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

        try {
            docker.removeImageCmd(TEST_IMAGE).withForce().exec();
        } catch (NotFoundException e) {
            // just ignore if not exist
        }
    }

    @After
    public void after() {
        try {
            docker.removeImageCmd(TEST_IMAGE).withForce().exec();
            if (pid != null) {
                docker.stopContainerCmd(pid).exec();
                docker.removeContainerCmd(pid).withForce().exec();
            }
        } catch (NotFoundException e) {
            // just ignore if not exist
        }
    }

    @Test
    public void shouldSupportDocker() {
        try {
            // Given
            DeploymentDescriptor descriptor = deployableManager.deploy("docker:" + TEST_IMAGE);

            // When
            pid = defaultProcessExecutor.start(descriptor.id());

            // Then
            assertNotNull(pid);
        } finally {
            if (pid != null) {
                docker.stopContainerCmd(pid).exec();
                docker.removeContainerCmd(pid);
            }
        }
    }

}

@SpringBootApplication
class DefaultProcessExecutorTestConfiguration {

    @Autowired
    DockerClient docker;

    @Bean
    DeployablesManager deploymentManager(ZedHome zedHome) {
        File workspace = createTempDir();
        return new FileSystemDeployablesManager(zedHome, workspace, DeployableHandlers.allDeployableHandlers(workspace, docker));
    }
}