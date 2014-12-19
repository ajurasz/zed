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

import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_SOURCES;
import static zed.utils.Mavens.artifactVersion;

@Mojo(name = "deploy", defaultPhase = PROCESS_SOURCES)
public class DeployMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject project;

    public void execute()
            throws MojoExecutionException {
        final Process p;
        try {
            String projectVersion = artifactVersion("com.github.zed-platform", "zed-maven-plugin");
            String zedShellUrl = String.format(System.getProperty("user.home") + "/.m2/repository/com/github/zed-platform/zed-shell/%s/zed-shell-%s.war", projectVersion, projectVersion);
            p = Runtime.getRuntime().exec(new String[]{"java", "-jar", zedShellUrl});
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    p.destroy();
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        File baseDir = project.getBasedir();
        try {
            File deployScript = Paths.get(baseDir.getAbsolutePath(), "src", "main", "resources", "META-INF", "zed", "deploy").toFile();
            List<String> commands = IOUtils.readLines(new FileInputStream(deployScript));
            Thread.sleep(15000);
            for (String command : commands) {
                getLog().info("Executing command: " + command);
                getLog().info(new SshClient("localhost", 2000).command(command).toString());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}