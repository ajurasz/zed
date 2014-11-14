package zed.deployer;

public interface UriDeployHandler {

    boolean supports(String uri);

    void deploy(String uri);

}
