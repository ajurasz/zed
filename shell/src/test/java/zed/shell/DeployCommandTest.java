package zed.shell;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import zed.deployer.manager.LocalFileSystemZedHome;
import zed.deployer.manager.ZedHome;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShellConfiguration.class)
@IntegrationTest
public class DeployCommandTest extends Assert {

    ZedHome zedHome = new LocalFileSystemZedHome();

    SshClient ssh = new SshClient("localhost", 2000);

    // Tests

    @Test
    public void shouldDeployGuavaJar() {
        ssh.printCommand("deploy_clean");

        String command = "deploy fatjar:mvn:com.google.guava/guava/18.0";
        ssh.printCommand(command);
        assertTrue(Arrays.asList(zedHome.deployDirectory().list()).contains("guava-18.0.jar"));

    }

    @Test
    public void shouldCleanDeployed() {
        ssh.printCommand("deploy_clean");
        ssh.printCommand("deploy fatjar:mvn:com.google.guava/guava/18.0");

        ssh.printCommand("deploy_clean");

        assertEquals(0, zedHome.deployDirectory().list().length);
    }

    @Test
    public void shouldHandleInvalidDeployableHandler() {
        // Given
        ssh.printCommand("deploy_clean");

        // When
        List<String> output = ssh.command("deploy someRandomCrap");

        // Then
        assertEquals(1, output.size());
        assertTrue(output.get(0).contains("No handler for deployable with URI"));
    }

}