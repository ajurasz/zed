package zed.deployer;

import java.util.List;

public interface DeploymentManager {

    DeploymentDescriptor deploy(String uri);

    DeploymentDescriptor update(DeploymentDescriptor pid);

    DeploymentDescriptor deployment(String deploymentId);

    List<DeploymentDescriptor> list();

    void clear();

}
