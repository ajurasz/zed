package zed.mavenrepo;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.jcabi.aether.Aether;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyResolutionException;
import org.sonatype.aether.util.artifact.DefaultArtifact;
import org.sonatype.aether.util.artifact.JavaScopes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class JcabiMavenRepositoryResolver implements MavenRepositoryResolver {

    public static void main(String[] args) throws IOException {
        new JcabiMavenRepositoryResolver().artifactStream("org.apache.camel", "camel-guava-eventbus", "2.14.0", "jar").close();
    }

    private final Aether aether;

    public JcabiMavenRepositoryResolver(List<RemoteRepository> remoteRepositories) {
        aether = new Aether(ImmutableList.copyOf(remoteRepositories), new File(System.getProperty("user.home") + "/.m2/repository"));
    }

    public JcabiMavenRepositoryResolver() {
        this(Arrays.asList(new RemoteRepository("mavenCentral", "default", "https://repo1.maven.org/maven2")));
    }

    @Override
    public InputStream artifactStream(String groupId, String artifactId, String version, String extension) {
        try {
            List<Artifact> artifactWithDependencies = aether.resolve(
                    new DefaultArtifact(groupId, artifactId, "", extension, version),
                    JavaScopes.RUNTIME);
            List<Artifact> mainArtifacts = Lists.newArrayList(Iterables.filter(artifactWithDependencies, new Predicate<Artifact>() {
                @Override
                public boolean apply(Artifact input) {
                    return input.getArtifactId().equals(artifactId) &&
                            input.getGroupId().equals(groupId) &&
                            input.getVersion().equals(version);
                }
            }));
            if (mainArtifacts.size() > 1) {
                throw new RuntimeException("More than single main artifacts found: " + mainArtifacts);
            }
            return new FileInputStream(mainArtifacts.get(0).getFile());
        } catch (DependencyResolutionException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}