package zed.shell;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class SshClient {

    private final String host;

    private final int port;

    public SshClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public List<String> executeCommandWithOutput(String command) {
        Session session = null;
        Channel channel = null;
        List<String> result = new LinkedList<>();

        try {
            JSch jsch = new JSch();
            session = jsch.getSession("zed", "localhost", 2000);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setConfig("PreferredAuthentications", "password");
            session.setPassword("zed");
            session.connect();

            channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);


            BufferedReader in = new BufferedReader(new InputStreamReader(channel.getInputStream()));
            channel.connect();

            String msg = null;
            while ((msg = in.readLine()) != null) {
                result.add(msg);
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
        return result;
    }

    public void executeCommand(String command) {
        Session session = null;
        Channel channel = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession("zed", "localhost", 2000);
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig("PreferredAuthentications", "password");
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
