package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
import zed.deployer.DeploymentDescriptor;
import zed.deployer.DeploymentManager;

import java.util.Arrays;
import java.util.List;

public class DefaultProcessExecutor implements ProcessExecutor {

    private final DeploymentManager deploymentManager;

    private final List<ProcessExecutorHandler> handlers;

    public DefaultProcessExecutor(DeploymentManager deploymentManager, DockerClient docker) {
        this.handlers = Arrays.asList(new MongoDockerProcessExecutorHandler(deploymentManager, docker));
        this.deploymentManager = deploymentManager;
    }

    @Override
    public String start(String deploymentId) {
        DeploymentDescriptor descriptor = deploymentManager.deployment(deploymentId);
        for (ProcessExecutorHandler handler : handlers) {
            if (handler.supports(descriptor.uri())) {
                return handler.start(deploymentId);
            }
        }
        throw new RuntimeException("No executor handler for URI: " + descriptor.uri());
    }

}