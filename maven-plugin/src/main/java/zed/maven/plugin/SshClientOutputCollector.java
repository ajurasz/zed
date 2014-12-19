package zed.maven.plugin;

public interface SshClientOutputCollector {

    void collect(String line);

}
