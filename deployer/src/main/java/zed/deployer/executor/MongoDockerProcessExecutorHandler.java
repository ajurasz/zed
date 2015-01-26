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

    @Override
    protected String getImageName(DeploymentDescriptor descriptor) {
        return MONGO_IMAGE;
    }
}
