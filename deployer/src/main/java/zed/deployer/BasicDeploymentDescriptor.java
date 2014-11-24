package zed.deployer;

public class BasicDeploymentDescriptor implements DeploymentDescriptor {

    private final String id;

    private final String uri;

    public BasicDeploymentDescriptor(String id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String uri() {
        return uri;
    }

}
