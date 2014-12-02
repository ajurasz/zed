package zed.deployer.executor;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import zed.deployer.DeploymentDescriptor;
import zed.deployer.DeploymentManager;

public class MongoDockerProcessExecutorHandler implements ProcessExecutorHandler {

    private final DeploymentManager deploymentManager;

    private final DockerClient docker;

    public MongoDockerProcessExecutorHandler(DeploymentManager deploymentManager, DockerClient docker) {
        this.deploymentManager = deploymentManager;
        this.docker = docker;
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith("mongodb:docker");
    }

    @Override
    public String start(String deploymentId) {
        try {
            String pid = docker.createContainer(ContainerConfig.builder().image("dockerfile/mongodb").build()).id();
            docker.startContainer(pid, HostConfig.builder().build());
            DeploymentDescriptor descriptor = deploymentManager.deployment(deploymentId);
            deploymentManager.update(descriptor.pid(pid));
            return pid;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
