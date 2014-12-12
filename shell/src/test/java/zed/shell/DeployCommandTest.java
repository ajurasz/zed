package zed.shell;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShellConfiguration.class)
@IntegrationTest
public class DeployCommandTest extends Assert {

    static int port = findAvailableTcpPort();

    @BeforeClass
    public void beforeClass() {
        System.setProperty("shell.ssh.port", port + "");
    }

    SshClient ssh = new SshClient("localhost", port);

    // Tests

    @Test
    public void shouldDeployFatJar() {
        ssh.printCommand("deploy_clean");

        ssh.printCommand("deploy fatjar:mvn:com.google.guava/guava/18.0");

        List<String> deploy_list = ssh.command("deploy_list");
        assertEquals(3, deploy_list.size());
        assertTrue(deploy_list.get(2).contains("fatjar:mvn:com.google.guava/guava/18.0"));
    }

    @Test
    public void shouldCleanDeployed() {
        ssh.printCommand("deploy_clean");
        ssh.printCommand("deploy fatjar:mvn:com.google.guava/guava/18.0");

        ssh.printCommand("deploy_clean");

        List<String> deploy_list = ssh.command("deploy_list");
        assertEquals(2, deploy_list.size());
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

    @Test
    public void shouldHandleInvalidFatJarMavenCoordinates() {
        // Given
        ssh.printCommand("deploy_clean");

        // When
        List<String> output = ssh.command("deploy fatjar:mvn:invalid/maven/coordinates");

        // Then
        assertEquals(1, output.size());
        assertTrue(output.get(0).contains("failed to load"));
        assertTrue(output.get(0).contains("invalid:maven:jar:coordinates"));
    }

}