package zed.quickstarts.fuse61.blueprint.camel.dozer;

import org.junit.Test;

public class FuseDozerIT extends MicoserviceTest {

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
