package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
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

    protected String getImageName(DeploymentDescriptor descriptor) {
        return descriptor.uri().substring(URI_PREFIX.length());
    }

    @Override
    public String start(String deploymentId) {
        try {
            DeploymentDescriptor descriptor = deployableManager.deployment(deploymentId);

            String pid = docker.createContainerCmd(getImageName(descriptor)).exec().getId();
            docker.startContainerCmd(pid).exec();

            deployableManager.update(descriptor.pid(pid));
            return pid;
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }
}
