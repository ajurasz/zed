package zed.shell;

import com.jcraft.jsch.JSchException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShellConfiguration.class)
@IntegrationTest
public class DeployListCommandTest extends Assert {

    SshClient ssh = new SshClient("localhost", 2000);

    // Tests

    @Before
    public void before() {
        ssh.printCommand("deploy_clean");
        ssh.printCommand("deploy fatjar:mvn:com.google.guava/guava/18.0");
    }

    @Test
    public void shouldPrintHeader() throws JSchException, IOException {
        // When
        List<String> output = ssh.command("deploy_list");

        // Then
        assertEquals(3, output.size());
        assertEquals("Deployments:", output.get(0));
    }

    @Test
    public void shouldPrintDetailsHeader() throws JSchException, IOException {
        // When
        List<String> output = ssh.command("deploy_list");

        // Then
        assertEquals(3, output.size());
        assertEquals("[ID]\t[PID]\t[Running]\t[URI]", output.get(1));
    }

    @Test
    public void shouldPrintDeployedFatJar() throws JSchException, IOException {
        // When
        List<String> output = ssh.command("deploy_list");

        // Then
        assertEquals(3, output.size());
        assertTrue(output.get(2).endsWith("fatjar:mvn:com.google.guava/guava/18.0"));
    }

}