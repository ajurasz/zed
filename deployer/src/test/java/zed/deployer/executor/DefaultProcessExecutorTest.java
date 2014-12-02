package zed.deployer.executor;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerException;
import org.junit.Assert;
import org.junit.Test;
import zed.deployer.DeploymentDescriptor;
import zed.deployer.DeploymentManager;
import zed.deployer.FileSystemDeploymentManager;

public class DefaultProcessExecutorTest extends Assert {

    DeploymentManager deploymentManager = new FileSystemDeploymentManager();

    DefaultProcessExecutor defaultProcessExecutor = new DefaultProcessExecutor(deploymentManager);

    String pid;

    @Test
    public void shouldSupportMongoDocker() throws DockerCertificateException, DockerException, InterruptedException {
        try {
            // Given
            DeploymentDescriptor descriptor = deploymentManager.deploy("mongodb:docker");

            // When
            pid = defaultProcessExecutor.start(descriptor.id());

            // Then
            assertNotNull(pid);
        } finally {
            if (pid != null) {
                DefaultDockerClient.fromEnv().build().stopContainer(pid, 15);
            }
        }
    }

}
