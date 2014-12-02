package zed.deployer.executor;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerException;
import org.junit.Assert;
import org.junit.Test;
import zed.deployer.DefaultStatusResolver;
import zed.deployer.DeploymentDescriptor;
import zed.deployer.DeploymentManager;
import zed.deployer.FileSystemDeploymentManager;
import zed.deployer.StatusResolver;

public class MongoDockerProcessExecutorHandlerTest extends Assert {

    DeploymentManager deploymentManager = new FileSystemDeploymentManager();

    MongoDockerProcessExecutorHandler mongoDockerProcessExecutorHandler = new MongoDockerProcessExecutorHandler(deploymentManager);

    StatusResolver statusResolver = new DefaultStatusResolver(deploymentManager);

    DeploymentDescriptor descriptor;

    @Test
    public void shouldStartMongoProcess() throws DockerCertificateException, DockerException, InterruptedException {
        try {
            // Given
            deploymentManager.clear();
            descriptor = deploymentManager.deploy("mongodb:docker");

            // When
            String pid = mongoDockerProcessExecutorHandler.start(descriptor.id());
            descriptor = descriptor.pid(pid);

            // Then
            assertTrue(statusResolver.status(descriptor.id()));
        } finally {
            DefaultDockerClient.fromEnv().build().stopContainer(descriptor.pid(), 15);
            assertFalse(statusResolver.status(descriptor.id()));
        }
    }

}
