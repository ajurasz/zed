package zed.deployer;

import com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

public class DefaultDeploymentManagerTest extends Assert {

    DefaultDeploymentManager deployer = new DefaultDeploymentManager();

    @Test
    public void shouldDeployFatGuavaJar() {
        // Given
        deployer.clear();

        // When
        deployer.deploy("fatjar:mvn:com.google.guava/guava/18.0");

        // Then
        assertTrue(Arrays.asList(deployer.zedHome().deployDirectory().list()).contains("guava-18.0.jar"));
    }

    @Test
    public void shouldDeployFatGuavaJarDescriptor() throws IOException {
        // Given
        deployer.clear();

        // When
        DeploymentDescriptor deploymentDescriptor = deployer.deploy("fatjar:mvn:com.google.guava/guava/18.0");

        // Then
        String savedDescriptor = Files.toString(new File(deployer.zedHome().deployDirectory(), deploymentDescriptor.id() + ".deploy"), Charset.defaultCharset());
        assertEquals(deploymentDescriptor.uri(), savedDescriptor);
    }

    @Test
    public void shouldListDeploymentDescriptors() {
        // Given
        deployer.clear();
        DeploymentDescriptor descriptor = deployer.deploy("fatjar:mvn:com.google.guava/guava/18.0");

        // When
        List<DeploymentDescriptor> descriptors = deployer.list();

        // Then
        assertEquals(1, descriptors.size());
        assertEquals(descriptor.id(), descriptors.get(0).id());
        assertEquals("fatjar:mvn:com.google.guava/guava/18.0", descriptors.get(0).uri());
    }

}
