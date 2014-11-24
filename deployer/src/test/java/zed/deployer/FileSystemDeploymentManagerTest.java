package zed.deployer;

import com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class FileSystemDeploymentManagerTest extends Assert {

    FileSystemDeploymentManager deployer = new FileSystemDeploymentManager();

    @Before
    public void before() {
        deployer.clear();
    }

    // Tests

    @Test
    public void shouldDeployFatGuavaJar() {
        // When
        deployer.deploy("fatjar:mvn:com.google.guava/guava/18.0");

        // Then
        assertTrue(Arrays.asList(deployer.zedHome().deployDirectory().list()).contains("guava-18.0.jar"));
    }

    @Test
    public void shouldDeployFatGuavaJarDescriptor() throws IOException {
        // When
        DeploymentDescriptor deploymentDescriptor = deployer.deploy("fatjar:mvn:com.google.guava/guava/18.0");

        // Then
        String savedDescriptor = Files.toString(new File(deployer.zedHome().deployDirectory(), deploymentDescriptor.id() + ".deploy"), Charset.defaultCharset());
        assertEquals(deploymentDescriptor.uri(), savedDescriptor);
    }

    @Test
    public void shouldListDeploymentDescriptors() {
        // Given
        DeploymentDescriptor descriptor = deployer.deploy("fatjar:mvn:com.google.guava/guava/18.0");

        // When
        List<DeploymentDescriptor> descriptors = deployer.list();

        // Then
        assertEquals(1, descriptors.size());
        assertEquals(descriptor.id(), descriptors.get(0).id());
        assertEquals("fatjar:mvn:com.google.guava/guava/18.0", descriptors.get(0).uri());
    }

}
