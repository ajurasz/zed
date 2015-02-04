package zed.dockerunit;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.VolumeBind;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class VolumeDockerInspectMatcher extends TypeSafeMatcher<VolumeBind[]> {

    private final String volume;

    private final String boundTo;

    public VolumeDockerInspectMatcher(String volume, String boundTo) {
        this.volume = volume;
        this.boundTo = boundTo;
    }

    @Factory
    public static <T> Matcher<VolumeBind[]> hasVolume(String containerPath, String hostPath) {
        return new VolumeDockerInspectMatcher(containerPath, hostPath);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(String.format("Expected to find the following volume binding: [%s:%s]", volume, boundTo));
    }

    @Override
    protected boolean matchesSafely(VolumeBind[] volumeBinds) {
        for (VolumeBind v : volumeBinds) {
            if (v.getContainerPath().equals(volume) && v.getHostPath().equals(boundTo)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void describeMismatchSafely(VolumeBind[] item, Description mismatchDescription) {
        String volumes = "";
        for (VolumeBind bind : item) {
            volumes += String.format("[%s:%s] ", bind.getContainerPath(), bind.getHostPath());
        }
        mismatchDescription.appendText("was " + volumes);
    }

    public static VolumeBind[] volumeBinds(DockerClient docker, String pid) {
        return docker.inspectContainerCmd(pid).exec().getVolumes();
    }

}