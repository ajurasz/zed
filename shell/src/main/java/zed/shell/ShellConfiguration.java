package zed.shell;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import zed.deployer.DefaultDeploymentManager;
import zed.deployer.DeploymentManager;

@EnableAutoConfiguration
public class ShellConfiguration {

    public static void main(String[] args) {
        new SpringApplication(ShellConfiguration.class).run(args);
    }

    @Bean
    DeploymentManager deployer() {
        return new DefaultDeploymentManager();
    }

}
