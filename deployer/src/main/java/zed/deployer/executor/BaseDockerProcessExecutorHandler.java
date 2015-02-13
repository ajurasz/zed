package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.PortBinding;
import zed.deployer.manager.DeployableDescriptor;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.util.DockerUriUtil;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class BaseDockerProcessExecutorHandler implements ProcessExecutorHandler {

    private static final String URI_PREFIX = "docker:";

    private final DeployablesManager deployableManager;

    private final DockerClient docker;

    public BaseDockerProcessExecutorHandler(DeployablesManager deployableManager, DockerClient docker) {
        this.deployableManager = deployableManager;
        this.docker = docker;
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith(URI_PREFIX);
    }

    @Override
    public String start(String deploymentId) {
        try {
            DeployableDescriptor descriptor = deployableManager.deployment(deploymentId);

            String pid;
            if (name(descriptor) != null) {
                List<Container> containers = docker.listContainersCmd().withShowAll(true).exec();
                containers = containers.parallelStream().filter(c -> asList(c.getNames()).contains("/" + name(descriptor))).collect(Collectors.toList());
                if (containers.size() == 0) {
                    pid = docker.createContainerCmd(getImageName(descriptor)).withName(name(descriptor)).withEnv(envVariables(descriptor)).exec().getId();
                } else {
                    pid = containers.get(0).getId();
                }
            } else {
                pid = docker.createContainerCmd(getImageName(descriptor)).withEnv(envVariables(descriptor)).exec().getId();
            }

            StartContainerCmd startContainer = docker.startContainerCmd(pid);
            if (portToExpose(descriptor) != null) {
                startContainer.withPortBindings(PortBinding.parse(portToExpose(descriptor) + ":" + portToExpose(descriptor)));
            }
            if (volume(descriptor) != null) {
                startContainer.withBinds(Bind.parse(volume(descriptor)));
            }
            startContainer.exec();

            deployableManager.update(descriptor.pid(pid));
            return pid;
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }

    protected String getImageName(DeployableDescriptor descriptor) {
        return DockerUriUtil.imageName(URI_PREFIX, descriptor.uri());
    }

    protected String name(DeployableDescriptor deployableDescriptor) {
        return null;
    }

    protected Integer portToExpose(DeployableDescriptor deployableDescriptor) {
        return null;
    }

    protected String volume(DeployableDescriptor deployableDescriptor) {
        return null;
    }

    protected String[] envVariables(DeployableDescriptor deployableDescriptor) {
        return DockerUriUtil.environmentVariables(deployableDescriptor.uri());
    }

}
