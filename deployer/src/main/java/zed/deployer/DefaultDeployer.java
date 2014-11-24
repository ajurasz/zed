package zed.deployer;


import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.UUID;

public class DefaultDeployer implements Deployer {

    private final ZedHome zedHome = new LocalFileSystemZedHome();

    @Override
    public DeploymentDescriptor deploy(String uri) {
        String id = UUID.randomUUID().toString();
        BasicDeploymentDescriptor deploymentDescriptor = new BasicDeploymentDescriptor(id, uri);
        new FatJarUriDeployHandler(zedHome).deploy(deploymentDescriptor);
        return deploymentDescriptor;
    }

    @Override
    public void clear() {
        try {
            FileUtils.deleteDirectory(zedHome.deployDirectory());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ZedHome zedHome() {
        return zedHome;
    }

}
