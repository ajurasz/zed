package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.StartContainerCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.PortBinding;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.DeploymentDescriptor;

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
            DeploymentDescriptor descriptor = deployableManager.deployment(deploymentId);

            CreateContainerCmd createContainer = docker.createContainerCmd(getImageName(descriptor));
            if (name(descriptor) != null) {
                createContainer.withName(name(descriptor));
            }
            String pid = createContainer.exec().getId();

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

    protected String getImageName(DeploymentDescriptor descriptor) {
        return descriptor.uri().substring(URI_PREFIX.length());
    }

    protected String name(DeploymentDescriptor deploymentDescriptor) {
        return null;
    }

    protected Integer portToExpose(DeploymentDescriptor deploymentDescriptor) {
        return null;
    }

    protected String volume(DeploymentDescriptor deploymentDescriptor) {
        return null;
    }

}
