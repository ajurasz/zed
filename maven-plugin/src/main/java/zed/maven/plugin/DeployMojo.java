package zed.maven.plugin;

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MINUTES;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_SOURCES;
import static zed.utils.Mavens.artifactVersion;
import static zed.utils.Mavens.localMavenRepository;

@Mojo(name = "deploy", defaultPhase = PROCESS_SOURCES)
public class DeployMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject project;

    @Parameter(defaultValue = "default", required = true)
    String workspace;

    public void execute()
            throws MojoExecutionException {
        final Process p;
        try {
            String projectVersion = artifactVersion("com.github.zed-platform", "zed-maven-plugin");
            String shellWarFilename = format("zed-shell-%s.war", projectVersion);
            String shellWarPath = Paths.get(localMavenRepository().getAbsolutePath(),
                    "com", "github", "zed-platform", "zed-shell", projectVersion, shellWarFilename).toFile().getAbsolutePath();
            p = Runtime.getRuntime().exec(new String[]{"java", "-jar", shellWarPath}, new String[]{"zed.shell.workspace=" + workspace});
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    p.destroy();
                }
            });
            await().atMost(2, MINUTES).until(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        new SshClient("localhost", 2000).command("foo");
                    } catch (RuntimeException e) {
                        return false;
                    }
                    return true;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File baseDir = project.getBasedir();
        try {
            File deployScript = Paths.get(baseDir.getAbsolutePath(), "src", "main", "resources", "META-INF", "zed", "deploy").toFile();
            List<String> commands = IOUtils.readLines(new FileInputStream(deployScript));
            for (String command : commands) {
                getLog().info("Executing command: " + command);
                getLog().info(new SshClient("localhost", 2000).command(command).toString());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}