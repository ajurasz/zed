package zed.deployer;


import org.apache.commons.io.FileUtils;

import java.io.IOException;

public class DefaultDeployer implements Deployer {

    private final ZedHome zedHome = new LocalFileSystemZedHome();

    @Override
    public void deploy(String uri) {
        new MavenUriDeployHandler(zedHome).deploy(uri);
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
