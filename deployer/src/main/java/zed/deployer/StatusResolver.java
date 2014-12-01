package zed.deployer;

public interface StatusResolver {

    boolean status(DeploymentDescriptor deploymentDescriptor);

}
