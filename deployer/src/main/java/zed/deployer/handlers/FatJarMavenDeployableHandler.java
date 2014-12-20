package zed.deployer.handlers;

import org.apache.commons.io.IOUtils;
import zed.deployer.manager.DeploymentDescriptor;
import zed.mavenrepo.JcabiMavenArtifactResolver;
import zed.mavenrepo.MavenArtifactResolver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FatJarMavenDeployableHandler implements DeployableHandler {

    private final static String URI_PREFIX = "fatjar:mvn:";

    private final File workspace;

    private final MavenArtifactResolver mavenArtifactResolver;

    public FatJarMavenDeployableHandler(File workspace, MavenArtifactResolver mavenArtifactResolver) {
        this.workspace = workspace;
        this.mavenArtifactResolver = mavenArtifactResolver;
    }

    public FatJarMavenDeployableHandler(File workspace) {
        this(workspace, new JcabiMavenArtifactResolver());
    }

    @Override
    public boolean supports(String uri) {
        return uri.startsWith(URI_PREFIX);
    }

    @Override
    public void deploy(DeploymentDescriptor deploymentDescriptor) {
        try {
            String mavenCoordinatesUri = deploymentDescriptor.uri().substring(URI_PREFIX.length());
            String[] mavenCoordinates = mavenCoordinatesUri.split("/");
            if (mavenCoordinates.length < 3) {
                throw new IllegalArgumentException(mavenCoordinatesUri + " is not a valid Maven artifact URI. Proper URI format is fatjar:mvn:groupId/artifactId/version/[type] .");
            }
            String artifactType = mavenCoordinates.length == 4 ? mavenCoordinates[3] : "jar";
            InputStream artifactData = mavenArtifactResolver.artifactStream(mavenCoordinates[0].replaceAll("\\.", "/"), mavenCoordinates[1].replaceAll("\\.", "/"), mavenCoordinates[2], artifactType);
            File deployDirectory = new File(workspace, mavenCoordinates[1] + "-" + mavenCoordinates[2] + "." + artifactType);
            IOUtils.copy(artifactData, new FileOutputStream(deployDirectory));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}