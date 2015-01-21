package zed.org.apache.camel.rest.annotations;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.JndiRegistry;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.test.AvailablePortFinder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;

import static zed.org.apache.camel.rest.annotations.RestAnnotationsExposer.exposeAnnotatedBeans;

public class RestAnnotationsTest extends CamelTestSupport {

    static int port = AvailablePortFinder.getNextAvailable();

    String baseUrl = String.format("http://localhost:%s/someName/", port);

    @Test
    public void shouldHandleMethodWithStringArguments() throws IOException {
        String response = IOUtils.toString(new URL(baseUrl + "someOperation/foo/bar"));
        assertEquals("\"foobar\"", response);
    }

    @Test
    public void shouldHandleMethodWithTwoNonStringTypes() throws IOException {
        String response = IOUtils.toString(new URL(baseUrl + "operationWithDifferentTypes/2/2.0"));
        assertEquals("\"4.0\"", response);
    }

    @Test
    public void shouldHandleMethodReturningInteger() throws IOException {
        String response = IOUtils.toString(new URL(baseUrl + "operationReturningInteger/10"));
        assertEquals("10", response);
    }

    @Test
    public void shouldHandleMethodReturningPojoWithValue() throws IOException {
        String response = IOUtils.toString(new URL(baseUrl + "operationReturningPojoWithValue/10"));
        assertEquals("{\"value\":\"10\"}", response);
    }

    @Test
    public void shouldHandleVoidMethod() throws IOException {
        String response = IOUtils.toString(new URL(baseUrl + "voidOperation/10"));
        assertEquals("\"\"", response);
    }

    @Test
    public void shouldHandleNoArgumentsMethod() throws IOException {
        String response = IOUtils.toString(new URL(baseUrl + "noArguments"));
        assertEquals("\"noArguments\"", response);
    }

    @Override
    protected RouteBuilder createRouteBuilder() throws Exception {
        return new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                restConfiguration().component("netty-http").host("0.0.0.0").port(port).bindingMode(RestBindingMode.json);
                exposeAnnotatedBeans(this);
            }
        };
    }

    @Override
    protected JndiRegistry createRegistry() throws Exception {
        JndiRegistry registry = new JndiRegistry();
        registry.bind("someName", new DefaultBeanToExpose());
        return registry;
    }

}