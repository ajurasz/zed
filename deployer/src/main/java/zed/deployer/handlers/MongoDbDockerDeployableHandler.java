package zed.deployer.handlers;

import com.github.dockerjava.api.DockerClient;
import zed.deployer.manager.DeployableDescriptor;

public class MongoDbDockerDeployableHandler extends BaseDockerDeployableHandler {

    private static final String URI_PREFIX = "mongodb:docker";
    private static final String MONGO_IMAGE = "dockerfile/mongodb";

    public MongoDbDockerDeployableHandler(DockerClient docker) {
        super(docker);
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith(URI_PREFIX);
    }

    @Override
    protected void pullDockerImage(DeployableDescriptor deployableDescriptor) {
        asString(docker().pullImageCmd(MONGO_IMAGE).exec());
    }

    @Override
    protected String getImageName(DeployableDescriptor descriptor) {
        return MONGO_IMAGE;
    }

    @Override
    protected String name(DeployableDescriptor deployableDescriptor) {
        return "mongodb";
    }
}
