package zed.quickstarts.fuse61.blueprint.camel.dozer;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;

public abstract class MicoserviceTest extends Assert {

    public static String waitForHttpQuery(final String url) {
        try {
            final URL urlWrapper = new URL(url);
            await().atMost(60, SECONDS).until(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    try {
                        urlWrapper.openStream();
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }
            });
            return IOUtils.toString(urlWrapper).trim();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
