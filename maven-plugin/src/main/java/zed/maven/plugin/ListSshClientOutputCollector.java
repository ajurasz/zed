package zed.maven.plugin;

import com.google.common.collect.ImmutableList;

import java.util.LinkedList;
import java.util.List;

public class ListSshClientOutputCollector implements SshClientOutputCollector {

    private final List<String> lines = new LinkedList();

    @Override
    public void collect(String line) {
        lines.add(line);
    }

    public List<String> lines() {
        return ImmutableList.copyOf(lines);
    }

}