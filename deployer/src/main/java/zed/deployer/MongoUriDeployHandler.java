package zed.deployer;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;

public class MongoUriDeployHandler implements UriDeployHandler {

    private final ZedHome zedHome;

    public MongoUriDeployHandler(ZedHome zedHome) {
        this.zedHome = zedHome;
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith("mongodb:docker");
    }

    @Override
    public void deploy(DeploymentDescriptor deploymentDescriptor) {
        try {
            String image = "dockerfile/mongodb";

            DockerClient docker = DefaultDockerClient.fromEnv().build();
            docker.pull(image);
        } catch (DockerCertificateException | InterruptedException | DockerException e) {
            throw new RuntimeException(e);
        }
    }

}