package zed.deployer.manager;

import java.util.List;

public interface DeployablesManager {

    DeployableDescriptor deploy(String uri);

    DeployableDescriptor update(DeployableDescriptor descriptor);

    DeployableDescriptor deployment(String deployableId);

    List<DeployableDescriptor> list();

    void clear();

}
