package zed.deployer;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerCertificateException;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.DockerException;
import com.spotify.docker.client.messages.Container;

import java.util.List;
import java.util.stream.Collectors;

public class DockerUriStatusResolver implements UriStatusResolver {

    @Override
    public boolean support(String uri) {
        return uri.contains(":docker");
    }

    @Override
    public boolean status(DeploymentDescriptor deploymentDescriptor) {
        try {
            DockerClient docker = DefaultDockerClient.fromEnv().build();
            List<Container> containers = docker.listContainers(DockerClient.ListContainersParam.allContainers());
            long size = containers.parallelStream().filter(c -> c.id().equals(deploymentDescriptor.pid()) && c.status().startsWith("Up ")).collect(Collectors.toList()).size();
            return size == 1;
        } catch (DockerCertificateException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (DockerException e) {
            throw new RuntimeException(e);
        }
    }

}