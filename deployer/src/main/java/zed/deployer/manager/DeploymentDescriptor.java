package zed.deployer.manager;

public interface DeploymentDescriptor {

    String workspace();

    String id();

    String uri();

    String pid();

    DeploymentDescriptor pid(String pid);

}
