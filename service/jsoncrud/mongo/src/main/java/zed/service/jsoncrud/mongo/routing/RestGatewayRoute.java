package zed.service.jsoncrud.mongo.routing;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class RestGatewayRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("direct:test").to("direct:test");
    }

}
