package zed.shell;

import com.github.dockerjava.api.DockerClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.spotifydocker.SpotifyDockerAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import zed.deployer.DefaultStatusResolver;
import zed.deployer.StatusResolver;
import zed.deployer.manager.DeployablesManager;
import zed.deployer.manager.FileSystemDeployablesManager;
import zed.deployer.manager.ZedHome;

import java.io.File;

import static zed.deployer.handlers.DeployableHandlers.allDeployableHandlers;

@EnableAutoConfiguration
@Import(SpotifyDockerAutoConfiguration.class)
public class ShellConfiguration {

    @Autowired
    DockerClient docker;

    @Bean
    DeployablesManager deploymentManager(@Value("${zed.shell.workspace:default}") String workspace, ZedHome zedHome) {
        File deployDirectory = zedHome.deployDirectory();
        File workspaceFile = new File(deployDirectory, workspace);
        return new FileSystemDeployablesManager(zedHome, workspaceFile, allDeployableHandlers(workspaceFile, docker));
    }

    @Bean
    StatusResolver statusResolver(DeployablesManager deploymentManager) {
        return new DefaultStatusResolver(deploymentManager, docker);
    }

}
