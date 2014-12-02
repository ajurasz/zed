package zed.deployer;

import com.spotify.docker.client.DockerClient;

import java.util.Arrays;
import java.util.List;

public class DefaultStatusResolver implements StatusResolver {

    private final DeploymentManager deploymentManager;

    private final List<UriStatusResolver> statusResolvers;

    public DefaultStatusResolver(DeploymentManager deploymentManager, DockerClient docker) {
        this.deploymentManager = deploymentManager;
        this.statusResolvers = Arrays.asList(new ProcessUriStatusResolver(), new DockerUriStatusResolver(docker));
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
