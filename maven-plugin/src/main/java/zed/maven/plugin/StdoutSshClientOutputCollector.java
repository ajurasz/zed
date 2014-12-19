package zed.maven.plugin;

public class StdoutSshClientOutputCollector implements SshClientOutputCollector {

    @Override
    public void collect(String line) {
        System.out.println(line);
    }

}
