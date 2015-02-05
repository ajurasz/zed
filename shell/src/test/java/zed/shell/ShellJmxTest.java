package zed.shell;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.net.URL;

import static org.springframework.util.SocketUtils.findAvailableTcpPort;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {ShellConfiguration.class, ShellJmxTest.class})
@IntegrationTest("spring.jmx.enabled=true")
@WebAppConfiguration
public class ShellJmxTest extends Assert {

    static int port = findAvailableTcpPort();

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("shell.ssh.port", findAvailableTcpPort() + "");
        System.setProperty("server.port", port + "");

    }

    // Tests

    @Test
    public void shouldExecuteCommandViaJmx() throws IOException, InterruptedException {
        // Given
        String commandUrl = "http://localhost:" + port + "/jolokia/exec/zed:name=zedShell/invokeCommand/deploy_clean";

        // When
        String commandOutput = IOUtils.toString(new URL(commandUrl));

        // Then
        assertTrue(commandOutput.contains("Deploy directory cleaned"));
    }


}