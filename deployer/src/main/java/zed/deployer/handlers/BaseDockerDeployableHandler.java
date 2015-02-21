package zed.deployer.handlers;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import org.apache.commons.io.IOUtils;
import zed.deployer.manager.DeployableDescriptor;
import zed.deployer.util.DockerUriUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class BaseDockerDeployableHandler implements DeployableHandler {

    private static final String URI_PREFIX = "docker:";

    private final DockerClient docker;

    public BaseDockerDeployableHandler(DockerClient docker) {
        this.docker = docker;
    }

    protected DockerClient docker() {
        return this.docker;
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith(URI_PREFIX);
    }

    @Override
    public void deploy(DeployableDescriptor deployableDescriptor) {
        String[] dockerUri = DockerUriUtil.imageName(URI_PREFIX, deployableDescriptor.uri()).split(":");
        InputStream inputStream = null;
        if (dockerUri.length == 2) {
            inputStream = docker.pullImageCmd(dockerUri[0]).withTag(dockerUri[1]).exec();
        } else {
            inputStream = docker.pullImageCmd(dockerUri[0]).exec();
        }

        asString(inputStream);

        String pid;
        if (name(deployableDescriptor) != null) {
            List<Container> containers = docker.listContainersCmd().withShowAll(true).exec();
            containers = containers.parallelStream().filter(c -> asList(c.getNames()).contains("/" + name(deployableDescriptor))).collect(Collectors.toList());
            if (containers.size() == 0) {
                pid = docker.createContainerCmd(getImageName(deployableDescriptor)).withName(name(deployableDescriptor)).withEnv(envVariables(deployableDescriptor)).exec().getId();
            } else {
                pid = containers.get(0).getId();
            }
        } else {
            pid = docker.createContainerCmd(getImageName(deployableDescriptor)).withEnv(envVariables(deployableDescriptor)).exec().getId();
        }

        //deployableManager.update(deployableDescriptor.pid(pid));
    }

    protected String asString(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected String getImageName(DeployableDescriptor descriptor) {
        return DockerUriUtil.imageName(URI_PREFIX, descriptor.uri());
    }

    protected String name(DeployableDescriptor deployableDescriptor) {
        return null;
    }

    protected String[] envVariables(DeployableDescriptor deployableDescriptor) {
        return DockerUriUtil.environmentVariables(deployableDescriptor.uri());
    }
}
