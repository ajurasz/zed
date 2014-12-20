package zed.deployer;

import com.github.dockerjava.api.DockerClient;
import zed.deployer.manager.DeploymentDescriptor;
import zed.deployer.manager.ZedHome;

public class MongoDeployableHandler implements DeployableHandler {

    private final ZedHome zedHome;

    private final DockerClient docker;

    public MongoDeployableHandler(ZedHome zedHome, DockerClient docker) {
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