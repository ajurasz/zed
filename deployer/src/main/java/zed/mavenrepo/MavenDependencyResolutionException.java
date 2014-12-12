package zed.mavenrepo;

import org.sonatype.aether.resolution.DependencyResolutionException;

public class MavenDependencyResolutionException extends RuntimeException {

    public MavenDependencyResolutionException(DependencyResolutionException cause) {
        super(cause);
    }

    @Override
    public synchronized DependencyResolutionException getCause() {
        return (DependencyResolutionException) super.getCause();
    }

}