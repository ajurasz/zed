package zed.maven.plugin;

import org.apache.maven.plugin.AbstractMojo;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static java.lang.String.format;
import static java.util.concurrent.TimeUnit.MINUTES;
import static zed.utils.Mavens.artifactVersion;
import static zed.utils.Mavens.localMavenRepository;

public abstract class AbstractZedMojo extends AbstractMojo {

    public void startSshServer(String workspace) {
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
                        new SshClient("localhost", 15005).command("foo");
                    } catch (RuntimeException e) {
                        return false;
                    }
                    return true;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected SshClient sshClient() {
        return new SshClient("localhost", 15005);
    }
}
