package zed.deployer.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

public class DockerUriUtilTest extends Assert {

    @Test
    public void resolveImageNameFromUri() {
        // Given
        String uri = "docker:repo/image";

        // When
        String imageName = DockerUriUtil.imageName("docker:", uri);

        // Then
        assertEquals("repo/image", imageName);
    }

    @Test
    public void resolveImageNameFromUriWithTag() {
        // Given
        String uri = "docker:repo/image:latest";

        // When
        String imageName = DockerUriUtil.imageName("docker:", uri);

        // Then
        assertEquals("repo/image:latest", imageName);
    }

    @Test
    public void resolveImageNameFromUriWithTagAndQueryString() {
        // Given
        String uri = "docker:repo/image:latest?foo=bar";

        // When
        String imageName = DockerUriUtil.imageName("docker:", uri);

        // Then
        assertEquals("repo/image:latest", imageName);
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveImageNameFromUriThrowsException() {
        // Given
        String uri = "doker:repo/image";

        // When
        String imageName = DockerUriUtil.imageName("docker:", uri);
    }

    @Test
    public void resolveEnvironmentParametersFromUri() {
        // Given
        String uri = "docker:repo/image:latest?e:foo=bar&e:bar=foo";

        // When
        String[] variables = DockerUriUtil.environmentVariables(uri);

        // Then
        assertTrue(Arrays.asList(variables).contains("foo=bar"));
        assertTrue(Arrays.asList(variables).contains("bar=foo"));
    }

    @Test
    public void resolveEnvironmentParametersFromUriWhenNonUsed() {
        // Given
        String uri = "docker:repo/image:latest";

        // When
        String[] variables = DockerUriUtil.environmentVariables(uri);

        // Then
        assertTrue(variables.length == 0);
    }
}
