package zed.quickstarts.fuse61.blueprint.camel.dozer;

import org.junit.Test;

public class FuseDozerIT extends MicoserviceTest {

    @Test
    public void shouldConvertPojo() {
        String conversionOutput = waitForHttpQuery("http://localhost:18081");
        assertEquals("someValue", conversionOutput);
    }

    @Test
    public void shouldAccessDozerMapper() {
        String conversionOutput = waitForHttpQuery("http://localhost:18082");
        assertEquals("someValue", conversionOutput);
    }

}
