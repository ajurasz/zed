package zed.deployer;

public class DefaultStatusResolver implements StatusResolver {

    private final DeploymentManager deploymentManager;

    public DefaultStatusResolver(DeploymentManager deploymentManager) {
        this.deploymentManager = deploymentManager;
    }

    @Override
    public boolean status(DeploymentDescriptor deploymentDescriptor) {
        return false;
    }

}
