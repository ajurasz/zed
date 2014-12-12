package zed.deployer;

import zed.deployer.manager.DeploymentDescriptor;

public interface UriDeployHandler {

    boolean supports(String uri);

    void deploy(DeploymentDescriptor deploymentDescriptor);

}
