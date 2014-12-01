package zed.shell;

import com.jcraft.jsch.JSchException;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import zed.deployer.LocalFileSystemZedHome;
import zed.deployer.ZedHome;

import java.io.IOException;
import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShellConfiguration.class)
@IntegrationTest
public class DeployCommandTest {

    ZedHome zedHome = new LocalFileSystemZedHome();

    SshClient ssh = new SshClient("localhost", 2000);

    // Tests

    @Test
    public void shouldDeployGuavaJar() {
        ssh.printCommand("deploy_clean");

        String command = "deploy fatjar:mvn:com.google.guava/guava/18.0";
        ssh.printCommand(command);
        Assert.assertTrue(Arrays.asList(zedHome.deployDirectory().list()).contains("guava-18.0.jar"));

    }

    @Test
    public void shouldCleanDeployed() throws JSchException, IOException {
        ssh.printCommand("deploy_clean");
        String command = "deploy fatjar:mvn:com.google.guava/guava/18.0";
        ssh.printCommand(command);

        ssh.printCommand("deploy_clean");

        Assert.assertEquals(0, zedHome.deployDirectory().list().length);
    }




}