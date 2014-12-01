package zed.deployer;

public interface UriStatusResolver {

    boolean support(String uri);

    boolean status(DeploymentDescriptor deploymentDescriptor);

}
