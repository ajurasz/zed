package zed.camel.kura;

import org.apache.camel.CamelContext;
import org.apache.camel.ServiceStatus;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.BundleContext;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;

public class KuraRouterTest extends Assert {

    TestKuraRouter router = new TestKuraRouter();

    BundleContext bundleContext = mock(BundleContext.class, RETURNS_DEEP_STUBS);

    @Before
    public void before() throws Exception {
        given(bundleContext.getBundle().getVersion().toString()).willReturn("version");

        router.start(bundleContext);
    }

    @After
    public void after() throws Exception {
        router.start(bundleContext);
    }

    @Test
    public void shouldCloseCamelContext() throws Exception {
        // When
        router.stop(bundleContext);

        // Then
        assertEquals(ServiceStatus.Stopped, router.camelContext.getStatus());
    }

    @Test
    public void shouldStartCamelContext() throws Exception {
        // Given
        String message = "foo";
        MockEndpoint mockEndpoint = router.camelContext.getEndpoint("mock:test", MockEndpoint.class);
        mockEndpoint.expectedBodiesReceived(message);

        // When
        router.camelContext.createProducerTemplate().sendBody("direct:start", message);

        // Then
        mockEndpoint.assertIsSatisfied();
    }

}

class TestKuraRouter extends KuraRouter {

    @Override
    public void configure() throws Exception {
        from("direct:start").to("mock:test");
    }

    @Override
    protected CamelContext createCamelContext() {
        return new DefaultCamelContext();
    }

}