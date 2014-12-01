package zed.deployer;

public interface DeploymentDescriptor {

    String id();

    String uri();

    String pid();

    DeploymentDescriptor pid(String pid);

}
