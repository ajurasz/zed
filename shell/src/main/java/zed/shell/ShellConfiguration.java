package zed.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import zed.deployer.DefaultStatusResolver;
import zed.deployer.DeploymentManager;
import zed.deployer.FileSystemDeploymentManager;
import zed.deployer.StatusResolver;
import zed.deployer.executor.DefaultProcessExecutor;
import zed.deployer.executor.ProcessExecutor;

@EnableAutoConfiguration
public class ShellConfiguration {

    public static void main(String[] args) {
        new SpringApplication(ShellConfiguration.class).run(args);
    }

    @Bean
    DeploymentManager deploymentManager() {
        return new FileSystemDeploymentManager();
    }

    @Bean
    StatusResolver statusResolver() {
        return new DefaultStatusResolver(deploymentManager());
    }

    @Bean
    ProcessExecutor processExecutor() {
        return new DefaultProcessExecutor(deploymentManager());
    }

}
