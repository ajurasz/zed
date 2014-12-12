package zed.mavenrepo;

import com.google.common.collect.ImmutableList;

import java.util.List;

import static zed.mavenrepo.Repository.mavenCentral;

public abstract class ConfigurableMavenRepositoryResolver implements MavenRepositoryResolver {

    protected final List<Repository> repositories;

    public ConfigurableMavenRepositoryResolver(List<Repository> repositories) {
        this.repositories = ImmutableList.copyOf(repositories);
    }

    public ConfigurableMavenRepositoryResolver() {
        this.repositories = ImmutableList.of(mavenCentral());
    }

}
