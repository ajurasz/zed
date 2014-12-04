package zed.deployer;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;

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
        List<Container> containers = docker.listContainersCmd().exec();
        long size = containers.parallelStream().filter(c -> c.getId().equals(deploymentDescriptor.pid()) && c.getStatus().startsWith("Up ")).collect(Collectors.toList()).size();
        return size == 1;
    }

}