package zed.deployer;

import zed.deployer.manager.DeploymentDescriptor;

public interface UriStatusResolver {

    boolean support(String uri);

    boolean status(DeploymentDescriptor deploymentDescriptor);

}
