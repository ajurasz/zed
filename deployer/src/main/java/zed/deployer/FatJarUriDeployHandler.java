package zed.deployer;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class FatJarUriDeployHandler implements UriDeployHandler {

    private final ZedHome zedHome;

    public FatJarUriDeployHandler(ZedHome zedHome) {
        this.zedHome = zedHome;
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith("fatjar:");
    }

    @Override
    public void deploy(DeploymentDescriptor deploymentDescriptor) {
        String uri = deploymentDescriptor.uri();
        uri = uri.replaceFirst("fatjar:mvn:", "");
        String[] mavenCoordinates = uri.split("/");
        String in = System.getProperty("user.home") + "/.m2/repository/" + mavenCoordinates[0].replaceAll("\\.", "/") + "/" + mavenCoordinates[1].replaceAll("\\.", "/") + "/" + mavenCoordinates[2] + "/" + mavenCoordinates[1] + "-" + mavenCoordinates[2] + ".jar";
        File deployDirectory = new File(zedHome.deployDirectory(), mavenCoordinates[1] + "-" + mavenCoordinates[2] + ".jar");
        try {
            Files.copy(new File(in), deployDirectory);
            Files.write(deploymentDescriptor.uri().getBytes(), new File(zedHome.deployDirectory(), deploymentDescriptor.id() + ".deploy"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}