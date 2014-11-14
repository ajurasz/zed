package zed.deployer;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class DefaultDeployerTest extends Assert {

    DefaultDeployer deployer = new DefaultDeployer();

    @Test
    public void shouldDeployGuavaJar() {
        // Given
        deployer.clear();

        // When
        deployer.deploy("mvn:com.google.guava/guava/18.0");

        // Then
        assertTrue(Arrays.asList(deployer.zedHome().deployDirectory().list()).contains("guava-18.0.jar"));
    }

}
