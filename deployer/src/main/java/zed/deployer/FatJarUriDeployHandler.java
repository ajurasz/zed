package zed.deployer;

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
        String mavenCoordinatesUri = deploymentDescriptor.uri().replaceFirst("fatjar:mvn:", "");
        String[] mavenCoordinates = mavenCoordinatesUri.split("/");
        if (mavenCoordinates.length < 3) {
            throw new IllegalArgumentException(mavenCoordinatesUri + " is not a valid Maven artifact URI. Proper URI format is fatjar:mvn:groupId/artifactId/version/[type] .");
        }
        String artifactType = mavenCoordinates.length == 4 ? mavenCoordinates[3] : "jar";
        InputStream in = mavenRepositoryResolver.artifactStream(mavenCoordinates[0].replaceAll("\\.", "/"), mavenCoordinates[1].replaceAll("\\.", "/"), mavenCoordinates[2], artifactType);
        File deployDirectory = new File(zedHome.deployDirectory(), mavenCoordinates[1] + "-" + mavenCoordinates[2] + "." + artifactType);
        try {
            IOUtils.copy(in, new FileOutputStream(deployDirectory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}