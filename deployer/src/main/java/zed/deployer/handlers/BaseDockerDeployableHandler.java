package zed.deployer.handlers;

import com.github.dockerjava.api.DockerClient;
import org.apache.commons.io.IOUtils;
import zed.deployer.manager.DeployableDescriptor;
import zed.deployer.util.DockerUriUtil;

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
    public void deploy(DeployableDescriptor deployableDescriptor) {
        String[] dockerUri = DockerUriUtil.imageName(URI_PREFIX, deployableDescriptor.uri()).split(":");
        InputStream inputStream = null;
        if (dockerUri.length == 2) {
            inputStream = docker.pullImageCmd(dockerUri[0]).withTag(dockerUri[1]).exec();
        } else {
            inputStream = docker.pullImageCmd(dockerUri[0]).exec();
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
