package zed.quickstarts.fuse61.blueprint.camel.dozer;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class FuseDozerIT extends MicoserviceTest {

    static DockerClient docker;
    static String containerId;

    String xxx;

    @BeforeClass
    public static void beforeClass() {
        DockerClientConfig config = DockerClientConfig.createDefaultConfigBuilder()
                .withVersion("1.14").build();
        docker = DockerClientBuilder.getInstance(config).build();

        containerId = docker.createContainerCmd("com.github.zed-platform-zed-quickstarts-fuse-61-blueprint-camel-dozer").exec().getId();
        docker.startContainerCmd(containerId).withPortBindings(new PortBinding(new Ports.Binding(18081), new ExposedPort(18081)), new PortBinding(new Ports.Binding(18082), new ExposedPort(18082))).exec();
    }

    @AfterClass
    public static void afterClass() {
        docker.stopContainerCmd(containerId).exec();
    }

    @Test
    public void shouldConvertPojo() {
        String conversionOutput = waitForHttpQuery("http://localhost:18081");
        assertEquals("mappedByDozerTypeConverter", conversionOutput);
    }

    @Test
    public void shouldAccessDozerMapper() {
        String conversionOutput = waitForHttpQuery("http://localhost:18082");
        assertEquals("mappedByDozerBeanMapper", conversionOutput);
    }

}
