package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
import org.apache.commons.lang3.StringUtils;
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
import java.util.Optional;

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

    Optional<String> pid;

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
            assertNotNull(pid.get());
        } finally {
            if (pid != null) {
                docker().stopContainerCmd(pid.get()).exec();
                docker().removeContainerCmd(pid.get()).withForce().exec();
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
            dockerTester.registerShutdown(pid.get());

            // Then
            assertTrue(isNotEmpty(pid.get()));
            assertTrue(docker().inspectContainerCmd(pid.get()).exec().getState().isRunning());
            assertTrue(docker().inspectContainerCmd(pid.get()).exec().getConfig().getImage().contains("dockerfile/mongodb"));
            assertThat(volumeBinds(docker(), pid.get()), hasVolume("/data/db", "/var/zed/mongodb/default"));
        } finally {
            if (pid.isPresent() && StringUtils.isNotEmpty(pid.get())) {
                docker().stopContainerCmd(pid.get()).exec();
                docker().removeContainerCmd(pid.get()).withForce().exec();
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
            Optional<String> pid2 = defaultProcessExecutor.start(descriptor.id());

            // Then
            assertNotNull(pid.get());
            assertFalse(pid2.isPresent());
        } finally {
            if (pid != null) {
                docker().stopContainerCmd(pid.get()).exec();
                docker().removeContainerCmd(pid.get()).withForce().exec();
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