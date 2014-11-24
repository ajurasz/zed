package zed.quickstarts.fuse61.blueprint.camel.dozer;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class XxxIT extends Assert {

    @Test
    public void should() throws InterruptedException, IOException {
        InputStream is = new URL("http://localhost:18081").openStream();
        String output = IOUtils.toString(is).trim();
        assertEquals("someValue", output);
    }

}
