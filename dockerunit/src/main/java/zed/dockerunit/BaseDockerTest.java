package zed.dockerunit;

import com.github.dockerjava.api.DockerClient;
import org.junit.AfterClass;
import org.junit.Assert;

import java.io.IOException;

import static zed.dockerunit.DockerTester.dockerTester;

public abstract class BaseDockerTest extends Assert {

    protected static DockerTester dockerTester;

    protected static void initializeDocker(String apiVersion) {
        dockerTester = dockerTester(apiVersion);
    }

    protected static void initializeDocker() {
        dockerTester = dockerTester();
    }

    protected static String run(String image, int... ports) {
        return dockerTester.run(image, ports);
    }

    protected void stop(String containerId) {
        dockerTester.stop(containerId);
    }

    @AfterClass
    public static void afterBaseDockerTest() throws IOException {
        dockerTester.stopAll();
        dockerTester.docker().close();
    }

    protected DockerClient docker() {
        return dockerTester.docker();
    }

}
