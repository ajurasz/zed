package zed.deployer;

import java.util.Arrays;
import java.util.List;

public class DefaultStatusResolver implements StatusResolver {

    private final DeploymentManager deploymentManager;

    private final List<UriStatusResolver> statusResolvers;

    public DefaultStatusResolver(DeploymentManager deploymentManager) {
        this.deploymentManager = deploymentManager;
        this.statusResolvers = Arrays.asList(new ProcessUriStatusResolver(), new DockerUriStatusResolver());
    }

    @Override
    public boolean status(String deploymentId) {
        DeploymentDescriptor descriptor = deploymentManager.deployment(deploymentId);
        for (UriStatusResolver uriStatusResolver : statusResolvers) {
            if (uriStatusResolver.support(descriptor.uri())) {
                return uriStatusResolver.status(descriptor);
            }
        }
        return false;
    }

}