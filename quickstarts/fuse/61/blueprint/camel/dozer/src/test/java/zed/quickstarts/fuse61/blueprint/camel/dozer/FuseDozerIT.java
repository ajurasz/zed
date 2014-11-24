package zed.quickstarts.fuse61.blueprint.camel.dozer;

import org.junit.Test;

import java.io.IOException;

public class FuseDozerIT extends MicoserviceTest {

    @Test
    public void shouldConvertPojo() throws InterruptedException, IOException {
        String conversionOutput = waitForHttpQuery("http://localhost:18081");
        assertEquals("someValue", conversionOutput);
    }

}
