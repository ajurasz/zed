package zed.mavenrepo;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
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
import java.io.InputStream;
import java.util.List;

import static com.google.common.collect.Lists.transform;

public class JcabiMavenArtifactResolver extends ConfigurableMavenArtifactResolver {

    private final Aether aether;

    public JcabiMavenArtifactResolver(List<Repository> repositories) {
        super(repositories);
        aether = initializeAether();
    }

    public JcabiMavenArtifactResolver() {
        super();
        aether = initializeAether();
    }

    private Aether initializeAether() {
        return new Aether(transform(repositories, new Function<Repository, RemoteRepository>() {
            @Override
            public RemoteRepository apply(Repository repository) {
                return new RemoteRepository(repository.id(), "default", repository.url());
            }
        }), new File(System.getProperty("user.home") + "/.m2/repository"));
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