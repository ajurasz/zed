package zed.service.geo.google.routing

import org.apache.camel.builder.RouteBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import static org.apache.camel.model.rest.RestBindingMode.json
import static zed.org.apache.camel.rest.annotations.RestAnnotationsExposer.exposeAnnotatedBeans

@Component
class RestApi extends RouteBuilder {

    // Configuration members

    @Value('${zed.service.api.port:15001}')
    private int restPort;

    @Value('${zed.service.api.cors:true}')
    private boolean enableCors;

    // Routes

    @Override
    void configure() throws Exception {
        // REST API facade

        restConfiguration().component("netty-http").
                host("0.0.0.0").port(restPort).bindingMode(json).enableCORS(enableCors)

        exposeAnnotatedBeans(this)

    }

}
