package zed.deployer;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.Container;

import java.util.List;
import java.util.stream.Collectors;

public class DockerUriStatusResolver implements UriStatusResolver {

    private final DockerClient docker;

    public DockerUriStatusResolver(DockerClient docker) {
        this.docker = docker;
    }

    @Override
    public boolean support(String uri) {
        return uri.contains(":docker");
    }

    @Override
    public boolean status(DeploymentDescriptor deploymentDescriptor) {
        try {
            List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers());
            long size = containers.parallelStream().filter(c -> c.id().equals(deploymentDescriptor.pid()) && c.status().startsWith("Up ")).collect(Collectors.toList()).size();
            return size == 1;
        } catch (InterruptedException | DockerException e) {
            throw new RuntimeException(e);
        }
    }

}