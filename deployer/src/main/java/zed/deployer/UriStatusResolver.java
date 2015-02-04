package zed.deployer;

import zed.deployer.manager.DeployableDescriptor;

public interface UriStatusResolver {

    boolean support(String uri);

    boolean status(DeployableDescriptor deployableDescriptor);

}
