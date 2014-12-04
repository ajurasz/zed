package zed.dockerunit;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DockerClientConfig.DockerClientConfigBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.github.dockerjava.core.DockerClientBuilder.getInstance;
import static com.github.dockerjava.core.DockerClientConfig.createDefaultConfigBuilder;
import static com.google.common.collect.Sets.newConcurrentHashSet;
import static java.lang.Runtime.getRuntime;

public class DockerTester {

    private final DockerClient docker;

    private Set<String> startedContainers = newConcurrentHashSet();

    public DockerTester(DockerClient docker) {
        this.docker = docker;
    }

    public static DockerTester dockerTester(String apiVersion) {
        DockerClientConfigBuilder config = createDefaultConfigBuilder();
        if (apiVersion != null) {
            config.withVersion(apiVersion);
        }
        DockerClient docker = getInstance(config.build()).build();
        return new DockerTester(docker);
    }

    public static DockerTester dockerTester() {
        return dockerTester(null);
    }

    public String run(String image, int... ports) {
        String containerId = docker.createContainerCmd(image).exec().getId();
        List<PortBinding> portBindings = new ArrayList<>();
        if (ports != null) {
            for (int port : ports) {
                portBindings.add(new PortBinding(new Ports.Binding(port), new ExposedPort(port)));
            }
        }
        docker.startContainerCmd(containerId).withPortBindings(portBindings.toArray(new PortBinding[portBindings.size()])).exec();
        startedContainers.add(containerId);
        registerShutdown(containerId);
        return containerId;
    }

    public void stop(String containerId) {
        docker.stopContainerCmd(containerId).exec();
        startedContainers.remove(containerId);
    }

    public void stopAll() {
        for (String containerId : startedContainers) {
            stop(containerId);
        }
    }

    private void registerShutdown(String containerId) {
        getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                DockerTester.this.stop(containerId);
            }
        });
    }

    public DockerClient docker() {
        return docker;
    }

}