package zed.deployer.manager;


import com.github.dockerjava.api.DockerClient;
import org.apache.commons.io.FileUtils;
import zed.deployer.FatJarUriDeployHandler;
import zed.deployer.MongoUriDeployHandler;
import zed.deployer.UriDeployHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

public class FileSystemDeployablesManager implements DeployablesManager {

    private final File workspace;

    private final ZedHome zedHome = new LocalFileSystemZedHome();

    private final List<UriDeployHandler> deployHandlers;

    public FileSystemDeployablesManager(String workspace, DockerClient docker) {
        this.workspace = new File(zedHome.deployDirectory(), workspace);
        this.workspace.mkdirs();
        deployHandlers = Arrays.asList(new FatJarUriDeployHandler(this.workspace), new MongoUriDeployHandler(zedHome, docker));
    }

    @Override
    public DeploymentDescriptor deploy(String uri) {
        String id = UUID.randomUUID().toString();
        BasicDeploymentDescriptor deploymentDescriptor = new BasicDeploymentDescriptor(workspace.getName(), id, uri);
        for (UriDeployHandler deployHandler : deployHandlers) {
            if (deployHandler.supports(uri)) {
                deployHandler.deploy(deploymentDescriptor);
                deploymentDescriptor.save(new File(workspace, deploymentDescriptor.id() + ".deploy"));
                return deploymentDescriptor;
            }
        }
        throw new RuntimeException("No handler for deployable with URI: " + uri);
    }

    @Override
    public DeploymentDescriptor update(DeploymentDescriptor pid) {
        BasicDeploymentDescriptor basicDescriptor = (BasicDeploymentDescriptor) pid;
        basicDescriptor.save(new File(workspace, pid.id() + ".deploy"));
        return basicDescriptor;
    }

    @Override
    public DeploymentDescriptor deployment(String deploymentId) {
        List<DeploymentDescriptor> deploymentDescriptors = list().parallelStream().filter(descriptor -> descriptor.id().equals(deploymentId)).collect(Collectors.toList());
        return new BasicDeploymentDescriptor(workspace.getName(), deploymentId, deploymentDescriptors.get(0).uri(), deploymentDescriptors.get(0).pid());
    }

    @Override
    public List<DeploymentDescriptor> list() {
        return newArrayList(workspace.listFiles((dir, name) -> name.endsWith(".deploy"))).
                parallelStream().map(file -> {
            try {
                Properties props = new Properties();
                props.load(new FileInputStream(file));
                String pid = props.getProperty("pid");
                String uri = props.getProperty("uri");
                return new BasicDeploymentDescriptor(workspace.getName(), file.getName().replaceAll(".deploy", ""), uri, pid);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }

    @Override
    public void clear() {
        try {
            FileUtils.deleteDirectory(workspace);
            workspace.mkdirs();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ZedHome zedHome() {
        return zedHome;
    }

    public File workspace() {
        return workspace;
    }

}
