package zed.deployer.manager;

public interface DeployableDescriptor {

    String workspace();

    String id();

    String uri();

    String pid();

    DeployableDescriptor pid(String pid);

}
