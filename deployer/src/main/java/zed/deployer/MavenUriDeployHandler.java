package zed.deployer;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class MavenUriDeployHandler implements UriDeployHandler {

    private final ZedHome zedHome;

    public MavenUriDeployHandler(ZedHome zedHome) {
        this.zedHome = zedHome;
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith("mvn:");
    }

    @Override
    public void deploy(String uri) {
        uri = uri.replaceFirst("mvn:", "");
        String[] mavenCoordinates = uri.split("/");
        String in = System.getProperty("user.home") + "/.m2/repository/" + mavenCoordinates[0].replaceAll("\\.", "/") + "/" + mavenCoordinates[1].replaceAll("\\.", "/") + "/" + mavenCoordinates[2] + "/" + mavenCoordinates[1] + "-" + mavenCoordinates[2] + ".jar";
        File deployDirectory = new File(zedHome.deployDirectory(), mavenCoordinates[1] + "-" + mavenCoordinates[2] + ".jar");
        try {
            Files.copy(new File(in), deployDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}