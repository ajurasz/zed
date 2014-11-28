package zed.shell;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import zed.deployer.LocalFileSystemZedHome;
import zed.deployer.ZedHome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShellConfiguration.class)
@IntegrationTest
public class DeployCommandTest {

    ZedHome zedHome = new LocalFileSystemZedHome();

    @Test
    public void xxx() throws JSchException, IOException {
        executeCommand("deploy_clean");

        String command = "deploy fatjar:mvn:com.google.guava/guava/18.0";
        executeCommand(command);
        Assert.assertTrue(Arrays.asList(zedHome.deployDirectory().list()).contains("guava-18.0.jar"));

    }

    @Test
    public void shouldCleanDeployed() throws JSchException, IOException {
        executeCommand("deploy_clean");
        String command = "deploy fatjar:mvn:com.google.guava/guava/18.0";
        executeCommand(command);

        executeCommand("deploy_clean");

        Assert.assertEquals(0, zedHome.deployDirectory().list().length);
    }


    protected void executeCommand(String command) {
        Session session = null;
        Channel channel = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession("zed", "localhost", 2000);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setPassword("zed");
            session.connect();

            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);


            BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            String msg = null;
            while ((msg = in.readLine()) != null) {
                System.out.println(msg);
            }


            channel.disconnect();
            session.disconnect();
        } catch (JSchException jsche) {
            throw new RuntimeException(jsche);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        } finally {
            channel.disconnect();
            session.disconnect();
        }
    }

}