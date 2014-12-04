package zed.deployer;

import com.github.dockerjava.api.DockerClient;

public class MongoUriDeployHandler implements UriDeployHandler {

    private final ZedHome zedHome;

    private final DockerClient docker;

    public MongoUriDeployHandler(ZedHome zedHome, DockerClient docker) {
        this.zedHome = zedHome;
        this.docker = docker;
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith("mongodb:docker");
    }

    @Override
    public void deploy(DeploymentDescriptor deploymentDescriptor) {
        docker.pullImageCmd("dockerfile/mongodb").exec();
    }

}