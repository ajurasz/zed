package zed.deployer.executor;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.messages.ContainerConfig;
import zed.deployer.DeploymentDescriptor;
import zed.deployer.DeploymentManager;

public class MongoDockerProcessExecutorHandler implements ProcessExecutorHandler {

    DeploymentManager deploymentManager;

    public MongoDockerProcessExecutorHandler(DeploymentManager deploymentManager) {
        this.deploymentManager = deploymentManager;
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith("mongo:docker");
    }

    @Override
    public String start(String deploymentId) {
        try {
            DockerClient docker = DefaultDockerClient.fromEnv().build();
            String pid = docker.createContainer(ContainerConfig.builder().image("dockerfile/mongodb").build()).id();
            docker.startContainer(pid);
            DeploymentDescriptor descriptor = deploymentManager.deployment(deploymentId);
            deploymentManager.update(descriptor.pid(pid));
            return pid;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
