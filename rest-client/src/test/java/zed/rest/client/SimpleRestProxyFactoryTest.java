package zed.rest.client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import static org.springframework.util.SocketUtils.findAvailableTcpPort;
import static zed.rest.client.Header.header;

public class SimpleRestProxyFactoryTest extends Assert {

    int port = findAvailableTcpPort();

    String baseServiceUri = "http://localhost:" + port + "/api/";

    RestProxy<FooService> fooServiceRestProxy = new SimpleRestProxyFactory().
            proxyService(FooService.class, baseServiceUri);

    int arg = 666;

    @Before
    public void before() {
        ClientAndServer server = new ClientAndServer(port);
        server.when(HttpRequest.request().withPath("/api/fooService/getString/666").withHeader(org.mockserver.model.Header.header("headerKey", "headerValue"))).respond(HttpResponse.response().withBody("withHeader"));
        server.when(HttpRequest.request().withPath("/api/fooService/getString/666")).respond(HttpResponse.response().withBody("returnValue"));
        server.when(HttpRequest.request().withPath("/api/fooService/getString").withHeader(org.mockserver.model.Header.header("headerKey", "headerValue"))).respond(HttpResponse.response().withBody("postedWithHeader"));
        server.when(HttpRequest.request().withPath("/api/fooService/getString")).respond(HttpResponse.response().withBody("posted"));
        server.when(HttpRequest.request().withPath("//api/fooService/getString")).respond(HttpResponse.response().withBody("doubleSlash"));
        server.when(HttpRequest.request().withPath("/api/fooService/voidMethod/666")).respond(HttpResponse.response().withBody("valueToBeIgnored"));
        server.when(HttpRequest.request().withPath("/api/fooService/parameterless")).respond(HttpResponse.response().withBody("parameterless"));
    }

    @Test
    public void shouldGenerateGetRequest() {
        // When
        String response = fooServiceRestProxy.get().getString(arg);

        // Then
        assertEquals("returnValue", response);
    }

    @Test
    public void shouldGenerateGetRequestWithHeader() {
        // When
        String response = fooServiceRestProxy.get(header("headerKey", "headerValue")).getString(arg);

        // Then
        assertEquals("withHeader", response);
    }

    @Test
    public void shouldGeneratePostRequest() {
        // When
        String response = fooServiceRestProxy.post().getString(arg);

        // Then
        assertEquals("posted", response);
    }

    @Test
    public void shouldGeneratePostRequestWithHeader() {
        // When
        String response = fooServiceRestProxy.post(header("headerKey", "headerValue")).getString(arg);

        // Then
        assertEquals("postedWithHeader", response);
    }

    @Test
    public void shouldRemoveExtraSlashFromBaseServiceUri() {
        // When
        String response = fooServiceRestProxy.post().getString(arg);

        // Then
        assertNotEquals("doubleSlash", response);
    }

    @Test
    public void shouldHandleVoidMethod() {
        fooServiceRestProxy.get().voidMethod(arg);
    }

    @Test
    public void shouldHandleParameterlessOperation() {
        // When
        String response = fooServiceRestProxy.get().parameterless();

        // Then
        assertEquals("parameterless", response);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotHandlePostedParameterlessOperation() {
        // When
        fooServiceRestProxy.post().parameterless();
    }

}
