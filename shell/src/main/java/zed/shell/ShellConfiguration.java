package zed.shell;

import com.github.dockerjava.api.DockerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.spotifydocker.SpotifyDockerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import zed.deployer.DefaultStatusResolver;
import zed.deployer.StatusResolver;
import zed.deployer.executor.DefaultProcessExecutor;
import zed.deployer.executor.ProcessExecutor;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.FileSystemDeployablesManager;

@EnableAutoConfiguration
@Import(SpotifyDockerAutoConfiguration.class)
public class ShellConfiguration {

    @Autowired
    DockerClient docker;

    @Bean
    DeployablesManager deploymentManager() {
        return new FileSystemDeployablesManager(docker);
    }

    @Bean
    StatusResolver statusResolver() {
        return new DefaultStatusResolver(deploymentManager(), docker);
    }

    @Bean
    ProcessExecutor processExecutor() {
        return new DefaultProcessExecutor(deploymentManager(), docker);
    }

}
