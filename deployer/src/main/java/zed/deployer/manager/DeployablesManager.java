package zed.deployer.manager;

import java.util.List;

public interface DeployablesManager {

    DeploymentDescriptor deploy(String uri);

    DeploymentDescriptor update(DeploymentDescriptor pid);

    DeploymentDescriptor deployment(String deploymentId);

    List<DeploymentDescriptor> list();

    void clear();

}
