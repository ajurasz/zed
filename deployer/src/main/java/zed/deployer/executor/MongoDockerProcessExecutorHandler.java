package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.DeploymentDescriptor;

public class MongoDockerProcessExecutorHandler extends BaseDockerProcessExecutorHandler {

    private static final String URI_PREFIX = "mongodb:docker";

    private static final String MONGO_IMAGE = "dockerfile/mongodb";

    public MongoDockerProcessExecutorHandler(DeployablesManager deployableManager, DockerClient docker) {
        super(deployableManager, docker);
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith(URI_PREFIX);
    }

    // Container configuration

    @Override
    protected String getImageName(DeploymentDescriptor descriptor) {
        return MONGO_IMAGE;
    }

    @Override
    protected String name(DeploymentDescriptor deploymentDescriptor) {
        return "mongodb";
    }

    @Override
    protected Integer portToExpose(DeploymentDescriptor deploymentDescriptor) {
        return 28017;
    }

    @Override
    protected String volume(DeploymentDescriptor deploymentDescriptor) {
        return "/var/zed/mongodb/default:/data/db";
    }

}