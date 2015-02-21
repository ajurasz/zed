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

            StartContainerCmd startContainer = docker.startContainerCmd(descriptor.pid());
            if (portToExpose(descriptor) != null) {
                startContainer.withPortBindings(PortBinding.parse(portToExpose(descriptor) + ":" + portToExpose(descriptor)));
            }
            if (volume(descriptor) != null) {
                startContainer.withBinds(Bind.parse(volume(descriptor)));
            }
            startContainer.exec();

            return descriptor.pid();
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }

    protected Integer portToExpose(DeployableDescriptor deployableDescriptor) {
        return null;
    }

    protected String volume(DeployableDescriptor deployableDescriptor) {
        return null;
    }

}
