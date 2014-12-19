package foo;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@Mojo(name = "deploy", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class DeployMojo extends AbstractMojo {
    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}", property = "outputDir", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project}", required = true)
    private MavenProject project;

    public void execute()
            throws MojoExecutionException {
        final Process p;
        try {
            Properties versions = new Properties();
            versions.load(getClass().getResourceAsStream("/META-INF/maven/dependencies.properties"));
            String projectVersion = versions.getProperty("com.github.zed-platform/zed-maven-plugin/version");
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
            List<String> commands = IOUtils.readLines(new FileInputStream(new File(baseDir.getAbsolutePath() + "/src/main/resources/META-INF/zed", "deploy")));
            Thread.sleep(15000);
            for (String command : commands) {
                getLog().info("Executing command: " + command);
//                getLog().info(IOUtils.toString(p.getInputStream()));
                getLog().info(new SshClient("localhost", 2000).command(command).toString());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        File f = outputDirectory;

        if (!f.exists()) {
            f.mkdirs();
        }

        File touch = new File(f, "touch.txt");

        FileWriter w = null;
        try {
            w = new FileWriter(touch);

            w.write("touch.txt");
        } catch (IOException e) {
            throw new MojoExecutionException("Error creating file " + touch, e);
        } finally {
            if (w != null) {
                try {
                    w.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }
}
