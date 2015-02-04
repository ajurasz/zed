package zed.deployer;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import zed.deployer.manager.DeployableDescriptor;

import java.util.List;
import java.util.stream.Collectors;

public class DockerUriStatusResolver implements UriStatusResolver {

    private final DockerClient docker;

    public DockerUriStatusResolver(DockerClient docker) {
        this.docker = docker;
    }

    @Override
    public boolean support(String uri) {
        return uri.contains("docker:") ||
                uri.contains(":docker");
    }

    @Override
    public boolean status(DeployableDescriptor deployableDescriptor) {
        List<Container> containers = docker.listContainersCmd().exec();
        long size = containers.parallelStream().filter(c -> c.getId().equals(deployableDescriptor.pid()) && c.getStatus().startsWith("Up ")).collect(Collectors.toList()).size();
        return size == 1;
    }

}