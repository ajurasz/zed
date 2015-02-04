package zed.deployer;

import com.github.dockerjava.api.DockerClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.spotifydocker.SpotifyDockerAutoConfiguration;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import zed.deployer.manager.DeployableDescriptor;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.FileSystemDeployablesManager;
import zed.deployer.manager.ZedHome;
import zed.utils.Mavens;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.google.common.io.Files.createTempDir;
import static org.junit.Assume.assumeTrue;
import static org.springframework.boot.autoconfigure.spotifydocker.Dockers.isConnected;
import static zed.deployer.handlers.DeployableHandlers.allDeployableHandlers;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SpotifyDockerAutoConfiguration.class, FileSystemDeploymentManagerTestConfiguration.class})
@IntegrationTest
public class FileSystemDeployablesManagerTest extends Assert {

    static String TEST_IMAGE = "ajurasz/busybox:latest";

    @Autowired
    DockerClient docker;

    @Autowired
    FileSystemDeployablesManager deploymentManager;

    @Before
    public void before() {
        deploymentManager.clear();
    }

    // Tests

    @Test
    public void shouldDeployFatJar() {
        // When
        deploymentManager.deploy("fatjar:mvn:com.google.guava/guava/18.0");

        // Then
        assertTrue(Arrays.asList(deploymentManager.workspace().list()).contains("guava-18.0.jar"));
    }

    @Test
    public void shouldDeployFatWar() {
        // When
        deploymentManager.deploy("fatjar:mvn:com.github.zed-platform/zed-shell/0.0.6/war");

        // Then
        assertTrue(Arrays.asList(deploymentManager.workspace().list()).contains("zed-shell-0.0.6.war"));
    }

    @Ignore("This test is not release-friendly.")
    @Test
    public void shouldDeploySnapshot() throws IOException {
        // Given
        String projectVersion = Mavens.artifactVersion("com.github.zed-platform", "zed-deployer");

        // When
        deploymentManager.deploy("fatjar:mvn:com.github.zed-platform/zed-utils/" + projectVersion);

        // Then
        assertTrue(Arrays.asList(deploymentManager.workspace().list()).contains("zed-utils-" + projectVersion + ".jar"));
    }

    @Test
    public void shouldWriteUriIntoFatJarMavenDescriptor() throws IOException {
        // When
        DeployableDescriptor deployableDescriptor = deploymentManager.deploy("fatjar:mvn:com.google.guava/guava/18.0");

        // Then
        Properties savedDescriptor = new Properties();
        savedDescriptor.load(new FileInputStream(new File(deploymentManager.workspace(), deployableDescriptor.id() + ".deploy")));
        assertEquals(deployableDescriptor.uri(), savedDescriptor.getProperty("uri"));
    }

    @Test
    public void shouldListDeploymentDescriptors() {
        // Given
        DeployableDescriptor descriptor = deploymentManager.deploy("fatjar:mvn:com.google.guava/guava/18.0");

        // When
        List<DeployableDescriptor> descriptors = deploymentManager.list();

        // Then
        assertEquals(1, descriptors.size());
        assertEquals(descriptor.id(), descriptors.get(0).id());
        assertEquals("fatjar:mvn:com.google.guava/guava/18.0", descriptors.get(0).uri());
    }

    @Test
    public void shouldWriteUriIntoDockerDescriptor() throws IOException {
        // When
        assumeTrue(isConnected(docker));
        DeployableDescriptor deployableDescriptor = deploymentManager.deploy("docker:" + TEST_IMAGE);

        // Then
        Properties savedDescriptor = new Properties();
        savedDescriptor.load(new FileInputStream(new File(deploymentManager.workspace(), deployableDescriptor.id() + ".deploy")));
        assertEquals(deployableDescriptor.uri(), savedDescriptor.getProperty("uri"));
    }

    @Test
    public void shouldWriteIdIntoDockerDescriptor() throws IOException {
        // Given
        assumeTrue(isConnected(docker));

        // When
        DeployableDescriptor deployableDescriptor = deploymentManager.deploy("docker:" + TEST_IMAGE);

        // Then
        Properties savedDescriptor = new Properties();
        savedDescriptor.load(new FileInputStream(new File(deploymentManager.workspace(), deployableDescriptor.id() + ".deploy")));
        assertEquals(deployableDescriptor.id(), savedDescriptor.getProperty("id"));
    }

}

@SpringBootApplication
class FileSystemDeploymentManagerTestConfiguration {

    @Autowired
    DockerClient docker;

    @Autowired
    ZedHome zedHome;

    @Bean
    DeployablesManager deploymentManager() {
        File workspace = createTempDir();
        return new FileSystemDeployablesManager(zedHome, workspace, allDeployableHandlers(workspace, docker));
    }

}