package zed.deployer;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;

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
        try {
            docker.pull("dockerfile/mongodb");
        } catch (InterruptedException | DockerException e) {
            throw new RuntimeException(e);
        }
    }

}