package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.DeploymentDescriptor;

import java.util.Arrays;
import java.util.List;

public class DefaultProcessExecutor implements ProcessExecutor {

    private final DeployablesManager deployableManager;

    private final List<ProcessExecutorHandler> handlers;

    public DefaultProcessExecutor(DeployablesManager deployableManager, DockerClient docker) {
        this.handlers = Arrays.asList(new MongoDockerProcessExecutorHandler(deployableManager, docker));
        this.deployableManager = deployableManager;
    }

    @Override
    public String start(String deploymentId) {
        DeploymentDescriptor descriptor = deployableManager.deployment(deploymentId);
        for (ProcessExecutorHandler handler : handlers) {
            if (handler.supports(descriptor.uri())) {
                return handler.start(deploymentId);
            }
        }
        throw new RuntimeException("No executor handler for URI: " + descriptor.uri());
    }

}