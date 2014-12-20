package zed.deployer.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class BasicDeploymentDescriptor implements DeploymentDescriptor {

    private final String workspace;

    private final String id;

    private final String uri;

    private final String pid;

    public BasicDeploymentDescriptor(String workspace, String id, String uri, String pid) {
        this.workspace = workspace;
        this.id = id;
        this.uri = uri;
        this.pid = pid;
    }

    public BasicDeploymentDescriptor(String workspace, String id, String uri) {
        this(workspace, id, uri, null);
    }

    @Override
    public String workspace() {
        return workspace;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String uri() {
        return uri;
    }

    @Override
    public String pid() {
        return pid;
    }

    @Override
    public BasicDeploymentDescriptor pid(String pid) {
        return new BasicDeploymentDescriptor(workspace, id, uri, pid);
    }

    @Override
    public String toString() {
        return id + " " + uri;
    }

    public void save(File output) {
        try {
            Properties properties = new Properties();
            properties.put("workspace", workspace);
            properties.put("id", id);
            properties.put("uri", uri);
            if (pid != null) {
                properties.put("pid", pid);
            }
            properties.store(new FileOutputStream(output), "some-comment");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
