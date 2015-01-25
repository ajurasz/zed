package zed.deployer.handlers;

import com.github.dockerjava.api.DockerClient;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.List;

public final class DeployableHandlers {

    private DeployableHandlers() {
    }

    public static List<DeployableHandler> allDeployableHandlers(File workspace, DockerClient docker) {
        return ImmutableList.of(new FatJarMavenDeployableHandler(workspace), new BaseDockerDeployableHandler(docker));
    }

}