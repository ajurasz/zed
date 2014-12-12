package zed.deployer;

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
import zed.deployer.executor.DefaultProcessExecutor;
import zed.deployer.executor.ProcessExecutor;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.DeploymentDescriptor;
import zed.deployer.manager.FileSystemDeployablesManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static org.junit.Assume.assumeTrue;
import static org.springframework.boot.autoconfigure.spotifydocker.Dockers.isConnected;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {SpotifyDockerAutoConfiguration.class, FileSystemDeploymentManagerTestConfiguration.class})
@IntegrationTest
public class FileSystemDeployablesManagerTest extends Assert {

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
        assertTrue(Arrays.asList(deploymentManager.zedHome().deployDirectory().list()).contains("guava-18.0.jar"));
    }

    @Test
    public void shouldDeployFatWar() {
        // When
        deploymentManager.deploy("fatjar:mvn:com.github.zed-platform/zed-service-document-mongo/0.0.6/war");

        // Then
        assertTrue(Arrays.asList(deploymentManager.zedHome().deployDirectory().list()).contains("zed-service-document-mongo-0.0.6.war"));
    }

    @Test
    public void shouldDeploySnapshot() throws IOException {
        // Given
        Properties versions = new Properties();
        versions.load(getClass().getResourceAsStream("/META-INF/maven/dependencies.properties"));
        String projectVersion = versions.getProperty("com.github.zed-platform/zed-deployer/version");

        // When
        deploymentManager.deploy("fatjar:mvn:com.github.zed-platform/zed-utils/" + projectVersion);

        // Then
        assertTrue(Arrays.asList(deploymentManager.zedHome().deployDirectory().list()).contains("zed-utils-" + projectVersion + ".jar"));
    }

    @Test
    public void shouldWriteUriIntoFatJarMavenDescriptor() throws IOException {
        // When
        DeploymentDescriptor deploymentDescriptor = deploymentManager.deploy("fatjar:mvn:com.google.guava/guava/18.0");

        // Then
        Properties savedDescriptor = new Properties();
        savedDescriptor.load(new FileInputStream(new File(deploymentManager.zedHome().deployDirectory(), deploymentDescriptor.id() + ".deploy")));
        assertEquals(deploymentDescriptor.uri(), savedDescriptor.getProperty("uri"));
    }

    @Test
    public void shouldListDeploymentDescriptors() {
        // Given
        DeploymentDescriptor descriptor = deploymentManager.deploy("fatjar:mvn:com.google.guava/guava/18.0");

        // When
        List<DeploymentDescriptor> descriptors = deploymentManager.list();

        // Then
        assertEquals(1, descriptors.size());
        assertEquals(descriptor.id(), descriptors.get(0).id());
        assertEquals("fatjar:mvn:com.google.guava/guava/18.0", descriptors.get(0).uri());
    }

    @Test
    public void shouldWriteUriIntoDockerMongoDescriptor() throws IOException {
        // When
        assumeTrue(isConnected(docker));
        DeploymentDescriptor deploymentDescriptor = deploymentManager.deploy("mongodb:docker:dockerfile/mongodb");

        // Then
        Properties savedDescriptor = new Properties();
        savedDescriptor.load(new FileInputStream(new File(deploymentManager.zedHome().deployDirectory(), deploymentDescriptor.id() + ".deploy")));
        assertEquals(deploymentDescriptor.uri(), savedDescriptor.getProperty("uri"));
    }

    @Test
    public void shouldWriteIdIntoDockerMongoDescriptor() throws IOException {
        // Given
        assumeTrue(isConnected(docker));

        // When
        DeploymentDescriptor deploymentDescriptor = deploymentManager.deploy("mongodb:docker:dockerfile/mongodb");

        // Then
        Properties savedDescriptor = new Properties();
        savedDescriptor.load(new FileInputStream(new File(deploymentManager.zedHome().deployDirectory(), deploymentDescriptor.id() + ".deploy")));
        assertEquals(deploymentDescriptor.id(), savedDescriptor.getProperty("id"));
    }

}

@Configuration
class FileSystemDeploymentManagerTestConfiguration {

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