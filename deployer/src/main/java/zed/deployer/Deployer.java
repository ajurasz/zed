package zed.deployer;

public interface Deployer {

    DeploymentDescriptor deploy(String uri);

    void clear();
}
