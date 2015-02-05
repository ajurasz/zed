package zed.ssh.client;

public interface SshClientOutputCollector {

    void collect(String line);

}
