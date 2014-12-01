package zed.shell;

public class StdoutSshClientOutputCollector implements SshClientOutputCollector {

    @Override
    public void collect(String line) {
        System.out.println(line);
    }

}
