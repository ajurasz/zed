package zed.deployer.manager;


import org.apache.commons.io.FileUtils;
import zed.deployer.handlers.DeployableHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

public class FileSystemDeployablesManager implements DeployablesManager {

    private final File workspace;

    private final ZedHome zedHome;

    private final List<DeployableHandler> deployHandlers;

    public FileSystemDeployablesManager(ZedHome zedHome, File workspace, List<DeployableHandler> deployHandlers) {
        this.zedHome = zedHome;
        this.workspace = workspace;
        this.workspace.mkdirs();
        this.deployHandlers = deployHandlers;
    }

    @Override
    public DeployableDescriptor deploy(String uri) {
        String id = UUID.randomUUID().toString();
        BasicDeployableDescriptor deploymentDescriptor = new BasicDeployableDescriptor(workspace.getName(), id, uri);
        for (DeployableHandler deployHandler : deployHandlers) {
            if (deployHandler.supports(uri)) {
                deployHandler.deploy(deploymentDescriptor);
                deploymentDescriptor.save(new File(workspace, deploymentDescriptor.id() + ".deploy"));
                return deploymentDescriptor;
            }
        }
        throw new RuntimeException("No handler for deployable with URI: " + uri);
    }

    @Override
    public DeployableDescriptor update(DeployableDescriptor descriptor) {
        BasicDeployableDescriptor basicDescriptor = (BasicDeployableDescriptor) descriptor;
        basicDescriptor.save(new File(workspace, descriptor.id() + ".deploy"));
        return basicDescriptor;
    }

    @Override
    public DeployableDescriptor deployment(String deployableId) {
        List<DeployableDescriptor> deployableDescriptors = list().parallelStream().filter(descriptor -> descriptor.id().equals(deployableId)).collect(Collectors.toList());
        return new BasicDeployableDescriptor(workspace.getName(), deployableId, deployableDescriptors.get(0).uri(), deployableDescriptors.get(0).pid());
    }

    @Override
    public List<DeployableDescriptor> list() {
        return newArrayList(workspace.listFiles((dir, name) -> name.endsWith(".deploy"))).
                parallelStream().map(file -> {
            try {
                Properties props = new Properties();
                props.load(new FileInputStream(file));
                String pid = props.getProperty("pid");
                String uri = props.getProperty("uri");
                return new BasicDeployableDescriptor(workspace.getName(), file.getName().replaceAll(".deploy", ""), uri, pid);
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
