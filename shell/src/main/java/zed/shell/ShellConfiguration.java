package zed.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import zed.deployer.DefaultStatusResolver;
import zed.deployer.DeploymentManager;
import zed.deployer.FileSystemDeploymentManager;
import zed.deployer.StatusResolver;

@EnableAutoConfiguration
public class ShellConfiguration {

    public static void main(String[] args) {
        new SpringApplication(ShellConfiguration.class).run(args);
    }

    @Bean
    DeploymentManager deployer() {
        return new FileSystemDeploymentManager();
    }

    @Bean
    StatusResolver statusResolver() {
        return new DefaultStatusResolver(deployer());
    }



}
