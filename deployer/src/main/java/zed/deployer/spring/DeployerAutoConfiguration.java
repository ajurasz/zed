package zed.deployer.spring;

import com.github.dockerjava.api.DockerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zed.deployer.executor.*;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.LocalFileSystemZedHome;
import zed.deployer.manager.ZedHome;

import static java.util.Arrays.asList;

@Configuration
public class DeployerAutoConfiguration {

    @Autowired
    DockerClient dockerClient;

    @ConditionalOnMissingBean
    @Bean
    ZedHome zedHome() {
        return new LocalFileSystemZedHome();
    }

    // Process executor

    @Bean
    ProcessExecutor defaultProcessExecutor(DeployablesManager deployablesManager, ProcessExecutorHandler[] handlers) {
        return new DefaultProcessExecutor(deployablesManager, asList(handlers));
    }

    @Bean
    ProcessExecutorHandler baseDockerProcessExecutorHandler(DeployablesManager deployablesManager) {
        return new BaseDockerProcessExecutorHandler(deployablesManager, dockerClient);
    }

    @Bean
    ProcessExecutorHandler fatJarLocalProcessExecutionHandler(ZedHome zedHome, DeployablesManager deployablesManager) {
        return new FatJarLocalProcessExecutionHandler(zedHome, deployablesManager);
    }

}
