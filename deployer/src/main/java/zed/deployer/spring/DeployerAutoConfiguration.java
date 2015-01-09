package zed.deployer.spring;

import com.github.dockerjava.api.DockerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zed.deployer.executor.DefaultProcessExecutor;
import zed.deployer.executor.FatJarLocalProcessExecutionHandler;
import zed.deployer.executor.MongoDockerProcessExecutorHandler;
import zed.deployer.executor.ProcessExecutor;
import zed.deployer.executor.ProcessExecutorHandler;
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
    ProcessExecutorHandler mongoDockerProcessExecutorHandler(DeployablesManager deployablesManager) {
        return new MongoDockerProcessExecutorHandler(deployablesManager, dockerClient);
    }

    @Bean
    ProcessExecutorHandler fatJarLocalProcessExecutionHandler(ZedHome zedHome, DeployablesManager deployablesManager) {
        return new FatJarLocalProcessExecutionHandler(zedHome, deployablesManager);
    }

}
