package zed.deployer;

import java.util.List;

public interface DeploymentManager {

    DeploymentDescriptor deploy(String uri);

    List<DeploymentDescriptor> list();

    void clear();
}
