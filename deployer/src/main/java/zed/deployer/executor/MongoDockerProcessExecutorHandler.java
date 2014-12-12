package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.DeploymentDescriptor;

public class MongoDockerProcessExecutorHandler implements ProcessExecutorHandler {

    private final DeployablesManager deployableManager;

    private final DockerClient docker;

    public MongoDockerProcessExecutorHandler(DeployablesManager deployableManager, DockerClient docker) {
        this.deployableManager = deployableManager;
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
            DeploymentDescriptor descriptor = deployableManager.deployment(deploymentId);
            deployableManager.update(descriptor.pid(pid));
            return pid;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
