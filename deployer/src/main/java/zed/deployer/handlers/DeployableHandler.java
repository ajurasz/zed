package zed.deployer.handlers;

import zed.deployer.manager.DeployableDescriptor;

public interface DeployableHandler {

    boolean supports(String uri);

    void deploy(DeployableDescriptor deployableDescriptor);

}
