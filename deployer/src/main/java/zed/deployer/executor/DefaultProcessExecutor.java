package zed.deployer.executor;

import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.DeploymentDescriptor;

import java.util.List;

public class DefaultProcessExecutor implements ProcessExecutor {

    private final DeployablesManager deployableManager;

    private final List<ProcessExecutorHandler> handlers;

    public DefaultProcessExecutor(DeployablesManager deployableManager, List<ProcessExecutorHandler> handlers) {
        this.handlers = handlers;
        this.deployableManager = deployableManager;
    }

    @Override
    public String start(String deploymentId) {
        DeploymentDescriptor descriptor = deployableManager.deployment(deploymentId);
        for (ProcessExecutorHandler handler : handlers) {
            if (handler.supports(descriptor.uri())) {
                String pid = handler.start(deploymentId);
                deployableManager.update(descriptor.pid(pid));
                return pid;
            }
        }
        throw new RuntimeException("No executor handler for URI: " + descriptor.uri());
    }

}