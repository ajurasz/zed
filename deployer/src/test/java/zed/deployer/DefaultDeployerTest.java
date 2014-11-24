package zed.deployer;

import com.google.common.io.Files;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class DefaultDeployerTest extends Assert {

    DefaultDeployer deployer = new DefaultDeployer();

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

}
