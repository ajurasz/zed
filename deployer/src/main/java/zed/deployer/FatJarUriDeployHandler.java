package zed.deployer;

import com.google.common.io.Files;
import org.apache.commons.io.IOUtils;
import zed.mavenrepo.JcabiMavenRepositoryResolver;
import zed.mavenrepo.MavenRepositoryResolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FatJarUriDeployHandler implements UriDeployHandler {

    private final ZedHome zedHome;

    private final MavenRepositoryResolver mavenRepositoryResolver = new JcabiMavenRepositoryResolver();

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
        InputStream in = mavenRepositoryResolver.artifactStream(mavenCoordinates[0].replaceAll("\\.", "/"), mavenCoordinates[1].replaceAll("\\.", "/"), mavenCoordinates[2], "jar");
        File deployDirectory = new File(zedHome.deployDirectory(), mavenCoordinates[1] + "-" + mavenCoordinates[2] + ".jar");
        try {
            IOUtils.copy(in, new FileOutputStream(deployDirectory));
            Files.write(deploymentDescriptor.uri().getBytes(), new File(zedHome.deployDirectory(), deploymentDescriptor.id() + ".deploy"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}