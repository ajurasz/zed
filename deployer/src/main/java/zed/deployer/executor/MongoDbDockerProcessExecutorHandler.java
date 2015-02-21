package zed.deployer.executor;

import com.github.dockerjava.api.DockerClient;
import zed.deployer.manager.DeployableDescriptor;
import zed.deployer.manager.DeployablesManager;

public class MongoDbDockerProcessExecutorHandler extends BaseDockerProcessExecutorHandler {

    private static final String URI_PREFIX = "mongodb:docker";

    public MongoDbDockerProcessExecutorHandler(DeployablesManager deployableManager, DockerClient docker) {
        super(deployableManager, docker);
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith(URI_PREFIX);
    }

    // Container configuration

    @Override
    protected Integer portToExpose(DeployableDescriptor deployableDescriptor) {
        return 28017;
    }

    @Override
    protected String volume(DeployableDescriptor deployableDescriptor) {
        return "/var/zed/mongodb/default:/data/db";
    }

}