package zed.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_SOURCES;

@Mojo(name = "deploy-start-all", defaultPhase = PROCESS_SOURCES)
public class DeployStartAllMojo extends AbstractZedMojo {

    private static final String START_ALL_COMMAND = "deploy_start_all";

    @Parameter(defaultValue = "default", required = true)
    String workspace;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        startSshServer(workspace);
        getLog().info("Executing command: " + START_ALL_COMMAND);
        getLog().info(sshClient().command(START_ALL_COMMAND).toString());

    }
}
