package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
import org.junit.Before;
import org.junit.BeforeClass;
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
import zed.deployer.manager.DeployableDescriptor;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.FileSystemDeployablesManager;
import zed.deployer.manager.ZedHome;
import zed.dockerunit.BaseDockerTest;

import java.io.File;

import static com.google.common.io.Files.createTempDir;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;
import static org.junit.Assume.assumeTrue;
import static org.springframework.boot.autoconfigure.spotifydocker.Dockers.isConnected;
import static zed.dockerunit.VolumeDockerInspectMatcher.hasVolume;
import static zed.dockerunit.VolumeDockerInspectMatcher.volumeBinds;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SpotifyDockerAutoConfiguration.class, DefaultProcessExecutorTestConfiguration.class})
@IntegrationTest
public class DefaultProcessExecutorTest extends BaseDockerTest {

    static String TEST_IMAGE = "ajurasz/busybox:latest";

    @Autowired
    DeployablesManager deployableManager;

    @Autowired
    ProcessExecutor defaultProcessExecutor;

    String pid;

    @BeforeClass
    public static void beforeClass() {
        initializeDocker();
    }

    @Before
    public void before() {
        assumeTrue(isConnected(dockerTester.docker()));
    }

    @Test
    public void shouldSupportDocker() {
        try {
            // Given
            DeployableDescriptor descriptor = deployableManager.deploy("docker:" + TEST_IMAGE);

            // When
            pid = defaultProcessExecutor.start(descriptor.id());

            // Then
            assertNotNull(pid);
        } finally {
            if (pid != null) {
                docker().stopContainerCmd(pid).exec();
                docker().removeContainerCmd(pid).withForce().exec();
            }
        }
    }

    @Test
    public void shouldRunDefaultDockerizedMongo() {
        try {
            // Given
            DeployableDescriptor descriptor = deployableManager.deploy("mongodb:docker");

            // When
            pid = defaultProcessExecutor.start(descriptor.id());
            dockerTester.registerShutdown(pid);

            // Then
            assertTrue(isNotEmpty(pid));
            assertTrue(docker().inspectContainerCmd(pid).exec().getState().isRunning());
            assertTrue(docker().inspectContainerCmd(pid).exec().getConfig().getImage().contains("dockerfile/mongodb"));
            assertThat(volumeBinds(docker(), pid), hasVolume("/data/db", "/var/zed/mongodb/default"));
        } finally {
            if (pid != null) {
                docker().stopContainerCmd(pid).exec();
                docker().removeContainerCmd(pid).withForce().exec();
            }
        }
    }

    @Test
    public void shouldStartDeployableOnce() {
        try {
            // Given
            DeployableDescriptor descriptor = deployableManager.deploy("docker:" + TEST_IMAGE);
            pid = defaultProcessExecutor.start(descriptor.id());

            // Then
            String pid2 = defaultProcessExecutor.start(descriptor.id());

            // Then
            assertNotNull(pid);
            assertEquals(pid, pid2);
            assertEquals(deployableManager.list().size(), 1);
        } finally {
            if (pid != null) {
                docker().stopContainerCmd(pid).exec();
                docker().removeContainerCmd(pid).withForce().exec();
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