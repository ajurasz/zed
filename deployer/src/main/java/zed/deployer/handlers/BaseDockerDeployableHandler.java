package zed.deployer.handlers;

import com.github.dockerjava.api.DockerClient;
import org.apache.commons.io.IOUtils;
import zed.deployer.manager.DeploymentDescriptor;

import java.io.IOException;
import java.io.InputStream;

public class BaseDockerDeployableHandler implements DeployableHandler {

    private static final String URI_PREFIX = "docker:";

    private final DockerClient docker;

    public BaseDockerDeployableHandler(DockerClient docker) {
        this.docker = docker;
    }

    protected DockerClient docker() {
        return this.docker;
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith(URI_PREFIX);
    }

    @Override
    public void deploy(DeploymentDescriptor deploymentDescriptor) {
        String[] dockerUri = deploymentDescriptor.uri().split(":");
        if (dockerUri.length < 2) {
            throw new IllegalArgumentException(deploymentDescriptor.uri() + " is not a valid docker deploy URI. Proper URI format is docker:imagerepoprefix/image[:tag] .");
        }

        InputStream inputStream = null;
        if (dockerUri.length == 3) {
            inputStream = docker.pullImageCmd(dockerUri[1]).withTag(dockerUri[2]).exec();
        } else {
            inputStream = docker.pullImageCmd(dockerUri[1]).exec();
        }

        asString(inputStream);
    }

    protected String asString(InputStream inputStream) {
        try {
            return IOUtils.toString(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
