package zed.deployer;

import com.github.dockerjava.api.DockerClient;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.DeploymentDescriptor;

import java.util.Arrays;
import java.util.List;

public class DefaultStatusResolver implements StatusResolver {

    private final DeployablesManager deployableManager;

    private final List<UriStatusResolver> statusResolvers;

    public DefaultStatusResolver(DeployablesManager deployableManager, DockerClient docker) {
        this.deployableManager = deployableManager;
        this.statusResolvers = Arrays.asList(new ProcessUriStatusResolver(), new DockerUriStatusResolver(docker));
    }

    @Override
    public boolean status(String deploymentId) {
        DeploymentDescriptor descriptor = deployableManager.deployment(deploymentId);
        for (UriStatusResolver uriStatusResolver : statusResolvers) {
            if (uriStatusResolver.support(descriptor.uri())) {
                return uriStatusResolver.status(descriptor);
            }
        }
        return false;
    }

}
