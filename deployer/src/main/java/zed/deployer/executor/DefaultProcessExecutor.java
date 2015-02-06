package zed.deployer.executor;

import zed.deployer.StatusResolver;
import zed.deployer.manager.DeployableDescriptor;
import zed.deployer.manager.DeployablesManager;

import java.util.List;
import java.util.Optional;

public class DefaultProcessExecutor implements ProcessExecutor {

    private final DeployablesManager deployableManager;

    private final List<ProcessExecutorHandler> handlers;

    private final StatusResolver statusResolver;

    public DefaultProcessExecutor(DeployablesManager deployableManager, List<ProcessExecutorHandler> handlers,
                                  StatusResolver statusResolver) {
        this.handlers = handlers;
        this.deployableManager = deployableManager;
        this.statusResolver = statusResolver;
    }

    @Override
    public Optional<String> start(String deploymentId) {
        DeployableDescriptor descriptor = deployableManager.deployment(deploymentId);
        for (ProcessExecutorHandler handler : handlers) {
            if (handler.supports(descriptor.uri())) {
                if (statusResolver.status(deploymentId)) {
                    return Optional.empty();
                }
                String pid = handler.start(deploymentId);
                deployableManager.update(descriptor.pid(pid));
                return Optional.of(pid);
            }
        }
        throw new RuntimeException("No executor handler for URI: " + descriptor.uri());
    }

}