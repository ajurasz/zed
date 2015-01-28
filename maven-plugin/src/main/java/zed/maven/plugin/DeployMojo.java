package zed.maven.plugin;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_SOURCES;
import static org.twdata.maven.mojoexecutor.MojoExecutor.*;

@Mojo(name = "deploy", defaultPhase = PROCESS_SOURCES)
public class DeployMojo extends AbstractZedMojo {

    private static final String DEPLOY_FILE_NAME = "deploy";
    private static final String RESOURCE_PLUGIN_ARTIFACT_ID = "maven-resources-plugin";

    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject project;

    @Parameter(defaultValue = "default", required = true)
    String workspace;

    @Parameter(property = "zed.profile")
    String profile;

    @Parameter(defaultValue = "2.5", required = true)
    String resourcePluginVersion;

    @Parameter(defaultValue = "${session}", readonly = true)
    private MavenSession mavenSession;

    @Component
    private BuildPluginManager pluginManager;

    public void execute()
            throws MojoExecutionException {

        findAndExecuteResourcePlugin();
        String deployFile = evaluateDeployFileName();

        startSshServer(workspace);

        File baseDir = project.getBasedir();
        try {
            List<File> deployScripts = Arrays.asList(
                    Paths.get(baseDir.getAbsolutePath(), "target", "classes", "META-INF", "zed", deployFile).toFile(),
                    Paths.get(baseDir.getAbsolutePath(), "src", "main", "resources", "META-INF", "zed", deployFile).toFile()
            );
            for (File file : deployScripts) {
                if (!file.exists()) continue;
                List<String> commands = IOUtils.readLines(new FileInputStream(file));
                for (String command : commands) {
                    getLog().info("Executing command: " + command);
                    getLog().info(sshClient().command(command).toString());
                }
                break;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void findAndExecuteResourcePlugin() {
        try {
            List<Plugin> plugins = project.getBuildPlugins();
            for (Plugin plugin : plugins) {
                if (plugin.getArtifactId().equals(RESOURCE_PLUGIN_ARTIFACT_ID)) {
                    resourcePluginVersion = plugin.getVersion();
                    break;
                }
            }
            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-resources-plugin"),
                            version(resourcePluginVersion)
                    ),
                    goal("resources"),
                    configuration(),
                    executionEnvironment(
                            project,
                            mavenSession,
                            pluginManager
                    )
            );
        } catch (Exception e) {
            getLog().warn(e.getMessage());
        }
    }

    private String evaluateDeployFileName() {
        if (StringUtils.isNotEmpty(profile)) {
            return DEPLOY_FILE_NAME + "." + profile;
        }
        return DEPLOY_FILE_NAME;
    }
}