package zed.deployer.handlers;

import com.github.dockerjava.api.DockerClient;
import zed.deployer.manager.DeploymentDescriptor;

public class MongoDbDockerDeployableHandler implements DeployableHandler {

    private static final String URI_PREFIX = "mongodb:docker";

    private final DockerClient docker;

    public MongoDbDockerDeployableHandler(DockerClient docker) {
        this.docker = docker;
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith(URI_PREFIX);
    }

    @Override
    public void deploy(DeploymentDescriptor deploymentDescriptor) {
        docker.pullImageCmd(URI_PREFIX).exec();
    }

}