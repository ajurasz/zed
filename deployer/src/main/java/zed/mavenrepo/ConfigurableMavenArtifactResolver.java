package zed.mavenrepo;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static zed.mavenrepo.Repository.mavenCentral;

public abstract class ConfigurableMavenArtifactResolver implements MavenArtifactResolver {

    protected final List<Repository> repositories;

    public ConfigurableMavenArtifactResolver(List<Repository> repositories) {
        this.repositories = ImmutableList.copyOf(repositories);
    }

    public ConfigurableMavenArtifactResolver() {
        this.repositories = ImmutableList.of(mavenCentral());
    }

}
