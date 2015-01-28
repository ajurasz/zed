package zed.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import static java.lang.String.format;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_SOURCES;

@Mojo(name = "deploy-start", defaultPhase = PROCESS_SOURCES)
public class DeployStartMojo extends AbstractZedMojo {

    private static final String START_COMMAND = "deploy_start %s";

    @Parameter(defaultValue = "default", required = true)
    String workspace;

    @Parameter(property = "deployableId", required = true)
    String deployableId;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        startSshServer(workspace);
        getLog().info("Executing command: " + format(START_COMMAND, deployableId));
        getLog().info(sshClient().command(format(START_COMMAND, deployableId)).toString());

    }
}
