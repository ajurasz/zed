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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ShellConfiguration.class)
@IntegrationTest
public class DeployListCommandTest extends Assert {

    @Test
    public void shouldPrintHeader() throws JSchException, IOException {
        // Given
        executeCommand("deploy_clean");
        String command = "deploy fatjar:mvn:com.google.guava/guava/18.0";
        executeCommand(command);

        // When
        List<String> output = executeCommand("deploy_list");

        // Then
        assertEquals(2, output.size());
        assertEquals("Deployments:", output.get(0));
    }

    @Test
    public void shouldPrintDeployedFatJar() throws JSchException, IOException {
        // Given
        executeCommand("deploy_clean");
        String command = "deploy fatjar:mvn:com.google.guava/guava/18.0";
        executeCommand(command);

        // When
        List<String> output = executeCommand("deploy_list");

        // Then
        assertEquals(2, output.size());
        assertTrue(output.get(1).endsWith("fatjar:mvn:com.google.guava/guava/18.0"));
    }

    protected List<String> executeCommand(String command) {
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
            List<String> out = new LinkedList<>();
            while ((msg = in.readLine()) != null) {
                out.add(msg);
            }


            channel.disconnect();
            session.disconnect();
            return out;
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