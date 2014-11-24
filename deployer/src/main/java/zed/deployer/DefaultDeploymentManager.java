package zed.deployer;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

public class DefaultDeploymentManager implements DeploymentManager {

    private final ZedHome zedHome = new LocalFileSystemZedHome();

    @Override
    public DeploymentDescriptor deploy(String uri) {
        String id = UUID.randomUUID().toString();
        BasicDeploymentDescriptor deploymentDescriptor = new BasicDeploymentDescriptor(id, uri);
        new FatJarUriDeployHandler(zedHome).deploy(deploymentDescriptor);
        return deploymentDescriptor;
    }

    @Override
    public List<DeploymentDescriptor> list() {
        return newArrayList(zedHome.deployDirectory().listFiles((dir, name) -> name.endsWith(".deploy"))).
                parallelStream().
                map(file -> new BasicDeploymentDescriptor(file.getName().replaceAll(".deploy", ""), fileToString(file))).
                collect(Collectors.toList());
    }

    String fileToString(File file) {
        try {
            return IOUtils.toString(new FileInputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
