package zed.deployer.executor;

import java.util.Optional;

public interface ProcessExecutor {

    Optional<String> start(String deploymentId);

}
