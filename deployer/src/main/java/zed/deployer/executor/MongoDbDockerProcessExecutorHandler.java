package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
import zed.deployer.manager.DeployableDescriptor;
import zed.deployer.manager.DeployablesManager;

public class MongoDbDockerProcessExecutorHandler extends BaseDockerProcessExecutorHandler {

    private static final String URI_PREFIX = "mongodb:docker";

    private static final String MONGO_IMAGE = "dockerfile/mongodb";

    public MongoDbDockerProcessExecutorHandler(DeployablesManager deployableManager, DockerClient docker) {
        super(deployableManager, docker);
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith(URI_PREFIX);
    }

    // Container configuration

    @Override
    protected String getImageName(DeployableDescriptor descriptor) {
        return MONGO_IMAGE;
    }

    @Override
    protected String name(DeployableDescriptor deployableDescriptor) {
        return "mongodb";
    }

    @Override
    protected Integer portToExpose(DeployableDescriptor deployableDescriptor) {
        return 28017;
    }

    @Override
    protected String volume(DeployableDescriptor deployableDescriptor) {
        return "/var/zed/mongodb/default:/data/db";
    }

}