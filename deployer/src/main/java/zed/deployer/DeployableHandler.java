package zed.deployer;

import zed.deployer.manager.DeploymentDescriptor;

public interface DeployableHandler {

    boolean supports(String uri);

    void deploy(DeploymentDescriptor deploymentDescriptor);

}
