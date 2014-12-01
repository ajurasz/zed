package zed.deployer.executor;

public interface ProcessExecutorHandler {

    boolean supports(String uri);

    String start(String deploymentId);

}
