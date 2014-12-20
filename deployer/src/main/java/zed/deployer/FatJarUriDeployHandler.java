package zed.deployer;

import org.apache.commons.io.IOUtils;
import zed.deployer.manager.DeploymentDescriptor;
import zed.mavenrepo.JcabiMavenArtifactResolver;
import zed.mavenrepo.MavenArtifactResolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FatJarUriDeployHandler implements UriDeployHandler {

    private final File workspace;

    private final MavenArtifactResolver mavenArtifactResolver = new JcabiMavenArtifactResolver();

    public FatJarUriDeployHandler(File workspace) {
        this.workspace = workspace;
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
        InputStream in = mavenArtifactResolver.artifactStream(mavenCoordinates[0].replaceAll("\\.", "/"), mavenCoordinates[1].replaceAll("\\.", "/"), mavenCoordinates[2], artifactType);
        File deployDirectory = new File(workspace, mavenCoordinates[1] + "-" + mavenCoordinates[2] + "." + artifactType);
        try {
            IOUtils.copy(in, new FileOutputStream(deployDirectory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}