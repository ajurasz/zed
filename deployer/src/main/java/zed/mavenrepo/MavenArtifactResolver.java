package zed.mavenrepo;

import java.io.InputStream;

public interface MavenArtifactResolver {

    InputStream artifactStream(String groupId, String artifactId, String version, String type);

}
