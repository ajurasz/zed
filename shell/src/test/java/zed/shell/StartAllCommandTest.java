package zed.shell;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import zed.ssh.client.SshClient;

import java.util.List;

import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ShellConfiguration.class, StartAllCommandTest.class})
@IntegrationTest
public class StartAllCommandTest extends Assert {

    static int port = findAvailableTcpPort();

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("shell.ssh.port", port + "");
    }

    SshClient ssh = new SshClient("localhost", port);

    // Tests

    @Test
    public void shouldStartDeployedService() {
        // Given
        ssh.printCommand("deploy_clean");
        ssh.printCommand("deploy fatjar:mvn:com.github.zed-platform/zed-shell/0.0.9/war");

        // When
        ssh.printCommand("deploy_start_all");

        // Then
        List<String> deploy_list = ssh.command("deploy_list");
        assertTrue(deploy_list.get(2).contains("true"));
    }

}