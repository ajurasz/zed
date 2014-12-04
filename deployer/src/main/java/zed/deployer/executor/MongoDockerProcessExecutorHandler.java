package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
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
            String pid = docker.createContainerCmd("dockerfile/mongodb").exec().getId();
            docker.startContainerCmd(pid).exec();
            DeploymentDescriptor descriptor = deploymentManager.deployment(deploymentId);
            deploymentManager.update(descriptor.pid(pid));
            return pid;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
