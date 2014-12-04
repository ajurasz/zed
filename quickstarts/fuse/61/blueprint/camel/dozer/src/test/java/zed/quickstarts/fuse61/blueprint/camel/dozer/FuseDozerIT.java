package zed.quickstarts.fuse61.blueprint.camel.dozer;

import org.junit.BeforeClass;
import org.junit.Test;
import zed.dockerunit.BaseDockerTest;

import static zed.quickstarts.fuse61.blueprint.camel.dozer.MicoserviceTest.waitForHttpQuery;

public class FuseDozerIT extends BaseDockerTest {

    @BeforeClass
    public static void beforeClass() {
        initializeDocker("1.14");
        run("com.github.zed-platform-zed-quickstarts-fuse-61-blueprint-camel-dozer", 18081, 18082);
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
